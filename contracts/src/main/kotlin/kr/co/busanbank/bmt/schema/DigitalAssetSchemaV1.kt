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
// * An PersistentDigitalAsset schema.
// */
//class DigitalAssetSchemaV1 : MappedSchema(DigitalAssetSchema::class.java, 1, ImmutableList.of(PersistentDigitalAsset::class.java)) {
//
//    @Entity
//    @Table(name = "digitalasset_states")
//    class PersistentDigitalAsset : PersistentState {
//
//        @Column(name = "assetIssuerNumber")
//        val assetIssuerNumber: String?
//        @Column(name = "assetNumber")
//        val assetNumber: String?
//        @Column(name = "name")
//        val name: String?
//        @Column(name = "totalAmount")
//        val totalAmount: String?
//        @Column(name = "totalValue")
//        val totalValue: String?
//        @Column(name = "assetValueNumber")
//        val assetValueNumber: String?
//        @Column(name = "issueDate")
//        val issueDate: String?
//        @Column(name = "assetIssuerSignture")
//        val assetIssuerSignture: String?
//
//        @Column(name = "linear_id")
//        val id: UniqueIdentifier?
//
//        constructor(assetIssuerNumber: String, assetNumber: String, name: String, totalAmount: String,
//                    totalValue: String, assetValueNumber: String, issueDate: String, assetIssuerSignture: String,
//                    linearId: UniqueIdentifier) {
//            this.assetIssuerNumber = assetIssuerNumber
//            this.assetNumber = assetNumber
//            this.name = name
//            this.totalAmount = totalAmount
//            this.totalValue = totalValue
//            this.assetValueNumber = assetValueNumber
//            this.issueDate = issueDate
//            this.assetIssuerSignture = assetIssuerSignture
//            this.id = linearId
//        }
//
//        constructor() {
//            this.assetIssuerNumber = null
//            this.assetNumber = null
//            this.name = null
//            this.totalAmount = null
//            this.totalValue = null
//            this.issueDate = null
//            this.assetValueNumber = null
//            this.assetIssuerSignture = null
//            this.id = null
//        }
//    }
//}