package tau.tac.adx.agents.bob.campaign;

import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CampaignBidManager {

	private GameData gameData;

	@Inject
	public CampaignBidManager(GameData gameData) {
		this.gameData = gameData;
	}

	public long generateCampaignBid(
			CampaignOpportunityMessage campaignOpportunity) {
		/*
		 * The campaign requires campaignOpportunity.getReachImps() impressions.
		 * The competing Ad Networks bid for the total campaign Budget (that is,
		 * the ad network that offers the lowest budget gets the campaign
		 * allocated). The advertiser is willing to pay the AdNetwork at most 1$
		 * CPM, therefore the total number of impressions may be treated as a
		 * reserve (upper bound) price for the auction.
		 */
		long cmpimps = campaignOpportunity.getReachImps();
		// GreedyLucky
		Double cmpBidMillis = cmpimps * gameData.getQualityScore()  - 1.0;

		return cmpBidMillis.longValue();
	}
}
