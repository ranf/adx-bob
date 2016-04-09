package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umich.eecs.tac.props.Ad;
import java.util.Random;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

@Singleton
public class BidManager {
	private GameData gameData;
	private Random random;
	private CampaignStorage campaignStorage;

	@Inject
	public BidManager(GameData gameData, Random random, CampaignStorage campaignStorage) {
		this.gameData = gameData;
		this.random = random;
		this.campaignStorage = campaignStorage;
	}

	public AdxBidBundle BuildBidAndAds() {
		AdxBidBundle bidBundle = new AdxBidBundle();

		int dayBiddingFor = this.gameData.getDay() + 1;
		// TODO go over getMyActiveCampaigns()
		// pending campaign is not yet ours
		CampaignData campaign = campaignStorage.pendingCampaign;

		double rbid = 10.0D * random.nextDouble();
		if ((dayBiddingFor >= campaign.getDayStart())
				&& (dayBiddingFor <= campaign.getDayEnd())
				&& (campaign.impsTogo() > 0)) {
			int entCount = 0;
			AdxQuery[] arrayOfAdxQuery;
			int j = (arrayOfAdxQuery = campaign.getCampaignQueries()).length;
			for (int i = 0; i < j; i++) {
				AdxQuery query = arrayOfAdxQuery[i];
				if (campaign.impsTogo() - entCount > 0) {
					if (query.getDevice() == Device.pc) {
						if (query.getAdType() == AdType.text) {
							entCount++;
						} else {
							entCount = (int) (entCount + campaign.getVideoCoef());
						}
					} else if (query.getAdType() == AdType.text) {
						entCount = (int) (entCount + campaign.getMobileCoef());
					} else {
						entCount = (int) (entCount + (campaign.getVideoCoef()
								+ campaign.getMobileCoef()));
					}
					bidBundle.addQuery(query, rbid, new Ad(null), campaign.getId(), 1);
				}
			}
			double impressionLimit = campaign.impsTogo();
			double budgetLimit = campaign.budget;
			bidBundle.setCampaignDailyLimit(campaign.getId(), (int) impressionLimit,
					budgetLimit);

			System.out.println("Day " + this.gameData.getDay() + ": Updated " + entCount
					+ " Bid Bundle entries for Campaign id " + campaign.getId());
		}
		this.gameData.bidBundle = bidBundle;
		return bidBundle;
	}
}
