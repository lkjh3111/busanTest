//package kr.co.busanbank.bmt.schema
//
//import com.google.common.collect.ImmutableList
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.schemas.MappedSchema
//import net.corda.core.schemas.PersistentState
//
//import javax.persistence.Column
//import javax.persistence.Entity
//import javax.persistence.Table
//
///**
// * An AssetTokenSchemaV1 schema.
// */
//class AssetTokenSchemaV1 : MappedSchema(AssetTokenSchema::class.java, 1, ImmutableList.of(PersistentAT::class.java)) {
//
//    @Entity
//    @Table(name = "assettoken_states")
//    class PersistentAT : PersistentState {
//
//        @Column(name = "assetNumber")
//        val assetNumber: String?
//        @Column(name = "owner")
//        val owner: String?
//        @Column(name = "amount")
//        val amount: String?
//        @Column(name = "linear_id")
//        val id: UniqueIdentifier?
//
//        constructor(assetNumber: String, owner: String, amount: String, linearId: UniqueIdentifier) {
//            this.assetNumber = assetNumber
//            this.owner = owner
//            this.amount = amount
//            this.id = linearId
//        }
//
//        constructor() {
//            this.assetNumber = null
//            this.owner = null
//            this.amount = null
//            this.id = null
//        }
//    }
//}