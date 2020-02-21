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
//import kr.co.busanbank.bmt.schema.AssetTokenSchemaV1
//import kr.co.busanbank.bmt.state.AssetTokenState
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
//import net.corda.core.node.services.Vault
//import net.corda.core.node.services.vault.FieldInfo
//import net.corda.core.node.services.vault.QueryCriteria
//import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression
//import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria
//import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//import net.corda.core.utilities.ProgressTracker
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
//class TransferFlow {
//
//    @InitiatingFlow
//    @StartableByRPC
//    class Initiator(private val participants: Array<Party>, private val assetNumber: String, private val fromAddress: String,
//                    private val toAddress: String, private val amount: String) : FlowLogic<SignedTransaction>() {
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
//            // from �ܰ� ��ȸ
//            //get house states on ledger with uuid as input tokenId
//            var assetNumberField: FieldInfo? =
//
//                    null
//            try {
//                assetNumberField = getField("assetNumber", AssetTokenSchemaV1.PersistentAT::class.java)
//            } catch (e: NoSuchFieldException) {
//                e.printStackTrace()
//                throw FlowException(e.message)
//            }
//
//            var ownerField: FieldInfo? = null
//            try {
//                ownerField = getField("owner", AssetTokenSchemaV1.PersistentAT::class.java)
//            } catch (e: NoSuchFieldException) {
//                e.printStackTrace()
//                throw FlowException(e.message)
//            }
//
//            val unconsumed = VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
//            val equalAssetExpress = net.corda.core.node.services.vault.Builder.equal<String>(assetNumberField!!, assetNumber)
//            val equalAsset = VaultCustomQueryCriteria(equalAssetExpress)
//
//            var equalAddressExpress = net.corda.core.node.services.vault.Builder.equal<String>(ownerField!!, fromAddress)
//            var equalAddress = VaultCustomQueryCriteria(equalAddressExpress)
//
//            var query = unconsumed.and(equalAsset).and(equalAddress)
//
//            val pages = serviceHub.vaultService.queryBy(AssetTokenState::class.java, query).states
//
//            if (pages.size < 1) {
//                throw FlowException("cannot find from asset")
//            }
//            val fromState = pages[0].state.data
//
//            val fromAmount = fromState.amount
//            var toAmount = BigInteger.ZERO
//
//            try {
//                equalAddressExpress = net.corda.core.node.services.vault.Builder.equal(ownerField, toAddress)
//                equalAddress = VaultCustomQueryCriteria(equalAddressExpress)
//
//                query = unconsumed.and(equalAsset).and(equalAddress)
//
//                if (pages.size > 0) {
//                    toAmount = pages[0].state.data.amount
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            val moveAmount = BigInteger(amount)
//            val afterFromAmount = fromAmount.subtract(moveAmount)
//            val afterToAmount = toAmount.add(moveAmount)
//
//            if (afterFromAmount.compareTo(BigInteger.ZERO) < 0) {
//                throw FlowException("not enough token amount")
//            }
//
//            val fromAsset = AssetTokenState(participantList, assetNumber,
//                    fromAddress, afterFromAmount.toLong(),
//                    UniqueIdentifier())
//
//            val toAsset = AssetTokenState(participantList, assetNumber,
//                    toAddress, afterToAmount.toLong(),
//                    UniqueIdentifier())
//
//            val builder = ImmutableList.builder<PublicKey>()
//
//            for (aParty in participantList) {
//                builder.add(aParty.owningKey)
//            }
//
//            val txCommand = Command<DigitalAssetContract.Commands.Transfer>(
//                    DigitalAssetContract.Commands.Transfer(), builder.build())
//
//            val txBuilder = TransactionBuilder(notary)
//                    .addOutputState(fromAsset)
//                    .addOutputState(toAsset).addCommand(txCommand)
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
//            return subFlow(FinalityFlow(fullySignedTx, imutableset))
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
//                        val state2 = stx.tx.outputs[1].data
//                        require.using("This must be an DigitalAssetState transaction.", state2 is AssetTokenState)
//                        val toToken = state2 as AssetTokenState
//                        require.using("ToAddress Amount must be bigger than zero.",
//                                BigInteger.ZERO.compareTo(toToken.amount) < 0)
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
