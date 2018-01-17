package net.corda.examples.obligation;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Created by pai on 16.01.18.
 */
public class CapacityContract implements Contract {

    public static final String CAPACITY_CONTRACT_ID = "net.corda.examples.obligation.CapacityContract";

    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements CapacityContract.Commands {
        }

        class Transfer extends TypeOnlyCommandData implements CapacityContract.Commands {
        }

        class Settle extends TypeOnlyCommandData implements CapacityContract.Commands {
        }
    }




    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<CapacityContract.Commands> command = requireSingleCommand(tx.getCommands(), CapacityContract.Commands.class);
        final CapacityContract.Commands commandData = command.getValue();
        final Set<PublicKey> setOfSigners = new HashSet<>(command.getSigners());
        if (commandData instanceof CapacityContract.Commands.Issue) {
            verifyIssue(tx, setOfSigners);
        } else if (commandData instanceof CapacityContract.Commands.Transfer) {
            //verifyTransfer(tx, setOfSigners);
        } else if (commandData instanceof CapacityContract.Commands.Settle) {
            //verifySettle(tx, setOfSigners);
        } else {
            throw new IllegalArgumentException("Unrecognised command.");
        }
    }

    private Set<PublicKey> keysFromParticipants(Capacity capacity) {
        return capacity
                .getParticipants().stream()
                .map(AbstractParty::getOwningKey)
                .collect(toSet());
    }

    // This only allows one work issuance per transaction.
    private void verifyIssue(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            req.using("No inputs should be consumed when issuing an capacity.",
                    tx.getInputStates().isEmpty());
            req.using("Only one capacity state should be created when issuing an capacity.", tx.getOutputStates().size() == 1);
            Capacity capacity = (Capacity) tx.getOutputStates().get(0);
            //req.using("A newly issued capacity must have a positive amount.", capacity.getTotalResourceAmount().getQuantity() > 0 );
            req.using("The lender and borrower cannot be the same identity.", !capacity.getBorrower().equals(capacity.getLender()));
            req.using("Both lender and borrower together only may sign work issue transaction.",
                    signers.equals(keysFromParticipants(capacity)));
            return null;
        });
    }


}
