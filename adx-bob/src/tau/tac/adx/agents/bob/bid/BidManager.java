package tau.tac.adx.agents.bob.bid;

import java.util.Random;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import edu.umich.eecs.tac.props.Ad;

@Singleton
public class BidManager {

	private GameData gameData;

	@Inject
	public BidManager(GameData gameData){
		this.gameData = gameData;
	}

	/**
	 * 
	 */
	public AdxBidBundle BuildBidAndAds() {

		AdxBidBundle bidBundle = new AdxBidBundle();

		/*
		 * 
		 */

		int dayBiddingFor = gameData.day + 1;

		Random random = new Random();

		/* A random bid, fixed for all queries of the campaign */
		/*
		 * Note: bidding per 1000 imps (CPM) - no more than average budget
		 * revenue per imp
		 */

		double rbid = 10.0 * random.nextDouble();

		/*
		 * add bid entries w.r.t. each active campaign with remaining contracted
		 * impressions.
		 * 
		 * for now, a single entry per active campaign is added for queries of
		 * matching target segment.
		 */

		if ((dayBiddingFor >= gameData.getCurrCampaign().getDayStart())
				&& (dayBiddingFor <= gameData.getCurrCampaign().getDayEnd())
				&& (gameData.getCurrCampaign().impsTogo() > 0)) {

			int entCount = 0;

			for (AdxQuery query : gameData.getCurrCampaign().getCampaignQueries()) {
				if (gameData.getCurrCampaign().impsTogo() - entCount > 0) {
					/*
					 * among matching entries with the same campaign id, the AdX
					 * randomly chooses an entry according to the designated
					 * weight. by setting a constant weight 1, we create a
					 * uniform probability over active campaigns(irrelevant
					 * because we are bidding only on one campaign)
					 */
					if (query.getDevice() == Device.pc) {
						if (query.getAdType() == AdType.text) {
							entCount++;
						} else {
							entCount += gameData.getCurrCampaign().getVideoCoef();
						}
					} else {
						if (query.getAdType() == AdType.text) {
							entCount += gameData.getCurrCampaign().getMobileCoef();
						} else {
							entCount += gameData.getCurrCampaign().getVideoCoef()
									+ gameData.getCurrCampaign().getMobileCoef();
						}

					}
					bidBundle.addQuery(query, rbid, new Ad(null),
							gameData.getCurrCampaign().getId(), 1);
				}
			}

			double impressionLimit = gameData.getCurrCampaign().impsTogo();
			double budgetLimit = gameData.getCurrCampaign().budget;
			bidBundle.setCampaignDailyLimit(gameData.getCurrCampaign().getId(),
					(int) impressionLimit, budgetLimit);

			System.out.println("Day " + gameData.day + ": Updated " + entCount
					+ " Bid Bundle entries for Campaign id " + gameData.getCurrCampaign().getId());
		}
		gameData.bidBundle = bidBundle;
		return bidBundle;
	}
}
