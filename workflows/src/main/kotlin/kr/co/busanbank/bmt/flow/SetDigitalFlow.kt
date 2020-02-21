package kr.co.busanbank.bmt.flow

import co.paralleluniverse.fibers.Suspendable
import kr.co.busanbank.bmt.contract.DigitalAssetContract
import kr.co.busanbank.bmt.state.AssetTokenState
import kr.co.busanbank.bmt.state.DigitalAssetState

//import com.example.contract.IOUContract;
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableList.Builder
import com.google.common.collect.ImmutableSet
import net.corda.core.contracts.Command
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.identity.PartyAndCertificate
import net.corda.core.node.NodeInfo
import net.corda.core.node.services.IdentityService
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step

import net.corda.core.contracts.requireThat

import java.math.BigInteger
import java.security.PublicKey
import java.util.ArrayList

//
//class SetDigitalFlow {

    @InitiatingFlow
    @StartableByRPC
    class SetDigitalFlow(private val participants: List<Party>, private val assetIssuerNumber: String, private val assetNumber: String, private val name: String,
                    private val totalAmount: String, private val totalValue: String, private val assetValueNumber: String, private val issueDate: String,
                    private val ownerAddress: String) : FlowLogic<SignedTransaction>() {

//        private val GENERATING_TRANSACTION = Step("Generating transaction based on new DigitalAsset.")
//        private val VERIFYING_TRANSACTION = Step("Verifying contract constraints.")
//        private val SIGNING_TRANSACTION = Step("Signing transaction with our private key.")
//        private val GATHERING_SIGS = object : Step("Gathering the counterparty's signature.") {
//            override fun childProgressTracker(): ProgressTracker {
//                return CollectSignaturesFlow.tracker()
//            }
//        }
//        private val FINALISING_TRANSACTION = object : Step("Obtaining notary signature and recording transaction.") {
//            override fun childProgressTracker(): ProgressTracker {
//                return FinalityFlow.tracker()
//            }
//        }

        // The progress tracker checkpoints each stage of the flow and outputs the
        // specified messages when each
        // checkpoint is reached in the code. See the 'progressTracker.currentStep'
        // expressions within the call()
        // function.
//        override val progressTracker: ProgressTracker? = ProgressTracker(GENERATING_TRANSACTION,
//                VERIFYING_TRANSACTION, SIGNING_TRANSACTION, GATHERING_SIGS, FINALISING_TRANSACTION)

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        @Throws(FlowException::class)
        override fun call(): SignedTransaction {

            // Obtain a reference to the notary we want to use.
            val notary = serviceHub.networkMapCache.notaryIdentities[0]
            // Stage 1.
//            progressTracker.currentStep = GENERATING_TRANSACTION
            // Generate an unsigned transaction.
            val me = ourIdentity

            val participantList = ArrayList<AbstractParty>()
            participantList.add(me)

            for (aParty in participants) {
                participantList.add(aParty)
            }

            val digitalAssetState = DigitalAssetState(participantList, assetIssuerNumber, assetNumber,
                    name, totalAmount, totalValue, assetValueNumber, issueDate, ownerAddress,
                    UniqueIdentifier())

            val bigTotalAmount = BigInteger(totalAmount)

            val assettokenstate = AssetTokenState(participantList, assetNumber, ownerAddress,
                    bigTotalAmount.toLong(), UniqueIdentifier())

            val builder = ImmutableList.builder<PublicKey>()

            for (aParty in participantList) {
                builder.add(aParty.owningKey)
            }

            val txCommand = Command<DigitalAssetContract.Commands.SetDigitalAsset>(
                    DigitalAssetContract.Commands.SetDigitalAsset(), builder.build())

            val txBuilder = TransactionBuilder(notary)
                    .addOutputState(digitalAssetState)
                    .addOutputState(assettokenstate).addCommand(txCommand)

            // Stage 2.
//            progressTracker.currentStep = VERIFYING_TRANSACTION
            // Verify that the transaction is valid.
            txBuilder.verify(serviceHub)

            // Stage 3.
//            progressTracker.currentStep = SIGNING_TRANSACTION
            // Sign the transaction.
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

            // Stage 4.
//            progressTracker.currentStep = GATHERING_SIGS
            // Send the state to the counterparty, and receive it back with their signature.
            val setbuilder = ImmutableSet.builder<FlowSession>()

            for (aParty in participants) {
                val otherPartySession = initiateFlow(aParty)
                setbuilder.add(otherPartySession)
            }

            val imutableset = setbuilder.build()
            val fullySignedTx = subFlow(
                    CollectSignaturesFlow(partSignedTx, imutableset, CollectSignaturesFlow.tracker()))

            // Stage 5.
//            progressTracker.currentStep = FINALISING_TRANSACTION

            return subFlow(FinalityFlow(fullySignedTx, imutableset))

        }
    }


@InitiatedBy(SetDigitalFlow::class)
class Acceptor(private val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) {
//                    requireThat<Any> { require ->
//                        val output = stx.tx.outputs[0].data
//                        require.using("This must be an DigitalAssetState transaction.", output is DigitalAssetState)
//                        val digitalasset = output as DigitalAssetState
//
//                        require.using("AssetIssuerNumber is null",
//                                digitalasset.assetIssuerNumber !=
//
//                                        null)
//
//                        val contractstate = stx.tx.outputs[1].data
//                        require.using("This must be an DigitalAssetState transaction.", contractstate is AssetTokenState)
//
//                        val assettoken = contractstate as AssetTokenState
//                        require.using("Asset Owner is null",
//                                assettoken.owner != null)
//                        null
//                    }
//                }
            }
        }
            val txId = subFlow(signTransactionFlow).id

            return subFlow(ReceiveFinalityFlow(otherPartySession, txId))
        }
}

