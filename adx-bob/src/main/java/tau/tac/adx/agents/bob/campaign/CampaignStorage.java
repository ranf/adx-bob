package tau.tac.adx.agents.bob.campaign;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Stores and queries campaign related data.
 */
@Singleton
public class CampaignStorage {

    private MarketSegmentProbability marketSegmentProbability;

    private CampaignData pendingCampaign;
    private List<CampaignData> allKnownCampaigns;
    private List<CampaignData> myCampaigns;

    @Inject
    public CampaignStorage(MarketSegmentProbability marketSegmentProbability) {
        this.marketSegmentProbability = marketSegmentProbability;
        reset();
    }


    /**
     * Adds a campaign to the list of all known campaigns.
     *
     * @param campaign the campaign to add
     */
    public void acknowledgeCampaign(CampaignData campaign) {
        allKnownCampaigns.add(campaign);
    }

    /**
     * Adds a campaign to the list of my campaigns.
     *
     * @param campaign the campaign to add
     */
    public void addMyCampaign(CampaignData campaign) {
        myCampaigns.add(campaign);
    }

    /**
     * Removes all stored data.
     */
    public void reset() {
        allKnownCampaigns = new ArrayList<>();
        myCampaigns = new ArrayList<>();
        pendingCampaign = null;
    }

    /**
     * Gets the campaign with the specified id, assuming it exists on my campaigns.
     *
     * @param campaignId the id of the relevant campaign
     * @return CampaignData of the specified campaign, or null if not found
     */
    public CampaignData getMyCampaign(int campaignId) {
        Optional<CampaignData> optional = myCampaigns.stream().filter(c -> c.getId() == campaignId).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * Sets the winner of the specified campaign.
     *
     * @param campaignId the id of the relevant campaign
     * @param winner     the name of the agent who won the campaign
     */
    public void setCampaignWinner(long campaignId, String winner) {
        allKnownCampaigns.stream().filter(c -> c.getId() == campaignId).forEach(c -> c.setWinner(winner));
    }

    /**
     * Gets the number of agents who are known to have won a campaign in the current simulation.
     *
     * @return the number of known agents in the simulation
     */
    public int getNumberOfAgents() {
        return (int) allKnownCampaigns.stream().filter(c -> c.getWinner() != null).map(CampaignData::getWinner)
                .distinct().count() + 1;
    }

    /**
     * Sets the stats of a campaign.
     *
     * @param campaignId the id of the relevant campaign
     * @param stats      the stats to set
     */
    public void setCampaignStats(int campaignId, CampaignStats stats) {
        myCampaigns.stream().filter(c -> c.getId() == campaignId).forEach(c -> c.setStats(stats));
    }

    /**
     * Gets an estimation on the amount of impressions the campaign will have to compete against other campaign on.
     * This value may be much bigger than the campaign reach (grows for each overlapping campaign).
     *
     * @param campaign the relevant campaign
     * @return estimation on the number of impressions overlapping another known campaign.
     */
    public long getOverlappingImps(CampaignData campaign) {
        Double count = allKnownCampaigns.stream().filter(c -> c.getId() != campaign.getId())
                .mapToDouble(c -> getOverlappingImps(c, campaign))
                .sum();
        return count < 0 ? 0 : count.longValue();
    }

    private double getOverlappingImps(CampaignData campaign1, CampaignData campaign2) {
        long sharedDays = Math.max(0, Math.min(campaign1.getDayEnd(), campaign2.getDayEnd())
                - Math.max(campaign1.getDayStart(), campaign2.getDayStart()));

        Set<MarketSegment> sharedSegments = Sets.intersection(campaign1.getTargetSegment(),
                campaign2.getTargetSegment());
        double segmentsFactor = sharedSegments.isEmpty() ? 0.8 : 1.2;

        return segmentsFactor * sharedDays *
                (campaign1.getReachImpsPerDay() + campaign2.getReachImpsPerDay()) / 2;
    }

    /**
     * Gets the list of all active campaigns assigned to me.
     *
     * @param effectiveDay the day for the campaigns to be active on
     * @return a list of active campaigns
     */
    public List<CampaignData> getMyActiveCampaigns(int effectiveDay) {
        List<CampaignData> result = myCampaigns.stream().filter(myActiveCampaignFilter(effectiveDay))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Gets the list of all known active campaigns.
     *
     * @param effectiveDay the day for the campaigns to be active on
     * @return a list of active campaigns
     */
    public List<CampaignData> getAllActiveCampaigns(int effectiveDay) {
        List<CampaignData> result = allKnownCampaigns.stream().filter(activeCampaignFilter(effectiveDay))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Gets the campaign pending auction.
     *
     * @return the pending campaign
     */
    public CampaignData getPendingCampaign() {
        return pendingCampaign;
    }

    /**
     * Sets the campaign pending auction.
     *
     * @param pendingCampaign the pending campaign
     */
    public void setPendingCampaign(CampaignData pendingCampaign) {
        this.pendingCampaign = pendingCampaign;
    }

    /**
     * Gets the campaigns ending on the specified day.
     *
     * @param endDay the day for the campaigns to end on
     * @return a list of ending campaigns
     */
    public List<CampaignData> getMyEndingCampaigns(int endDay) {
        return myCampaigns.stream().filter(c -> c.getDayEnd() == endDay).collect(Collectors.toList());
    }

    /**
     * Gets the total number of remaining impressions for all active campaigns.
     *
     * @param day the day for the campaigns to be active on
     * @return the number of remaining impressions
     */
    public int getTotalNumberOfRemainingImpression(int day) {
        return myCampaigns.stream().filter(myActiveCampaignFilter(day))
                .mapToInt(CampaignData::impsTogo)
                .sum();
    }

    /**
     * Return {@code true} there is an active campaign with lower market segment than {@code marketPercentage},
     * otherwise {@code false}.
     *
     * @param day              the day for the campaigns to be active on
     * @param marketPercentage the threshold percentage
     * @return
     */
    public boolean isMarketSegmentPercentageLow(int day, double marketPercentage) {
        return myCampaigns.stream().filter(myActiveCampaignFilter(day))
                .mapToDouble(c -> marketSegmentProbability.getMarketSegmentsRatio(c.getTargetSegment()))
                .anyMatch(p -> p <= marketPercentage);
    }

    private Predicate<CampaignData> activeCampaignFilter(int day) {
        return c -> c.getDayStart() <= day && c.getDayEnd() >= day;
    }

    private Predicate<CampaignData> myActiveCampaignFilter(int day) {
        return activeCampaignFilter(day).and(c -> c.impsTogo() > 0);
    }


}
