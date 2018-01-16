package net.corda.examples.obligation;

import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.transactions.SignedTransaction;
import net.corda.examples.obligation.flows.IssueCapacity;
import net.corda.examples.obligation.flows.IssueWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;

/**
 * Created by pai on 16.01.18.
 */

@Path("capacity")
public class CapacityAPI {

    private final CordaRPCOps rpcOps;
    private final Party myIdentity;

    public CapacityAPI(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myIdentity = rpcOps.nodeInfo().getLegalIdentities().get(0);
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Party> me() {
        return ImmutableMap.of("me", myIdentity);
    }


    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> peers() {
        return ImmutableMap.of("peers", rpcOps.networkMapSnapshot()
                .stream()
                .filter(nodeInfo -> nodeInfo.getLegalIdentities().get(0) != myIdentity)
                .map(it -> it.getLegalIdentities().get(0).getName().getOrganisation())
                .collect(toList()));
    }

    @GET
    @Path("capacities")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<Work>> obligations() {
        return rpcOps.vaultQuery(Work.class).getStates();
    }


    @GET
    @Path("issue-resource-vendor-capacity")
    public Response issueResourceVendorCapacity(
            @QueryParam(value = "resourceCount") int resourceCount,
            @QueryParam(value = "durationInMonths") int durationInMonths,
            @QueryParam(value = "grade") int grade,
            @QueryParam(value = "totalResourceAmount") int totalResourceAmount,
            @QueryParam(value = "party") String party) {

        // 1. Get party objects for the counterparty.
        final Set<Party> lenderIdentities = rpcOps.partiesFromName(party, false);
        if (lenderIdentities.size() != 1) {
            final String errMsg = String.format("Found %d identities for the lender.", lenderIdentities.size());
            throw new IllegalStateException(errMsg);
        }
        final Party lenderIdentity = lenderIdentities.iterator().next();

        // 2. Create an amount object.
        final Amount issueAmount = new Amount<>((long) totalResourceAmount * 100, Currency.getInstance("CHF"));

        // 3. Start the IssueObligation flow. We block and wait for the flow to return.
        try {
            final FlowHandle<SignedTransaction> flowHandle = rpcOps.startFlowDynamic(
                    IssueCapacity.Initiator.class,
                    resourceCount, durationInMonths, grade, issueAmount, null, null, null, lenderIdentity
            );


            final SignedTransaction result = flowHandle.getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n%s",
                    result.getId(), result.getTx().getOutputStates().get(0));
            return Response.status(CREATED).entity(msg).build();
        } catch (Exception e) {
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("issue-resource-hr-capacity")
    public Response issueResourceHRCapacity(
            @QueryParam(value = "resourceCount") int resourceCount,
            @QueryParam(value = "durationInMonths") int durationInMonths,
            @QueryParam(value = "grade") int grade,
            @QueryParam(value = "totalResourceAmount") int totalResourceAmount,
            @QueryParam(value = "party") String party) {

        // 1. Get party objects for the counterparty.
        final Set<Party> lenderIdentities = rpcOps.partiesFromName(party, false);
        if (lenderIdentities.size() != 1) {
            final String errMsg = String.format("Found %d identities for the lender.", lenderIdentities.size());
            throw new IllegalStateException(errMsg);
        }
        final Party lenderIdentity = lenderIdentities.iterator().next();

        // 2. Create an amount object.
        final Amount issueAmount = new Amount<>((long) totalResourceAmount * 100, Currency.getInstance("CHF"));

        // 3. Start the IssueObligation flow. We block and wait for the flow to return.
        try {
            final FlowHandle<SignedTransaction> flowHandle = rpcOps.startFlowDynamic(
                    IssueCapacity.Initiator.class,
                    resourceCount, durationInMonths, grade, issueAmount, null, null, null, lenderIdentity
            );


            final SignedTransaction result = flowHandle.getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n%s",
                    result.getId(), result.getTx().getOutputStates().get(0));
            return Response.status(CREATED).entity(msg).build();
        } catch (Exception e) {
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("issue-group-cto-capacity")
    public Response issueGroupCTOCapacity(
            @QueryParam(value = "assetType") String assetType,
            @QueryParam(value = "assetCount") int assetCount,
            @QueryParam(value = "totalAssetAmount") int totalAssetAmount,
            @QueryParam(value = "party") String party) {

        // 1. Get party objects for the counterparty.
        final Set<Party> lenderIdentities = rpcOps.partiesFromName(party, false);
        if (lenderIdentities.size() != 1) {
            final String errMsg = String.format("Found %d identities for the lender.", lenderIdentities.size());
            throw new IllegalStateException(errMsg);
        }
        final Party lenderIdentity = lenderIdentities.iterator().next();

        // 2. Create an amount object.
        final Amount issueAmount = new Amount<>((long) totalAssetAmount * 100, Currency.getInstance("CHF"));

        // 3. Start the IssueObligation flow. We block and wait for the flow to return.
        try {
            final FlowHandle<SignedTransaction> flowHandle = rpcOps.startFlowDynamic(
                    IssueCapacity.Initiator.class,
                    null, null, null, null, assetType, totalAssetAmount, null, lenderIdentity
            );


            final SignedTransaction result = flowHandle.getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n%s",
                    result.getId(), result.getTx().getOutputStates().get(0));
            return Response.status(CREATED).entity(msg).build();
        } catch (Exception e) {
            return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
        }
    }




}
