package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umich.eecs.tac.props.Ad;

import java.util.List;
import java.util.Random;
//import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.GameData;
//import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

@Singleton
public class BidManager {
	private GameData gameData;
	private CampaignStorage campaignStorage;
	private BidBundleStrategy bidBundleStrategy;
	private BidBundleDataBuilder bidBundleDataBuilder;

	@Inject
	public BidManager(GameData gameData, CampaignStorage campaignStorage, BidBundleStrategy bidBundleStrategy,
			BidBundleDataBuilder bidBundleDataBuilder) {
		this.gameData = gameData;
		this.campaignStorage = campaignStorage;
		this.bidBundleStrategy = bidBundleStrategy;
		this.bidBundleDataBuilder = bidBundleDataBuilder;
	}

	public AdxBidBundle BuildBidAndAds() {
		AdxBidBundle bidBundle = new AdxBidBundle();
		int dayBiddingFor = this.gameData.getDay() + 1;
		List<CampaignData> activeCampaigns = campaignStorage.getMyActiveCampaigns(dayBiddingFor);
		for (CampaignData campaign : activeCampaigns) {
			addCampaignQueries(bidBundle, campaign);
		}
		System.out.println("Bid bundle :" + bidBundle.toString());
		this.gameData.bidBundle = bidBundle;// TODO store history in array
		return bidBundle;
	}

	private void addCampaignQueries(AdxBidBundle bidBundle, CampaignData campaign) {
		double bid;
		int dayInGame = gameData.getDay() + 1;

		AdxQuery[] arrayOfAdxQuery = campaign.getCampaignQueries();
		for (int i = 0; i < arrayOfAdxQuery.length; i++) {
			AdxQuery query = arrayOfAdxQuery[i];
			if (campaign.impsTogo() > 0 && campaign.getDayStart() <= dayInGame && campaign.getDayEnd() > dayInGame) {
				BidBundleData bidBundleData = bidBundleDataBuilder.build(campaign, query);
				if (dayInGame < 6) // first five days of the game
				{
					bid = bidBundleStrategy.calcFirstDayBid(bidBundleData);
				} else {
					bid = bidBundleStrategy.calcStableBid(bidBundleData);
				}
				bidBundle.addQuery(query, bid, new Ad(null), campaign.getId(), 1);
				System.out.println("Day " + this.gameData.getDay() + "Campaign id " + campaign.getId() + "Bid : " + bid
						+ "Query : " + query.toString());
			}
		}
		double impressionLimit = campaign.impsTogo();
		double budgetLimit = campaign.budget;
		bidBundle.setCampaignDailyLimit(campaign.getId(), (int) impressionLimit, budgetLimit);

		System.out.println("Day " + this.gameData.getDay() + " Bid Bundle entries for Campaign id " + campaign.getId());
	}
}
