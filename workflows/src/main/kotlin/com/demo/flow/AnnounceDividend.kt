package com.demo.flow

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.contracts.utilities.of
import com.r3.corda.lib.tokens.money.FiatCurrency
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlow
import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlowHandler
import com.r3.corda.lib.tokens.workflows.flows.move.MoveFungibleTokensFlow
//import com.r3.corda.lib.tokens.workflows.flows.evolvable.UpdateEvolvableTokenFlow
import com.r3.corda.lib.tokens.workflows.flows.move.MoveTokensFlowHandler
import com.r3.corda.lib.tokens.workflows.types.PartyAndAmount
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import demo.Stock
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

import net.corda.core.flows.*
import net.corda.core.transactions.TransactionBuilder
import java.math.BigDecimal
import java.util.*


@StartableByService
@StartableByRPC
@InitiatingFlow
class AnnounceDividend(val symbol: String, val amount: BigDecimal, val exDate: Date, val payDate: Date) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {

        //prepare input
        val stockPointer: TokenPointer<Stock> = Helper.getStockPointer(serviceHub, symbol)
        val stockRefAndState = stockPointer.pointer.resolve(serviceHub)

        //prepare output
        val output = stockRefAndState.state.data.copy(dividend = amount,
                            exDate = exDate, payDate = payDate)

        //observer sessions
        val exchangeSession = initiateFlow(serviceHub.identityService.partiesFromName("Exchange", false).single())
        val citiBankSession = initiateFlow(serviceHub.identityService.partiesFromName("Citi Bank", false).single())
        val jpMorganSession = initiateFlow(serviceHub.identityService.partiesFromName("JP Morgan", false).single())


        //exchange, citi and jp are only observers which don't involve in transaction.
        //The subflow will multi-cast the new stock information to the observers
        //Make sure the observers are online when updating the stock information.
        //However in production, it should not use this approach because we can't make sure all observers online.
        //Instead, it should use 'SendTransactionFlow' to see the stock information when receiving request before market open
        return subFlow(UpdateEvolvableTokenFlow(stockRefAndState, output, emptyList() , listOf(exchangeSession, citiBankSession, jpMorganSession)))

    }


}

@InitiatedBy(AnnounceDividend::class)
class UpdateAnnounceDividend(val otherSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() = subFlow(UpdateEvolvableTokenFlowHandler(otherSession))
}
