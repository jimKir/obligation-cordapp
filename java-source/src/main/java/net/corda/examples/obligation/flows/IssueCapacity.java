package net.corda.examples.obligation.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.contracts.Amount;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.examples.obligation.Capacity;
import net.corda.examples.obligation.CapacityContract;


import java.security.PublicKey;
import java.time.Duration;
import java.util.Currency;
import java.util.List;


/**
 * Created by pai on 16.01.18.
 *
 *
 */
public class IssueCapacity {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        // PC-Resource-Vendor/HR
        private Integer resourceCount;
        private Integer durationInMonths;
        private Integer grade;

        private final Amount<Currency> totalResourceAmount;

        // PC-GROUP-CTO
        private String assetType;
        private Integer assetCount;
        private final Amount<Currency> totalAssetAmount;

        private final Party lender;

        private final ProgressTracker.Step INITIALISING = new ProgressTracker.Step("Performing initial steps.");
        private final ProgressTracker.Step BUILDING = new ProgressTracker.Step("Performing initial steps.");
        private final ProgressTracker.Step SIGNING = new ProgressTracker.Step("Signing transaction.");
        private final ProgressTracker.Step COLLECTING = new ProgressTracker.Step("Collecting counterparty signature.") {
            @Override public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.Companion.tracker();
            }
        };
        private final ProgressTracker.Step FINALISING = new ProgressTracker.Step("Finalising transaction.") {
            @Override public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };

        private final ProgressTracker progressTracker = new ProgressTracker(
                INITIALISING, BUILDING, SIGNING, COLLECTING, FINALISING
        );

        public Initiator(Integer resourceCount, Integer durationInMonths, Integer grade, Amount<Currency> totalResourceAmount,
                         Party lender) {
            this.resourceCount = resourceCount;
            this.durationInMonths = durationInMonths;
            this.grade = grade;
            this.totalResourceAmount = totalResourceAmount;

            this.totalAssetAmount = null;

            this.lender = lender;

        }

        public Initiator(String assetType, Integer assetCount, Amount<Currency> totalAssetAmount, Party lender) {
            this.assetType = assetType;
            this.assetCount = assetCount;
            this.totalAssetAmount = totalAssetAmount;

            this.totalResourceAmount = null;

            this.lender = lender;

        }


        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Suspendable
        private Capacity createCapacity() throws FlowException {

            return new Capacity(resourceCount,durationInMonths, grade, totalResourceAmount,
                    assetType, assetCount, totalAssetAmount, lender, getOurIdentity());
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            System.out.println("Here we enter");

            // Step 1. Initialisation.
            progressTracker.setCurrentStep(INITIALISING);
            final Capacity capacity = createCapacity();
            final PublicKey ourSigningKey = capacity.getBorrower().getOwningKey();

            // Step 2. Building.
            progressTracker.setCurrentStep(BUILDING);
            final List<PublicKey> requiredSigners = capacity.getParticipantKeys();

            final TransactionBuilder utx = new TransactionBuilder(getFirstNotary())
                    .addOutputState(capacity, CapacityContract.CAPACITY_CONTRACT_ID)
                    .addCommand(new CapacityContract.Commands.Issue(), requiredSigners)
                    .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(30));

            // Step 3. Sign the transaction.
            progressTracker.setCurrentStep(SIGNING);
            final SignedTransaction ptx = getServiceHub().signInitialTransaction(utx, ourSigningKey);

            // Step 4. Get the counter-party signature.
            progressTracker.setCurrentStep(COLLECTING);
            final FlowSession lenderFlow = initiateFlow(lender);
            final SignedTransaction stx = subFlow(new CollectSignaturesFlow(
                    ptx,
                    ImmutableSet.of(lenderFlow),
                    ImmutableList.of(ourSigningKey),
                    COLLECTING.childProgressTracker())
            );

            // Step 5. Finalise the transaction.
            progressTracker.setCurrentStep(FINALISING);
            return subFlow(new FinalityFlow(stx, FINALISING.childProgressTracker()));

        }

        Party getFirstNotary() throws FlowException {
            List<Party> notaries = getServiceHub().getNetworkMapCache().getNotaryIdentities();
            if (notaries.isEmpty()) {
                throw new FlowException("No available notary.");
            }
            return notaries.get(0);
        }

    }


    @InitiatedBy(IssueCapacity.Initiator.class)
    public static class Responder extends FlowLogic<SignedTransaction> {
        private final FlowSession otherFlow;

        public Responder(FlowSession otherFlow) {
            this.otherFlow = otherFlow;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final SignedTransaction stx = subFlow(new ObligationBaseFlow.SignTxFlowNoChecking(otherFlow, SignTransactionFlow.Companion.tracker()));
            return waitForLedgerCommit(stx.getId());
        }
    }


}
