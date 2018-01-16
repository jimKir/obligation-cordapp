package net.corda.examples.obligation;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by pai on 16.01.18.
 */
public class Work implements LinearState {


    private final UniqueIdentifier linearId;
    private final String featureTitle;
    private final String description;

    private final Amount<Currency> amount;
    private final AbstractParty lender;
    private final AbstractParty borrower;

    public Work(UniqueIdentifier linearId, String featureTitle, String description, Amount<Currency> amount, AbstractParty lender, AbstractParty borrower) {
        this.linearId = linearId;
        this.featureTitle = featureTitle;
        this.description = description;
        this.amount = amount;
        this.lender = lender;
        this.borrower = borrower;
    }

    public Work(String featureTitle, String description, Amount<Currency> amount, AbstractParty lender, AbstractParty borrower) {
        this.featureTitle = featureTitle;
        this.description = description;
        this.amount = amount;
        this.lender = lender;
        this.borrower = borrower;
        this.linearId = new UniqueIdentifier();
    }

    public String getFeatureTitle() {
        return featureTitle;
    }

    public String getDescription() {
        return description;
    }

    public Amount<Currency> getAmount() {
        return amount;
    }

    public AbstractParty getLender() {
        return lender;
    }

    public AbstractParty getBorrower() {
        return borrower;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(lender, borrower);
    }

    public List<PublicKey> getParticipantKeys() {
        return getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Work work = (Work) o;
        return Objects.equals(linearId, work.linearId) &&
                Objects.equals(featureTitle, work.featureTitle) &&
                Objects.equals(description, work.description) &&
                Objects.equals(amount, work.amount) &&
                Objects.equals(lender, work.lender) &&
                Objects.equals(borrower, work.borrower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linearId, featureTitle, description, amount, lender, borrower);
    }

    @Override
    public String toString() {
        return "Work{" +
                "linearId=" + linearId +
                ", featureTitle='" + featureTitle + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", lender=" + lender +
                ", borrower=" + borrower +
                '}';
    }
}
