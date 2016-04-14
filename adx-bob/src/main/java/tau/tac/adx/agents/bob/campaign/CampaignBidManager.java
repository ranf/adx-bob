package tau.tac.adx.agents.bob.campaign;

import java.util.Random;
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
	private Random random;

	@Inject
	public CampaignBidManager(GameData gameData, MarketSegmentProbability marketSegmentProbability,
			CampaignStorage campaignStorage, Random random) {
		this.gameData = gameData;
		this.marketSegmentProbability = marketSegmentProbability;
		this.campaignStorage = campaignStorage;
		this.random = random;
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
		int day = gameData.getDay();
		log.info("campaign #" + campaignOpportunity.getId() + " market segment ratio = "
				+ marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
		log.info("campaign #" + campaignOpportunity.getId() + " overlapping imps = "
				+ campaignStorage.getOverlappingImps(campaignStorage.getPendingCampaign()));
		log.info("campaign #" + campaignOpportunity.getId() + " active imps = "
				+ campaignStorage.totalActiveCampaignsImpsCount(day + 1));
		if (day >= 5)
			log.info("campaign #" + campaignOpportunity.getId() + " activity ratio = " + getActivityRatio());
		long cmpimps = campaignOpportunity.getReachImps();
		Double greedyBidMillis = cmpimps * gameData.getQualityScore() - 1.0;
		Double spartanBid = cmpimps * 0.1 / gameData.getQualityScore() + 1.0;

		Double cmpBidMillis = greedyBidMillis
				- campaignStorage.getOverlappingImps(campaignStorage.getPendingCampaign()) * 0.2;
		cmpBidMillis *= (0.9
				+ 0.1 * marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
		if (day > 5)
			cmpBidMillis *= 0.5 + getActivityRatio();
		cmpBidMillis *= random.nextDouble() + 0.5;
		cmpBidMillis *= day / 60 + 0.5;

		if (cmpBidMillis > greedyBidMillis)
			cmpBidMillis = greedyBidMillis;
		if (cmpBidMillis < spartanBid)
			cmpBidMillis = spartanBid;

		return cmpBidMillis.longValue();
	}

	private double getActivityRatio() {
		int effectiveDay = gameData.getDay() + 1;
		long my = campaignStorage.getMyActiveCampaigns(effectiveDay).size();
		long all = campaignStorage.getAllActiveCampaigns(effectiveDay).size();
		int numberOfgents = campaignStorage.getNumberOfAgents();
		System.out.println("my campaign count = " + my);
		System.out.println("all campaign count = " + all);
		return (double) my * numberOfgents / all;
	}
}
