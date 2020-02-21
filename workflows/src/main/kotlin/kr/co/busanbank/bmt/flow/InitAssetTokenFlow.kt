//package kr.co.busanbank.bmt.flow
//
//import net.corda.core.contracts.requireThat
//
//import java.math.BigInteger
//import java.security.PublicKey
//import java.util.ArrayList
//
//import com.google.common.collect.ImmutableList
//import com.google.common.collect.ImmutableList.Builder
//import com.google.common.collect.ImmutableSet
//
//import co.paralleluniverse.fibers.Suspendable
//import kr.co.busanbank.bmt.contract.DigitalAssetContract
//import kr.co.busanbank.bmt.state.AssetTokenState
//import kr.co.busanbank.bmt.state.DigitalAssetState
//import net.corda.core.contracts.Command
//import net.corda.core.contracts.ContractState
//import net.corda.core.contracts.StateAndRef
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.crypto.SecureHash
//import net.corda.core.flows.CollectSignaturesFlow
//import net.corda.core.flows.FinalityFlow
//import net.corda.core.flows.FlowException
//import net.corda.core.flows.FlowLogic
//import net.corda.core.flows.FlowSession
//import net.corda.core.flows.InitiatedBy
//import net.corda.core.flows.InitiatingFlow
//import net.corda.core.flows.ReceiveFinalityFlow
//import net.corda.core.flows.SignTransactionFlow
//import net.corda.core.flows.StartableByRPC
//import net.corda.core.identity.AbstractParty
//import net.corda.core.identity.Party
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//import net.corda.core.utilities.ProgressTracker
//import net.corda.core.utilities.ProgressTracker.Step
//
///**
// * This flow allows two parties (the [Initiator] and the [Acceptor]) to come to
// * an agreement about the IOU encapsulated within an [IOUState].
// *
// * In our simple example, the [Acceptor] always accepts a valid IOU.
// *
// * These flows have deliberately been implemented by using only the call()
// * method for ease of understanding. In practice we would recommend splitting up
// * the various stages of the flow into sub-routines.
// *
// * All methods called within the [FlowLogic] sub-class need to be annotated with
// * the @Suspendable annotation.
// */
//class InitAssetTokenFlow {
//
//    @InitiatingFlow
//    @StartableByRPC
//    class Initiator(private val participants: Array<Party>, private val assetNumber: String, private val address: String, private val amount: String) : FlowLogic<SignedTransaction>() {
//
//
//        // The progress tracker checkpoints each stage of the flow and outputs the
//        // specified messages when each
//        // checkpoint is reached in the code. See the 'progressTracker.currentStep'
//        // expressions within the call()
//        // function.
//        override val progressTracker: ProgressTracker? = ProgressTracker()
//
//        /**
//         * The flow logic is encapsulated within the call() method.
//         */
//        @Suspendable
//        @Throws(FlowException::class)
//        override fun call(): SignedTransaction {
//
//            // Obtain a reference to the notary we want to use.
//            val notary = serviceHub.networkMapCache.notaryIdentities[0]
//
//            // Generate an unsigned transaction.
//            val me = ourIdentity
//            val participantList = ArrayList<AbstractParty>()
//            participantList.add(me)
//            for (aParty in participants) {
//                participantList.add(aParty)
//            }
//
//            val initAmount = BigInteger(amount)
//
//            if (initAmount.compareTo(BigInteger.ZERO) < 0) {
//                throw FlowException("not enough token amount")
//            }
//
//            val initAsset = AssetTokenState(participantList, assetNumber,
//                    address, initAmount.toLong(),
//                    UniqueIdentifier())
//
//
//            val builder = ImmutableList.builder<PublicKey>()
//
//            for (aParty in participantList) {
//                builder.add(aParty.owningKey)
//            }
//
//            val txCommand = Command<DigitalAssetContract.Commands.InitAssetToken>(
//                    DigitalAssetContract.Commands.InitAssetToken(), builder.build())
//
//            val txBuilder = TransactionBuilder(notary)
//                    .addOutputState(initAsset).addCommand(txCommand)
//
//            txBuilder.verify(serviceHub)
//
//            // Sign the transaction.
//            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)
//
//            val setbuilder = ImmutableSet.builder<FlowSession>()
//
//            for (aParty in participants) {
//                val otherPartySession = initiateFlow(aParty)
//                setbuilder.add(otherPartySession)
//            }
//
//            val imutableset = setbuilder.build()
//            val fullySignedTx = subFlow(
//                    CollectSignaturesFlow(partSignedTx, imutableset, CollectSignaturesFlow.tracker()))
//
//
//            return subFlow(FinalityFlow(fullySignedTx, imutableset))
//
//        }
//    }
//
//    @InitiatedBy(Initiator::class)
//    class Acceptor(private val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
//
//        @Suspendable
//        @Throws(FlowException::class)
//        override fun call(): SignedTransaction {
//            class SignTxFlow private constructor(otherPartyFlow: FlowSession, progressTracker: ProgressTracker) : SignTransactionFlow(otherPartyFlow, progressTracker) {
//
//                override fun checkTransaction(stx: SignedTransaction) {
//                    requireThat<Any> { require ->
//                        val state1 = stx.tx.outputs[0].data
//                        require.using("This must be an DigitalAssetState transaction.", state1 is AssetTokenState)
//                        val fromToken = state1 as AssetTokenState
//
//                        require.using("FromAddress Amount must be bigger than zero.",
//                                BigInteger.ZERO.compareTo(fromToken.amount) < 0)
//
//                        null
//                    }
//                }
//            }
//
//            val signTxFlow = SignTxFlow(otherPartySession, SignTransactionFlow.tracker())
//            val txId = subFlow(signTxFlow).id
//
//            return subFlow(ReceiveFinalityFlow(otherPartySession, txId))
//        }
//    }
//}
