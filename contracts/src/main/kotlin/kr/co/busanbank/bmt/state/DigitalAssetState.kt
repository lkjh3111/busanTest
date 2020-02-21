package kr.co.busanbank.bmt.state

//import kr.co.busanbank.bmt.schema.DigitalAssetSchemaV1
import kr.co.busanbank.bmt.contract.DigitalAssetContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty

/**
 * The state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 */
@BelongsToContract(DigitalAssetContract::class)
class DigitalAssetState
/**
 *
 * @param owner
 * @param assetIssuerNumber
 * @param assetNumber
 * @param name
 * @param totalAmount
 * @param totalValue
 * @param linearId
 */
(override val participants: List<AbstractParty>, val assetIssuerNumber: String, val assetNumber: String,
 val name: String, val totalAmount: String, val totalValue: String, val assetValueNumber: String, val issueDate: String,
 val ownerAddress: String,
 override val linearId: UniqueIdentifier) : LinearState{
    override fun toString(): String {
        return String.format(
                "DigitalAssetState(assetIssuerNumber=%s, assetNumber=%s, name=%s, totalAmount=%s, totalValue=%s, assetValueNumber=%s, issueDate=%s, ownerAddress=%s,  linearId=%s)",
                assetIssuerNumber, assetNumber, name, totalAmount, totalValue, assetValueNumber, issueDate,
                ownerAddress, linearId)
    }
}