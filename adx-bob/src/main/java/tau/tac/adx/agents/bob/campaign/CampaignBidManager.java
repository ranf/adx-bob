package tau.tac.adx.agents.bob.campaign;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.agents.bob.learn.CampaignOpportunityBidHistory;
import tau.tac.adx.agents.bob.learn.KnnCampaignOpportunityBid;
import tau.tac.adx.agents.bob.learn.LearnStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class CampaignBidManager {

    private final Logger log = Logger.getLogger(CampaignBidManager.class.getName());

    private GameData gameData;
    private MarketSegmentProbability marketSegmentProbability;
    private CampaignStorage campaignStorage;
    private KnnCampaignOpportunityBid knnCampaignOpportunityBid;
    private LearnStorage learnStorage;

    @Inject
    public CampaignBidManager(GameData gameData, MarketSegmentProbability marketSegmentProbability,
                              CampaignStorage campaignStorage,KnnCampaignOpportunityBid knnCampaignOpportunityBid,
                              LearnStorage learnStorage) {
        this.gameData = gameData;
        this.marketSegmentProbability = marketSegmentProbability;
        this.campaignStorage = campaignStorage;
        this.knnCampaignOpportunityBid = knnCampaignOpportunityBid;
        this.learnStorage = learnStorage;
    }

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
        if (gameData.getQualityScore() < 0.6)
            cmpBidMillis = greedyBidMillis;
        else if (campaignOpportunity.getDayEnd() - campaignOpportunity.getDayStart() > 6 || avgImpressionPerDay >
                1500 || day >= 54)
            cmpBidMillis = spartanBid;
        else {
            cmpBidMillis = 0.8 * greedyBidMillis
                    + campaignStorage.getOverlappingImps(campaignStorage.getPendingCampaign()) * 0.1;
            cmpBidMillis *= (0.8
                    + 0.2 * marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
            if (day > 5)
                cmpBidMillis *= 0.3 + getActivityRatio(day + 1);
            List<CampaignOpportunityBidHistory> allSimilarCampaign =knnCampaignOpportunityBid
                    .getSimilarCampaignOpportunity(learnStorage,campaignOpportunity,epsilon);
            System.out.println("All similar campaign size with knn is" + allSimilarCampaign.size());
            if(allSimilarCampaign.size() > 7){
                double knnBid = calculateKnnBid(allSimilarCampaign,campaignOpportunity,k);
                System.out.println("cmp Bid Millis with knn Campaign Opportunity Bid Before" + cmpBidMillis);
                System.out.println("knn bid" + knnBid);
                cmpBidMillis = 0.95*cmpBidMillis + 0.05*knnBid;
                System.out.println("cmp Bid Millis with knn Campaign Opportunity Bid" + cmpBidMillis);
            }
            cmpBidMillis *= Utils.randDouble(0.75, 1.25);
            if (cmpBidMillis > greedyBidMillis)
                cmpBidMillis = greedyBidMillis;
            if (cmpBidMillis < spartanBid)
                cmpBidMillis = spartanBid;
        }
    System.out.println("cmpBidMillis"+cmpBidMillis);
        return cmpBidMillis.longValue();
    }

    private double calculateKnnBid(List<CampaignOpportunityBidHistory> allSimilarCampaign,CampaignOpportunityMessage
            campaignOpportunity, int k){
        List<CampaignOpportunityBidHistory> kSimiliarCampaign = knnCampaignOpportunityBid
                .getKNearestNeighboursSimilarCampaignOpportunity(campaignOpportunity,allSimilarCampaign,k);
        double bidAvg = knnCampaignOpportunityBid.getSimilarCampaignOpportunityBidAvg(kSimiliarCampaign);
        double profitAvg = knnCampaignOpportunityBid.getSimilarCampaignOpportunityProfitAvg(kSimiliarCampaign);
        double completeAvgRate = knnCampaignOpportunityBid.getSimilarCampaignOpportunityCompleteRateAvg(kSimiliarCampaign);
        if(profitAvg < 0.0005*campaignOpportunity.getReachImps()){
            bidAvg *= 1.1;
        }
        if(gameData.getDay() < 20){
            if (completeAvgRate < 0.9)
                bidAvg *= 1.1;
        }
        else if(gameData.getDay() < 40){
            if(completeAvgRate < 0.85){
                bidAvg *= 1.05;
            }
        }
        else if(gameData.getDay() < 50){
            if(completeAvgRate < 0.8){
                bidAvg *= 1.05;
            }
        }
        return bidAvg;
    }

    private double getActivityRatio(int effectiveDay) {
        long my = campaignStorage.getMyActiveCampaigns(effectiveDay).size();
        long all = campaignStorage.getAllActiveCampaigns(effectiveDay).size();
        int numberOfgents = campaignStorage.getNumberOfAgents();
        log.info("my campaign count = " + my);
        log.info("all campaign count = " + all);
        return (double) my * numberOfgents / all;
    }
}
