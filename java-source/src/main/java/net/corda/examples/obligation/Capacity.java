package net.corda.examples.obligation;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.ContractState;
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
public class Capacity implements ContractState {


    private final UniqueIdentifier linearId;

    // PC-Resource-Vendor/HR
    private Integer resourceCount;
    private Integer durationInMonths;
    private Integer grade;
    private final Amount<Currency> totalResourceAmount;

    // PC-GROUP-CTO
    private String assetType;
    private Integer assetCount;
    private final Amount<Currency> totalAssetAmount;


    private final AbstractParty lender;
    private final AbstractParty borrower;


    public Capacity(Integer resourceCount, Integer durationInMonths, Integer grade, Amount<Currency> totalResourceAmount, String assetType, Integer assetCount, Amount<Currency> totalAssetAmount, AbstractParty lender, AbstractParty borrower) {
        this.resourceCount = resourceCount;
        this.durationInMonths = durationInMonths;
        this.grade = grade;
        this.totalResourceAmount = totalResourceAmount;
        this.assetType = assetType;
        this.assetCount = assetCount;
        this.totalAssetAmount = totalAssetAmount;
        this.lender = lender;
        this.borrower = borrower;
        this.linearId = new UniqueIdentifier();
    }

    public Capacity(Integer resourceCount, Integer durationInMonths, Integer grade, Amount<Currency> totalResourceAmount, AbstractParty lender, AbstractParty borrower) {
        this.resourceCount = resourceCount;
        this.durationInMonths = durationInMonths;
        this.grade = grade;
        this.totalResourceAmount = totalResourceAmount;
        this.totalAssetAmount = null;
        this.lender = lender;
        this.borrower = borrower;
        this.linearId = new UniqueIdentifier();
    }

    public Capacity(String assetType, Integer assetCount, Amount<Currency> totalAssetAmount, AbstractParty lender, AbstractParty borrower) {
        this.assetType = assetType;
        this.assetCount = assetCount;
        this.totalAssetAmount = totalAssetAmount;
        this.totalResourceAmount = null;
        this.lender = lender;
        this.borrower = borrower;
        this.linearId = new UniqueIdentifier();

    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(lender, borrower);
    }

    public List<PublicKey> getParticipantKeys() {
        return getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList());
    }


    public Integer getResourceCount() {
        return resourceCount;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public Integer getGrade() {
        return grade;
    }

    public Amount<Currency> getTotalResourceAmount() {
        return totalResourceAmount;
    }

    public String getAssetType() {
        return assetType;
    }

    public Integer getAssetCount() {
        return assetCount;
    }

    public Amount<Currency> getTotalAssetAmount() {
        return totalAssetAmount;
    }


    public AbstractParty getLender() {
        return lender;
    }

    public AbstractParty getBorrower() {
        return borrower;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Capacity capacity = (Capacity) o;
        return Objects.equals(linearId, capacity.linearId) &&
                Objects.equals(resourceCount, capacity.resourceCount) &&
                Objects.equals(durationInMonths, capacity.durationInMonths) &&
                Objects.equals(grade, capacity.grade) &&
                Objects.equals(totalResourceAmount, capacity.totalResourceAmount) &&
                Objects.equals(assetType, capacity.assetType) &&
                Objects.equals(assetCount, capacity.assetCount) &&
                Objects.equals(totalAssetAmount, capacity.totalAssetAmount) &&
                Objects.equals(lender, capacity.lender) &&
                Objects.equals(borrower, capacity.borrower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linearId, resourceCount, durationInMonths, grade, totalResourceAmount, assetType, assetCount, totalAssetAmount, lender, borrower);
    }

    @Override
    public String toString() {
        return "Capacity{" +
                "linearId=" + linearId +
                ", resourceCount=" + resourceCount +
                ", durationInMonths=" + durationInMonths +
                ", grade=" + grade +
                ", totalResourceAmount=" + totalResourceAmount +
                ", assetType='" + assetType + '\'' +
                ", assetCount=" + assetCount +
                ", totalAssetAmount=" + totalAssetAmount +
                ", lender=" + lender +
                ", borrower=" + borrower +
                '}';
    }
}
