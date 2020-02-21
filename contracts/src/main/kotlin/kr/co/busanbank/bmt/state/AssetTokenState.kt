package kr.co.busanbank.bmt.state

import com.google.common.collect.ImmutableList
import kr.co.busanbank.bmt.contract.DigitalAssetContract
//import kr.co.busanbank.bmt.schema.AssetTokenSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import org.bouncycastle.util.test.FixedSecureRandom

/**
 * The state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 */
@BelongsToContract(DigitalAssetContract::class)
class AssetTokenState
/**
 *
 * @param participants
 * @param assetNumber
 * @param owner
 * @param amount
 * @param linearId
 */
(override val participants: List<AbstractParty>, val assetNumber: String, val owner: String, val amount: Long,
 override val linearId: UniqueIdentifier) : LinearState {

//    @Override
    override fun toString(): String {
        return String.format("AssetTokenState(assetNumber=%s, owner=%s, amount=%s, linearId=%s)", assetNumber, owner,
                amount, linearId)
    }
}
