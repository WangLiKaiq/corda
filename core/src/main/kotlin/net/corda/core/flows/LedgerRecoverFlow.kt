package net.corda.core.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.CordaInternal
import net.corda.core.crypto.SecureHash
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.ProgressTracker

/**
 * Ledger Recovery Flow (available in Enterprise only).
 */
@StartableByRPC
@InitiatingFlow
class LedgerRecoveryFlow(
        private val parameters: LedgerRecoveryParameters,
        override val progressTracker: ProgressTracker = ProgressTracker()) : FlowLogic<Long>() {

    @CordaInternal
    data class ExtraConstructorArgs(val parameters: LedgerRecoveryParameters)
    @CordaInternal
    fun getExtraConstructorArgs() = ExtraConstructorArgs(parameters)

    @Suspendable
    @Throws(LedgerRecoveryException::class)
    override fun call(): Long {
        throw NotImplementedError("Enterprise only feature")
    }
}

@InitiatedBy(LedgerRecoveryFlow::class)
class ReceiveLedgerRecoveryFlow constructor(private val otherSideSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        throw NotImplementedError("Enterprise only feature")
    }
}

@CordaSerializable
class LedgerRecoveryException(message: String) : FlowException("Ledger recovery failed: $message")

data class LedgerRecoveryParameters(
    val recoveryPeers: Collection<Party>,
    val timeWindow: RecoveryTimeWindow? = null,
    val useAllNetworkNodes: Boolean = false,
    val transactionRole: TransactionRole = TransactionRole.ALL,
    val dryRun: Boolean = false,
    val optimisticInitiatorRecovery: Boolean = false,
    val useTimeWindowNarrowing: Boolean = false,
    val verboseLogging: Boolean = true,
    val recoveryBatchSize: Int = 1000
)

/**
 * This specifies which type of transactions to recover based on the transaction role of the recovering node
 */
@CordaSerializable
enum class TransactionRole {
    ALL,
    INITIATOR,  // only recover transactions that I initiated
    PEER,       // only recover transactions where I am a participant on a transaction
    OBSERVER,   // only recover transactions where I am an observer (but not participant) to a transaction
    PEER_AND_OBSERVER   // recovery transactions where I am either participant or observer
}

@CordaSerializable
data class RecoveryResult(
        val transactionId: SecureHash,
        val recoveryPeer: CordaX500Name,
        val transactionRole: TransactionRole,  // what role did I play in this transaction
        val synchronised: Boolean,   // whether the transaction was successfully synchronised (will always be false when dryRun option specified)
        val synchronisedInitiated: Boolean = false,    // only attempted if [optimisticInitiatorRecovery] option set to true and [TransactionRecoveryType.INITIATOR]
        val failureCause: String? = null    // reason why a transaction failed to synchronise
)
