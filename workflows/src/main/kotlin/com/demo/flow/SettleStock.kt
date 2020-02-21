package com.demo.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.selection.TokenQueryBy
import com.r3.corda.lib.tokens.selection.database.selector.DatabaseTokenSelection
import com.r3.corda.lib.tokens.workflows.flows.move.*
import com.r3.corda.lib.tokens.workflows.internal.flows.distribution.UpdateDistributionListFlow
import com.r3.corda.lib.tokens.workflows.internal.flows.finality.ObserverAwareFinalityFlow
import com.r3.corda.lib.tokens.workflows.internal.flows.finality.ObserverAwareFinalityFlowHandler
//import com.r3.corda.lib.tokens.workflows.internal.selection.TokenSelection




import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import demo.Stock
import net.corda.core.contracts.Amount
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

import net.corda.core.flows.*
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.unwrap
import java.math.BigDecimal




@CordaSerializable
data class ExecutionNotification(val buyer: Party, val seller: Party,
                                 val settleStockTokenAmount: Amount<TokenType>, val executedPrice: BigDecimal, val settleFiatTokenAmount: Amount<TokenType>)


@StartableByService
@StartableByRPC
@InitiatingFlow
class SettleStock(val buyer: Party, val seller: Party,
                  val symbol: String, val executedQty: Int, val executedPrice: BigDecimal) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {

        //this will be called when a order get matched from matching engine which then start the settlement process

        //get stock by symbol
        val stockPointer: TokenPointer<Stock> = Helper.getStockPointer(serviceHub, symbol)
        val stock = stockPointer.pointer.resolve(serviceHub).state.data

        val buyerSession = initiateFlow(buyer)
        val sellerSession = initiateFlow(seller)

        //calculate the required amount
        val settleStockTokenAmount: Amount<TokenType> = executedQty of stockPointer
        val settleFiatTokenAmount: Amount<TokenType> =  (executedQty.toBigDecimal() * executedPrice) of FiatCurrency.getInstance(stock.currency)

        //************ Dealing with Buyer ******************
        //notify buyer the order detail which then invoke Buyer's SettleStockTokensHandler and wait its reply (input & proposed output)
        buyerSession.send(ExecutionNotification(buyer, seller, settleStockTokenAmount, executedPrice, settleFiatTokenAmount))
        //input and output should be the same except seller is the new owner of the fiat token
        //waiting buyer's SendStateAndRefFlow
        val inputsFiat = subFlow(ReceiveStateAndRefFlow<FungibleToken>(buyerSession))
        //waiting buyers'  to send outputs
        val outputsFiat = buyerSession.receive<List<FungibleToken>>().unwrap { it }


        //************ Dealing with Seller ******************
        //notify seller the order detail which then invoke Seller's SettleStockTokensHandler and wait its reply (input & proposed output)
        sellerSession.send(ExecutionNotification(buyer, seller, settleStockTokenAmount, executedPrice, settleFiatTokenAmount))
        //input and output should be the same except buyer is the new owner of the stock token
        //waiting seller's SendStateAndRefFlow
        val inputsStock = subFlow(ReceiveStateAndRefFlow<FungibleToken>(sellerSession))
        //waiting seller to send outputs
        val outputsStock = sellerSession.receive<List<FungibleToken>>().unwrap { it }


        //************ Start Transaction ******************
        //at this point, exchange has all input and output tokens from buyer and seller.
        //And it is ready to make a transaction to move the tokens
        val txBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        addMoveTokens(txBuilder, inputsFiat, outputsFiat)
        addMoveTokens(txBuilder, inputsStock, outputsStock)


        //sign txn and collect signatures from buyer and seller
        val initialStx = serviceHub.signInitialTransaction(txBuilder, ourIdentity.owningKey)
        val stx = subFlow(CollectSignaturesFlow(initialStx, listOf(buyerSession, sellerSession), listOf(ourIdentity.owningKey)))

        //run finality flow to finalize the txn
        return subFlow(ObserverAwareFinalityFlow(stx, listOf(buyerSession, sellerSession)))
    }
}

/**
 * TODO docs
 */
@InitiatedBy(SettleStock::class)
class SettleStockTokensHandler(val otherSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
   // println("testing 3")
    override fun call() {
        val executionNotification = otherSession.receive<ExecutionNotification>().unwrap { it }

        //Buyer
        if (executionNotification.buyer.equals(ourIdentity)) {

            //Use DatabaseTokenSelection to query available fiat token for buyer and put it as input.
            //And it makes a copy to output. The owner of output is seller
            val (inputs, outputs) = DatabaseTokenSelection(serviceHub).generateMove(

                    listOf(Pair(executionNotification.seller, executionNotification.settleFiatTokenAmount)),
                    ourIdentity,
                    lockId = runId.uuid
            )

            //invoke subFlow to send input state ref to exchange
            subFlow(SendStateAndRefFlow(otherSession, inputs))
            //send proposed output state to exchange
            otherSession.send(outputs)


            //waiting exchange's CollectSignaturesFlow
            subFlow(object : SignTransactionFlow(otherSession) {
                override fun checkTransaction(stx: SignedTransaction) {}
            }
            )

            //waiting exchange's ObserverAwareFinalityFlow
            subFlow(ObserverAwareFinalityFlowHandler(otherSession))
        }

        //Seller
        if (executionNotification.seller.equals(ourIdentity)) {

            //Use DatabaseTokenSelection to query available stock token for seller and put it as input.
            //And it makes a copy to output. The owner of Output is buyer
            val (inputs, outputs) =  DatabaseTokenSelection(serviceHub).generateMove(

                    listOf(Pair(executionNotification.buyer, executionNotification.settleStockTokenAmount)),
                    ourIdentity,
                    lockId = runId.uuid

            )
            //invoke subFlow to send input state ref to exchange
            subFlow(SendStateAndRefFlow(otherSession, inputs))
            //send proposed output state to exchange
            otherSession.send(outputs)

            //waiting exchange's CollectSignaturesFlow
            subFlow(object : SignTransactionFlow(otherSession) {
                override fun checkTransaction(stx: SignedTransaction) {}
            }
            )
            //waiting exchange's ObserverAwareFinalityFlow
            subFlow(ObserverAwareFinalityFlowHandler(otherSession))
        }

    }

}