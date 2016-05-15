package tau.tac.adx.agents.bob.campaign;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.learn.CampaignOpportunityBidHistory;
import tau.tac.adx.agents.bob.learn.KnnCampaignOpportunityBid;
import tau.tac.adx.agents.bob.learn.LearnStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import java.util.List;
import java.util.logging.Logger;

/**
 * Manages logic related to campaign bid.
 */
public class CampaignBidManager {

    private final Logger log = Logger.getLogger(CampaignBidManager.class.getName());

    private GameData gameData;
    private MarketSegmentProbability marketSegmentProbability;
    private CampaignStorage campaignStorage;
    private KnnCampaignOpportunityBid knnCampaignOpportunityBid;
    private LearnStorage learnStorage;

    @Inject
    public CampaignBidManager(GameData gameData, MarketSegmentProbability marketSegmentProbability,
                              CampaignStorage campaignStorage, KnnCampaignOpportunityBid knnCampaignOpportunityBid,
                              LearnStorage learnStorage) {
        this.gameData = gameData;
        this.marketSegmentProbability = marketSegmentProbability;
        this.campaignStorage = campaignStorage;
        this.knnCampaignOpportunityBid = knnCampaignOpportunityBid;
        this.learnStorage = learnStorage;
    }

    /**
     * Returns the appropriate bid for the campaign opportunity according to game status.
     *
     * @param campaignOpportunity campaign opportunity message
     * @return the campaign bid in millis
     */
    public long generateCampaignBid(CampaignOpportunityMessage campaignOpportunity) {
        int day = gameData.getDay();
        long cmpimps = campaignOpportunity.getReachImps();
        double epsilon = 0.65;
        int k = 8;

        Double greedyBidMillis = Math.ceil(cmpimps * gameData.getQualityScore()) - 1;
        Double spartanBid = Math.floor(cmpimps * 0.1 / gameData.getQualityScore()) + 1;

        long avgImpressionPerDay = cmpimps /
                (campaignOpportunity.getDayEnd() - campaignOpportunity.getDayStart() + 1);

        Double cmpBidMillis = 0.0;
        if (gameData.getQualityScore() < 0.8)
            cmpBidMillis = greedyBidMillis;
        else if (campaignOpportunity.getDayEnd() - campaignOpportunity.getDayStart() > 6 || avgImpressionPerDay <
                500 || day >= 50 || (avgImpressionPerDay > 1500 && day > 2))
            cmpBidMillis = spartanBid;
        else {
            cmpBidMillis = 0.8 * greedyBidMillis
                    + campaignStorage.getOverlappingImps(campaignStorage.getPendingCampaign()) * 0.1;
            cmpBidMillis *= (0.8
                    + 0.2 * marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
            cmpBidMillis *= 1.1 - 0.05 * (campaignOpportunity.getMobileCoef() + campaignOpportunity.getVideoCoef());
            if (day > 5)
                cmpBidMillis *= 0.3 + getActivityRatio(day + 1);
            List<CampaignOpportunityBidHistory> allSimilarCampaign = knnCampaignOpportunityBid
                    .getSimilarCampaignOpportunity(learnStorage, campaignOpportunity, epsilon);
            if (allSimilarCampaign.size() > 7) {
                double knnBid = calculateKnnBid(allSimilarCampaign, campaignOpportunity, k);
                cmpBidMillis = 0.95 * cmpBidMillis + 0.05 * knnBid;
            }
            cmpBidMillis *= Utils.randDouble(0.75, 1.25);
            if (cmpBidMillis > greedyBidMillis)
                cmpBidMillis = greedyBidMillis;
            if (cmpBidMillis < spartanBid)
                cmpBidMillis = spartanBid;
        }
        return cmpBidMillis.longValue();
    }

    private double calculateKnnBid(List<CampaignOpportunityBidHistory> allSimilarCampaign, CampaignOpportunityMessage
            campaignOpportunity, int k) {
        List<CampaignOpportunityBidHistory> kSimilarCampaign = knnCampaignOpportunityBid
                .getKNearestNeighboursSimilarCampaignOpportunity(campaignOpportunity, allSimilarCampaign, k);
        double bidAvg = knnCampaignOpportunityBid.getSimilarCampaignOpportunityBidAvg(kSimilarCampaign);
        double profitAvg = knnCampaignOpportunityBid.getSimilarCampaignOpportunityProfitAvg(kSimilarCampaign);
        double completeAvgRate = knnCampaignOpportunityBid.getSimilarCampaignOpportunityCompleteRateAvg
                (kSimilarCampaign);
        if (profitAvg < 0.0005 * campaignOpportunity.getReachImps()) {
            bidAvg *= 1.1;
        }
        if (gameData.getDay() < 20) {
            if (completeAvgRate < 0.9)
                bidAvg *= 1.1;
        } else if (gameData.getDay() < 40) {
            if (completeAvgRate < 0.85) {
                bidAvg *= 1.05;
            }
        } else if (gameData.getDay() < 50) {
            if (completeAvgRate < 0.8) {
                bidAvg *= 1.05;
            }
        }
        return bidAvg;
    }

    private double getActivityRatio(int effectiveDay) {
        long my = campaignStorage.getMyActiveCampaigns(effectiveDay).size();
        long all = campaignStorage.getAllActiveCampaigns(effectiveDay).size();
        int numberOfgents = campaignStorage.getNumberOfAgents();
        return (double) my * numberOfgents / all;
    }
}
