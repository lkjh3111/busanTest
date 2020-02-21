//package kr.co.busanbank.bmt.flow
//
//
//import co.paralleluniverse.fibers.Suspendable
//import kr.co.busanbank.bmt.schema.AssetTokenSchemaV1
//import kr.co.busanbank.bmt.state.AssetTokenState
//import net.corda.core.contracts.StateAndRef
//import net.corda.core.flows.FlowException
//import net.corda.core.flows.FlowLogic
//import net.corda.core.flows.InitiatingFlow
//import net.corda.core.flows.StartableByRPC
//import net.corda.core.node.services.Vault
//import net.corda.core.node.services.vault.Builder
//import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression
//import net.corda.core.node.services.vault.FieldInfo
//import net.corda.core.node.services.vault.QueryCriteria
//import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria
//import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria
//import net.corda.core.node.services.vault.getField
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
//class GetBalanceFlow {
//
//    @InitiatingFlow
//    @StartableByRPC
//    class Initiator(private val assetNumber: String, private val address: String) : FlowLogic<String>() {
//        override val progressTracker: ProgressTracker? = ProgressTracker()
//
//        /**
//         * The flow logic is encapsulated within the call() method.
//         */
//        @Suspendable
//        @Throws(FlowException::class)
//        override fun call(): String {
//
//            var assetNumberField: FieldInfo? = null
//            try {
//                assetNumberField = getField("assetNumber", AssetTokenSchemaV1.PersistentAT::class.java)
//            } catch (e: IllegalArgumentException) {
//                e.printStackTrace()
//                throw FlowException(e.message)
//            }
//
//            var ownerField: FieldInfo? = null
//            try {
//                ownerField = getField("owner", AssetTokenSchemaV1.PersistentAT::class.java)
//            } catch (e: IllegalArgumentException) {
//                e.printStackTrace()
//                throw FlowException(e.message)
//            }
//
//            val unconsumed = VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)
//            val equalAssetExpress = Builder.equal(assetNumberField, assetNumber)
//            val equalAsset = VaultCustomQueryCriteria(equalAssetExpress)
//
//            val equalAddressExpress = Builder.equal<String>(ownerField!!, address)
//            val equalAddress = VaultCustomQueryCriteria(equalAddressExpress)
//
//            val query = unconsumed.and(equalAsset).and(equalAddress)
//
//            val pages = serviceHub.vaultService.queryBy(AssetTokenState::class.java, query).states
//
//            if (pages.size > 0) {
//                return pages[0].state.data.amount.toString()
//            }
//            throw FlowException("Query result is null")
//        }
//    }
//}