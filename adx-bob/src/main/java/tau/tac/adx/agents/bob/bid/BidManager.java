package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
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
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*This class is used to manage all bid bundle related actions in the game*/
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

    /*This routine create new bid bundle and add it to the campaign queries and store the bid in the
    learsStorage object*/
    public AdxBidBundle BuildBidAndAds() {
        AdxBidBundle bidBundle = new AdxBidBundle();
        /*At day d we bid for day d+1 (the next day)*/
        int dayBiddingFor = this.gameData.getDay() + 1;
        /*Get a list of all our campaigns that will be active at the next day, and for each campaign we add the bid
        bundle query*/
        List<CampaignData> activeCampaigns = campaignStorage.getMyActiveCampaigns(dayBiddingFor);
        for (CampaignData campaign : activeCampaigns) {
            addCampaignQueries(bidBundle, campaign);
        }
        log.info("Bid bundle :" + bidBundle.toString());
        learnStorage.saveBundle(bidBundle, dayBiddingFor);
        return bidBundle;
    }

    /*This routine calculate the bid bundle based on the current day in the game and add it to the server queries*/
    private void addCampaignQueries(AdxBidBundle bidBundle, CampaignData campaign) {
        double bid;
        int dayInGame = gameData.getDay() + 1;

        AdxQuery[] arrayOfAdxQuery = campaign.getCampaignQueries();
        for (int i = 0; i < arrayOfAdxQuery.length; i++) {
            AdxQuery query = arrayOfAdxQuery[i];
            BidBundleData bidBundleData = bidBundleDataBuilder.build(campaign, query);
            log.info(bidBundleData.toString());
            if (campaign.getDayStart() == 1) {
                bid = bidBundleStrategy.calcFirstCampaignBid(bidBundleData, dayInGame, campaign);
            } else if (dayInGame < 13) {
                bid = bidBundleStrategy.calcFirstDayBid(bidBundleData, dayInGame, campaign);
            } else {
                if (dayInGame >= 52) { //after day 52 we don't mind if we didn't reach all campaign impressions
                    bid = bidBundleStrategy.calcLastDaysBid(bidBundleData, dayInGame, campaign);
                } else {
                    bid = bidBundleStrategy.calcStableBid(bidBundleData, dayInGame, campaign);
                }
            }
            bidBundle.addQuery(query, bid, new Ad(null), campaign.getId(), 1);
            log.info("Day " + this.gameData.getDay() + " Campaign id " + campaign.getId() + " Bid : " +
                    bid + "Query : " + query.toString());
        }
        double impressionLimit = campaign.impsTogo();
        double budgetLimit = campaign.getBudget();
        bidBundle.setCampaignDailyLimit(campaign.getId(), (int) impressionLimit, budgetLimit);

        log.info("Day " + this.gameData.getDay() + " Bid Bundle entries for Campaign id " + campaign.getId());
    }

    public void addAdnetReport(AdNetworkReport adnetReport) {
        Map<Integer, List<AdNetworkKey>> keys = adnetReport.keys().stream()
                .collect(Collectors.groupingBy(AdNetworkKey::getCampaignId));
        for (Map.Entry<Integer, List<AdNetworkKey>> key : keys.entrySet()) {
            CampaignData campaign = campaignStorage.getMyCampaign(key.getKey());
            double ratio = marketSegmentProbability.getMarketSegmentsRatio(campaign.getTargetSegment());
            long campaignBid = learnStorage.getCampaignBid(campaign.getId());
            int day = gameData.getDay();
            double storedBid = learnStorage.getBaseBidFromBundle(day - 1, campaign.getId());

            Optional<AdNetworkReportEntry> summedReport = key.getValue().stream()
                    .map(adnetReport::getAdNetworkReportEntry)
                    .collect(Collectors.reducing(this::sumReportEntries));
            if (!summedReport.isPresent()) {
                log.warning("could not find report for campaign " + key.getKey().toString());
                continue;
            }
            BidResult bidResult = new BidResult(storedBid, summedReport.get());

            learnStorage.addToCampaignCost(campaign.getId(), bidResult.getReport().getCost());

            CampaignBidBundleHistory history = new CampaignBidBundleHistory(campaign.getReachImps(), campaign
                    .getReachImpsPerDay(), ratio, campaignBid, day, campaign.getBudget(), campaign.getId(), bidResult);
            log.fine(history.toString());
            learnStorage.addBidHistory(history);
        }
    }

    private AdNetworkReportEntry sumReportEntries(AdNetworkReportEntry entry1, AdNetworkReportEntry entry2) {
        AdNetworkReportEntry result = new AdNetworkReportEntry();
        result.setBidCount(entry1.getBidCount() + entry2.getBidCount());
        result.setCost(entry1.getCost() + entry2.getCost());
        result.setWinCount(entry1.getWinCount() + entry2.getWinCount());
        return result;
    }


}
