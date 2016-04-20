package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umich.eecs.tac.props.Ad;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.learn.BidResult;
import tau.tac.adx.agents.bob.learn.CampaignBidBundleHistory;
import tau.tac.adx.agents.bob.learn.LearnStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;

import java.util.List;
import java.util.logging.Logger;

@Singleton
public class BidManager {

    private final Logger log = Logger.getLogger(BidManager.class.getName());

    private GameData gameData;
    private CampaignStorage campaignStorage;
    private BidBundleStrategy bidBundleStrategy;
    private BidBundleDataBuilder bidBundleDataBuilder;
    private MarketSegmentProbability marketSegmentProbability;
    private LearnStorage learnStorage;

    @Inject
    public BidManager(GameData gameData, CampaignStorage campaignStorage, BidBundleStrategy bidBundleStrategy,
                      BidBundleDataBuilder bidBundleDataBuilder, MarketSegmentProbability marketSegmentProbability,
                      LearnStorage learnStorage) {
        this.gameData = gameData;
        this.campaignStorage = campaignStorage;
        this.bidBundleStrategy = bidBundleStrategy;
        this.bidBundleDataBuilder = bidBundleDataBuilder;
        this.marketSegmentProbability = marketSegmentProbability;
        this.learnStorage = learnStorage;
    }

    public AdxBidBundle BuildBidAndAds() {
        AdxBidBundle bidBundle = new AdxBidBundle();
        int dayBiddingFor = this.gameData.getDay() + 1;
        List<CampaignData> activeCampaigns = campaignStorage.getMyActiveCampaigns(dayBiddingFor);
        for (CampaignData campaign : activeCampaigns) {
            addCampaignQueries(bidBundle, campaign);
        }
        log.info("Bid bundle :" + bidBundle.toString());
        learnStorage.saveBundle(bidBundle, dayBiddingFor);
        return bidBundle;
    }

    private void addCampaignQueries(AdxBidBundle bidBundle, CampaignData campaign) {
        double bid;
        int dayInGame = gameData.getDay() + 1;

        AdxQuery[] arrayOfAdxQuery = campaign.getCampaignQueries();
        for (int i = 0; i < arrayOfAdxQuery.length; i++) {
            AdxQuery query = arrayOfAdxQuery[i];
            if (campaign.impsTogo() > 0 && campaign.getDayStart() <= dayInGame && campaign.getDayEnd() > dayInGame) {
                BidBundleData bidBundleData = bidBundleDataBuilder.build(campaign, query, campaignStorage);
                log.info(bidBundleData.toString());
                if (dayInGame < 13) // first 12 days of the game
                {
                    bid = bidBundleStrategy.calcFirstDayBid(bidBundleData);
                } else {
                    bid = bidBundleStrategy.calcStableBid(bidBundleData);
                }
                bidBundle.addQuery(query, bid, new Ad(null), campaign.getId(), 1);
                log.info("Day " + this.gameData.getDay() + " Campaign id " + campaign.getId() + " Bid : " +
                        bid + "Query : " + query.toString());
            }
        }
        double impressionLimit = campaign.impsTogo();
        double budgetLimit = campaign.budget;
        bidBundle.setCampaignDailyLimit(campaign.getId(), (int) impressionLimit, budgetLimit);

        log.info("Day " + this.gameData.getDay() + " Bid Bundle entries for Campaign id " + campaign.getId());
    }

    public void addAdnetReport(AdNetworkReport adnetReport) {
        for (AdNetworkKey key : adnetReport.keys()) {
            AdNetworkReportEntry entry = adnetReport.getEntry(key);
            CampaignData campaign = campaignStorage.getMyCampaign(key.getCampaignId());
            double ratio = marketSegmentProbability.getMarketSegmentsRatio(campaign.getTargetSegment());
            long campaignBid = learnStorage.getCampaignBid(campaign.getId());
            int day = gameData.getDay();
            double storedBid = learnStorage.getBaseBidFromBundle(day, campaign.getId());
            BidResult bidResult = new BidResult(storedBid, entry);
            CampaignBidBundleHistory history = new CampaignBidBundleHistory(campaign.getReachImps(), campaign
                    .getReachImpsPerDay(), ratio, campaignBid, day, bidResult);
            learnStorage.addBidHistory(history);
        }
    }
}
