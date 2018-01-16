package net.corda.examples.obligation;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by pai on 16.01.18.
 */
public class Work implements LinearState {




    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return null;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}
