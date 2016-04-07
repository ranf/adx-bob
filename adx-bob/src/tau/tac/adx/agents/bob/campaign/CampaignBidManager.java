package tau.tac.adx.agents.bob.campaign;

import java.util.logging.Logger;

import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CampaignBidManager {

	private final Logger log = Logger.getLogger(CampaignBidManager.class.getName());

	private GameData gameData;

	private MarketSegmentProbability marketSegmentProbability;

	private CampaignStorage campaignStorage;

	@Inject
	public CampaignBidManager(GameData gameData, MarketSegmentProbability marketSegmentProbability,
			CampaignStorage campaignStorage) {
		this.gameData = gameData;
		this.marketSegmentProbability = marketSegmentProbability;
		this.campaignStorage = campaignStorage;
	}

	public long generateCampaignBid(CampaignOpportunityMessage campaignOpportunity) {
		/*
		 * The campaign requires campaignOpportunity.getReachImps() impressions.
		 * The competing Ad Networks bid for the total campaign Budget (that is,
		 * the ad network that offers the lowest budget gets the campaign
		 * allocated). The advertiser is willing to pay the AdNetwork at most 1$
		 * CPM, therefore the total number of impressions may be treated as a
		 * reserve (upper bound) price for the auction.
		 */
		log.info("campaign #" + campaignOpportunity.getId() + " market segment ratio = "
				+ marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
		log.info("campaign #" + campaignOpportunity.getId() + " overlapping imps = "
				+ campaignStorage.getOverlappingImps(campaignStorage.pendingCampaign));
		log.info("campaign #" + campaignOpportunity.getId() + " active imps = "
				+ campaignStorage.totalActiveCampaignsImpsCount());
		if (campaignOpportunity.getDay() >= 5)
			log.info("campaign #" + campaignOpportunity.getId() + " activity ratio = " + getActivityRatio());
		long cmpimps = campaignOpportunity.getReachImps();
		// GreedyLucky
		Double cmpBidMillis = cmpimps * gameData.getQualityScore() - 1.0;

		return cmpBidMillis.longValue();
	}

	private double getActivityRatio() {
		long my = campaignStorage.getMyActiveCampaigns().size();
		long other = campaignStorage.getOtherAgentsActiveCampaigns();
		int numberOfgents = 8;
		return my * numberOfgents / (my + other);
	}
}
