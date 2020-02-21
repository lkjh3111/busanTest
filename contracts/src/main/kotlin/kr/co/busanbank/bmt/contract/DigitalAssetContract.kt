package kr.co.busanbank.bmt.contract

import kr.co.busanbank.bmt.state.AssetTokenState
import kr.co.busanbank.bmt.state.DigitalAssetState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction


/**
 * A implementation of a basic smart contract in Corda.
 *
 * This contract enforces rules regarding the creation of a valid [IOUState],
 * which in turn encapsulates an [IOU].
 *
 * For a new [IOU] to be issued onto the ledger, a transaction is required which
 * takes: - Zero input states. - One output state: the new [IOU]. - An Create()
 * command with the public keys of both the lender and the borrower.
 *
 * All contracts must sub-class the [Contract] interface.
 */
class DigitalAssetContract : Contract {

    /**
     * The verify() function of all the states' contracts must not throw an
     * exception for a transaction to be considered valid.
     */
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.SetDigitalAsset -> requireThat {
//                "No inputs should be consumed when issuing an DigitalAsset." using (tx.inputs.isEmpty())
//                "Only one output state should be created." using (tx.outputs.size == 2)
//
//                val state1 = tx.outputsOfType<DigitalAssetState>().single()
//                val state2 = tx.outputsOfType<AssetTokenState>().single()
//
//                "Asset Issuer Number is null " using (state1.assetIssuerNumber.isEmpty())
//                "Asset Number is null " using  (state1.assetNumber.isEmpty())
//                "Asset name is null " using (state1.name.isEmpty())
//                "Asset Owner is null " using (state2.owner.isEmpty())
//
//                "All of the participants must be signers." using command.signers.containsAll(state1
//                        .participants.map{ it.owningKey })
            }
            is Commands.Transfer -> requireThat {
//                "Only one output state should be created." using (tx.outputs.size == 1)
//                val state1 = tx.outputsOfType<AssetTokenState>().single()
//                "All of the participants must be signers." using command.signers.containsAll(state1
//                        .participants.map{ it.owningKey })
            }
            is Commands.InitAssetToken -> requireThat {
//                "No inputs should be consumed when issuing an AssetTokenState." using  (tx.inputs.isEmpty())
//                "Only one output state should be created." using  (tx.outputs.size == 1)
//                val state1 = tx.outputsOfType<AssetTokenState>().single()
//                "All of the participants must be signers." using command.signers.containsAll(state1
//                        .participants.map{ it.owningKey })
            }
        }
    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class SetDigitalAsset : Commands
        class Transfer : Commands
        class InitAssetToken : Commands
    }

    companion object {
        val ID = "kr.co.busanbank.bmt.contract.DigitalAssetContract"
    }
}