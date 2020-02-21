package com.template.contracts

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class TestContract : Contract {

    companion object {
        @JvmStatic
        val TEST_ID = "com.template.contracts.TestContract"
    }

    interface Commands : CommandData {
        class Issue : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {

            }
        }
    }

}