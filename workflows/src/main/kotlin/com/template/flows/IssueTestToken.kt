//package com.template.flows
//
//import co.paralleluniverse.fibers.Suspendable
//import com.r3.corda.lib.tokens.contracts.utilities.issuedBy
//import com.r3.corda.lib.tokens.contracts.utilities.of
//import com.r3.corda.lib.tokens.money.FiatCurrency
//import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlow
//import com.r3.corda.lib.tokens.workflows.flows.issue.IssueTokensFlowHandler
//import com.r3.corda.lib.tokens.workflows.utilities.toParty
//import com.template.contracts.TestContract
//import com.template.states.TestTokenState
//import net.corda.core.contracts.Command
//import net.corda.core.contracts.requireThat
//import net.corda.core.flows.*
//import net.corda.core.identity.AbstractParty
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//import net.corda.core.utilities.ProgressTracker
//
//// *********
//// * Flows *
//// *********
//@InitiatingFlow
//@StartableByRPC
//class IssueTestToken (private val amount: Double,
//                      private val currency: String,
//                      private val issuer: AbstractParty,
//                      private val holder: AbstractParty): FlowLogic<SignedTransaction>() {
//    override val progressTracker = ProgressTracker()
//
//    @Suspendable
//    override fun call(): SignedTransaction {
//        transaction().verify(serviceHub)
//        val signedTx =serviceHub.signInitialTransaction(transaction())
//        val sessions = (outputState().participants - ourIdentity).map { initiateFlow(it.toParty(serviceHub)) }
//        val collectSign = subFlow(CollectSignaturesFlow(signedTx, sessions))
//        val issueToken = IssueTokensFlow(outputState(), sessions)
//        subFlow(issueToken)
//        return(subFlow(FinalityFlow(collectSign, sessions)))
//    }
//
//    private fun outputState(): TestTokenState{
//        val token = FiatCurrency.getInstance(currency)
//        return TestTokenState(
//                amount = amount of token issuedBy issuer.toParty(serviceHub),
//                holder = holder
//        )
//    }
//
//    private fun transaction(): TransactionBuilder{
//        val notary =serviceHub.networkMapCache.notaryIdentities.first()
//        val cmd = Command(TestContract.Commands.Issue(),
//                outputState().participants.map { it.owningKey })
//        return TransactionBuilder(notary)
//                .addOutputState(outputState(), TestContract.TEST_ID)
//                .addCommand(cmd)
//    }
//}
//
//@InitiatedBy(IssueTestToken::class)
//class IssueTestTokensHandler(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val signTransactionFlow = object : SignTransactionFlow(flowSession) {
//            override fun checkTransaction(stx: SignedTransaction) = requireThat {
//            }
//        }
//        val signedTransaction = subFlow(signTransactionFlow)
//        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = signedTransaction.id))
//    }
//}
//
