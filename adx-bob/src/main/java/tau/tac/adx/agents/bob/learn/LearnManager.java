package tau.tac.adx.agents.bob.learn;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.FileSerializer;
import tau.tac.adx.agents.bob.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LearnManager {

    private final Logger log = Logger.getLogger(LearnManager.class.getName());

    private final String BID_HISTORY_FILE = "config/history.json";
    private final String CAMPAIGN_HISTORY_FILE = "config/campaign_history.json";

    private LearnStorage learnStorage;
    private FileSerializer fileSerializer;
    private MarketSegmentProbability marketSegmentProbability;
    private CampaignStorage campaignStorage;

    @Inject
    public LearnManager(LearnStorage learnStorage, FileSerializer fileSerializer,
                        MarketSegmentProbability marketSegmentProbability, CampaignStorage campaignStorage) {
        this.learnStorage = learnStorage;
        this.fileSerializer = fileSerializer;
        this.marketSegmentProbability = marketSegmentProbability;
        this.campaignStorage = campaignStorage;
    }

    public void loadStorage() {
        try {
            Type listType = new TypeToken<List<CampaignBidBundleHistory>>() {
            }.getType();
            List<CampaignBidBundleHistory> previous = fileSerializer.deserialize(BID_HISTORY_FILE, listType);
            learnStorage.setCampaignBidBundleHistories(previous);
        } catch (Exception e) {
            log.severe("could not load history:" + e.getMessage());
            e.printStackTrace();
            learnStorage.setCampaignBidBundleHistories(new ArrayList<>());
        }
        try {
            Type listType = new TypeToken<List<CampaignOpportunityBidHistory>>() {
            }.getType();
            List<CampaignOpportunityBidHistory> previous = fileSerializer.deserialize(CAMPAIGN_HISTORY_FILE, listType);
            learnStorage.setCampaignOpportunityBidHistories(previous);
        } catch (Exception e) {
            log.severe("could not load campaign history:" + e.getMessage());
            e.printStackTrace();
            learnStorage.setCampaignOpportunityBidHistories(new ArrayList<>());
        }
    }

    public void saveStorage() {
        List<CampaignBidBundleHistory> campaignBidBundleHistories = learnStorage.getCampaignBidBundleHistories();
        try {
            fileSerializer.serialize(campaignBidBundleHistories, BID_HISTORY_FILE);
        } catch (IOException e) {
            log.severe("could not save history:" + e.getMessage());
            e.printStackTrace();
        }
        List<CampaignOpportunityBidHistory> campaignHistories = learnStorage.getCampaignOpportunityBidHistories();
        try {
            fileSerializer.serialize(campaignHistories, CAMPAIGN_HISTORY_FILE);
        } catch (IOException e) {
            log.severe("could not save campaign history:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void storeEndingCampaigns(int endDay) {
        List<CampaignData> campaigns = campaignStorage.getMyEndingCampaigns(endDay);
        for (CampaignData campaign : campaigns) {
            CampaignOpportunityBidHistory history = new CampaignOpportunityBidHistory();
            history.setCampaignBid(learnStorage.getCampaignBid(campaign.getId()));
            history.setCampaignImpressions(campaign.getReachImps());
            long completedImps = campaign.getReachImps() - campaign.impsTogo();
            double err = Utils.effectiveReachRatio((double) completedImps, campaign.getReachImps());
            history.setCompletedPart(err);
            history.setDayStart((int) campaign.getDayStart());
            history.setId(campaign.getId());
            history.setImpressionsPerDay(campaign.getReachImpsPerDay());
            history.setMarketSegmentRatio(marketSegmentProbability.getMarketSegmentsRatio(campaign.getTargetSegment()));
            double income = err * campaign.getBudget();
            double expenses = learnStorage.getCampaignBidBundlesCost(campaign.getId());
            double profit = income - expenses;
            history.setProfit(profit);
            history.setWon(true);
            log.info("campaign is over, storing:" + history.toString());
            learnStorage.addCampaignBidHistory(history);
        }
    }
}
