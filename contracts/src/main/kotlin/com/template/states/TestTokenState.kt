//package com.template.states
//
//import com.google.common.collect.ImmutableList
//import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
//import com.r3.corda.lib.tokens.contracts.states.FungibleToken
//import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
//import com.template.ExampleEvolvableTokenTypeContract
//import com.template.contracts.TestContract
//import net.corda.core.contracts.Amount
//import net.corda.core.contracts.BelongsToContract
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.identity.AbstractParty
//import net.corda.core.identity.Party
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.identity.Party
//import java.math.BigDecimal
//import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
//import net.corda.core.contracts.BelongsToContract
//import java.util.*
//
//
////@BelongsToContract(TestContract::class)
////class TestTokenState(
////        override val amount: Amount<IssuedTokenType>,
////        override val holder: AbstractParty
////        ) : FungibleToken(amount, holder) {
////        override fun equals(other: Any?): Boolean {
////                if (this === other) return true
////                if (javaClass != other?.javaClass) return false
////                if (!super.equals(other)) return false
////
////                other as TestTokenState
////
////                if (amount != other.amount) return false
////                if (holder != other.holder) return false
////
////                return true
////        }
////
////        override fun hashCode(): Int {
////                var result = super.hashCode()
////                result = 31 * result + amount.hashCode()
////                result = 31 * result + holder.hashCode()
////                return result
////        }
////}
//
//@BelongsToContract(TestContract::class)
//class FungibleHouseTokenState(val valuation: BigDecimal, val maintainer: Party,
//                              override val linearId: UniqueIdentifier, override val fractionDigits: Int, val symbol: String) : EvolvableTokenType() {
//
//        override val maintainers: List<Party>
//                get() = ImmutableList.of(maintainer)
//
//        fun getUniqueIdentifier(): UniqueIdentifier {
//                return linearId
//        }
//
//        override fun equals(o: Any?): Boolean {
//                if (this === o) return true
//                if (o == null || javaClass != o.javaClass) return false
//                val that = o as FungibleHouseTokenState?
//                return fractionDigits == that!!.fractionDigits &&
//                        valuation == that.valuation &&
//                        maintainer == that.maintainer &&
//                        linearId == that.linearId
//        }
//
//        override fun hashCode(): Int {
//                return Objects.hash(valuation, maintainer, linearId, fractionDigits)
//        }
//}