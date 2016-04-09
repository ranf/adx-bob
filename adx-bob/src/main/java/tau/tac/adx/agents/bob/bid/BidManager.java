package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umich.eecs.tac.props.Ad;

import java.util.List;
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
		List<CampaignData> activeCampaigns = campaignStorage.getMyActiveCampaigns(dayBiddingFor);
		for (CampaignData campaign : activeCampaigns) {
			addCampaignQueries(bidBundle, campaign);
		}
		this.gameData.bidBundle = bidBundle;// TODO store history in array
		return bidBundle;
	}

	private void addCampaignQueries(AdxBidBundle bidBundle, CampaignData campaign) {
		double rbid = 10.0D * random.nextDouble();
		if (campaign.impsTogo() > 0) { // TODO find out if we benefit from
										// bidding above reach (ERR)
			int entCount = 0; //Q: what is entCount?
			AdxQuery[] arrayOfAdxQuery= campaign.getCampaignQueries();
			for (int i = 0; i < arrayOfAdxQuery.length; i++) {
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
						entCount = (int) (entCount + (campaign.getVideoCoef() + campaign.getMobileCoef()));
					}
					//TODO this does not make sense at all..
					bidBundle.addQuery(query, rbid, new Ad(null), campaign.getId(), 1);
				}
			}
			double impressionLimit = campaign.impsTogo();
			double budgetLimit = campaign.budget;
			bidBundle.setCampaignDailyLimit(campaign.getId(), (int) impressionLimit, budgetLimit);

			System.out.println("Day " + this.gameData.getDay() + ": Updated " + entCount
					+ " Bid Bundle entries for Campaign id " + campaign.getId());
		}
	}
}
