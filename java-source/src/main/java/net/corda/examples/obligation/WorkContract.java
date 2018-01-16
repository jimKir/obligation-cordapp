package net.corda.examples.obligation;

import com.google.common.collect.Sets;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.finance.contracts.asset.Cash;

import java.security.PublicKey;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;
import static net.corda.core.contracts.Structures.withoutIssuer;
import static net.corda.finance.utils.StateSumming.sumCash;

/**
 * Created by pai on 16.01.18.
 */
public class WorkContract implements Contract{

    public static final String WORK_CONTRACT_ID = "net.corda.examples.obligation.WorkContract";

    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements WorkContract.Commands {
        }

        class Transfer extends TypeOnlyCommandData implements WorkContract.Commands {
        }

        class Settle extends TypeOnlyCommandData implements WorkContract.Commands {
        }
    }



    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<WorkContract.Commands> command = requireSingleCommand(tx.getCommands(), WorkContract.Commands.class);
        final WorkContract.Commands commandData = command.getValue();
        final Set<PublicKey> setOfSigners = new HashSet<>(command.getSigners());
        if (commandData instanceof WorkContract.Commands.Issue) {
            verifyIssue(tx, setOfSigners);
        } else if (commandData instanceof WorkContract.Commands.Transfer) {
            verifyTransfer(tx, setOfSigners);
        } else if (commandData instanceof WorkContract.Commands.Settle) {
            verifySettle(tx, setOfSigners);
        } else {
            throw new IllegalArgumentException("Unrecognised command.");
        }


    }

    private Set<PublicKey> keysFromParticipants(Work work) {
        return work
                .getParticipants().stream()
                .map(AbstractParty::getOwningKey)
                .collect(toSet());
    }

    // This only allows one work issuance per transaction.
    private void verifyIssue(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            req.using("No inputs should be consumed when issuing an work.",
                    tx.getInputStates().isEmpty());
            req.using("Only one work state should be created when issuing an work.", tx.getOutputStates().size() == 1);
            Work work = (Work) tx.getOutputStates().get(0);
            req.using("A newly issued work must have a positive amount.", work.getAmount().getQuantity() > 0);
            req.using("The lender and borrower cannot be the same identity.", !work.getBorrower().equals(work.getLender()));
            req.using("Both lender and borrower together only may sign work issue transaction.",
                    signers.equals(keysFromParticipants(work)));
            return null;
        });
    }

    // This only allows one work transfer per transaction.
    private void verifyTransfer(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            req.using("An work transfer transaction should only consume one input state.", tx.getInputs().size() == 1);
            req.using("An work transfer transaction should only create one output state.", tx.getOutputs().size() == 1);
            Work input = tx.inputsOfType(Work.class).get(0);
            Work output = tx.outputsOfType(Work.class).get(0);

            req.using("The lender property must change in a transfer.", !input.getLender().equals(output.getLender()));
            req.using("The borrower, old lender and new lender only must sign an work transfer transaction",
                    signers.equals(Sets.union(keysFromParticipants(input), keysFromParticipants(output))));
            return null;
        });
    }

    private void verifySettle(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            // Check for the presence of an input work state.
            List<Work> workInputs = tx.inputsOfType(Work.class);
            req.using("There must be one input work.", workInputs.size() == 1);

            // Check there are output cash states.
            // We don't care about cash inputs, the Cash contract handles those.
            List<Cash.State> cash = tx.outputsOfType(Cash.State.class);
            req.using("There must be output cash.", !cash.isEmpty());

            // Check that the cash is being assigned to us.
            Work inputWork = workInputs.get(0);
            List<Cash.State> acceptableCash = cash.stream().filter(it -> it.getOwner().equals(inputWork.getLender())).collect(Collectors.toList());
            req.using("There must be output cash paid to the recipient.", !acceptableCash.isEmpty());

            // Sum the cash being sent to us (we don't care about the issuer).
            Amount<Currency> sumAcceptableCash = withoutIssuer(sumCash(acceptableCash));
            //Amount<Currency> amountOutstanding = inputWork.getAmount().minus(inputWork.getPaid());
            //req.using("The amount settled cannot be more than the amount outstanding.", amountOutstanding.compareTo(sumAcceptableCash) >= 0);

            List<Work> workOutputs = tx.outputsOfType(Work.class);

/*            // Check to see if we need an output work or not.
            if (amountOutstanding.equals(sumAcceptableCash)) {
                // If the work has been fully settled then there should be no work output state.
                req.using("There must be no output work as it has been fully settled.", workOutputs.isEmpty());
            } else {
                // If the work has been partially settled then it should still exist.
                req.using("There must be one output work.", workOutputs.size() == 1);

                // Check only the paid property changes.
                Work outputWork = workOutputs.get(0);
                req.using("The amount may not change when settling.", inputWork.getAmount().equals(outputWork.getAmount()));
                req.using("The borrower may not change when settling.", inputWork.getBorrower().equals(outputWork.getBorrower()));
                req.using("The lender may not change when settling.", inputWork.getLender().equals(outputWork.getLender()));
                req.using("The linearId may not change when settling.", inputWork.getLinearId().equals(outputWork.getLinearId()));

                // Check the paid property is updated correctly.
                //req.using("Paid property incorrectly updated.", outputWork.getPaid().equals(inputWork.getPaid().plus(sumAcceptableCash)));
            }

*/
            // Checks the required parties have signed.
            req.using("Both lender and borrower together only must sign work settle transaction.", signers.equals(keysFromParticipants(inputWork)));
            return null;
        });
    }




}
