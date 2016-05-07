package tau.tac.adx.agents.bob.learn;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.agentware.Main;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import java.util.*;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Created by adichen1 on 25/04/2016.
 */
public class KnnCampaignOpportunityBid {
    private MarketSegmentProbability marketSegmentProbability;

    @Inject
    public KnnCampaignOpportunityBid(MarketSegmentProbability marketSegmentProbability) {
        this.marketSegmentProbability = marketSegmentProbability;
    }

    /*return all similar campaign opportunity with respect to epsilon(maximum distance between campaigns)*/
    public List<CampaignOpportunityBidHistory> getSimilarCampaignOpportunity(LearnStorage learnStorage,
                                                                              CampaignOpportunityMessage
            campaignOpportunity, double epsilon) {
        List<CampaignOpportunityBidHistory> similarCampaignOpportunity =  new ArrayList<>();
        double distance;
        for (CampaignOpportunityBidHistory campaignOpportunityBidHistory: learnStorage.getCampaignOpportunityBidHistories())
        {
            distance = calcCampaignDistance(campaignOpportunityBidHistory, campaignOpportunity);
            if (distance < epsilon)
            {
                similarCampaignOpportunity.add(campaignOpportunityBidHistory);
            }
        }
        return similarCampaignOpportunity;
    }

    /* return k nearest campaigns*/
    public List<CampaignOpportunityBidHistory> getKNearestNeighboursSimilarCampaignOpportunity(CampaignOpportunityMessage
            campaignOpportunity,List<CampaignOpportunityBidHistory>  allSimilarCampaignOpportunity,
                                                                                               int k) {
        List<CampaignOpportunityBidHistory> kNearestCampaignOpportunity = new ArrayList<>();
        List<Double> allNearestCampaignOpportunityDistances = new ArrayList<>();
        double distance;
        for (CampaignOpportunityBidHistory campaignOpportunityBidHistory: allSimilarCampaignOpportunity)
        {
            distance = calcCampaignDistance(campaignOpportunityBidHistory, campaignOpportunity);
            System.out.println("Distance for knn" +distance);
            allNearestCampaignOpportunityDistances.add(distance);
        }
        System.out.println("allNearestCampaignOpportunityDistances" +allNearestCampaignOpportunityDistances);
        List<Integer> getkNearestCampaignOpportunityIndexes = getkNearestCampaignOpportunityIndexes
                (allNearestCampaignOpportunityDistances, k);
        for(int i :getkNearestCampaignOpportunityIndexes){
            kNearestCampaignOpportunity.add(allSimilarCampaignOpportunity.get(i));
        }
        return kNearestCampaignOpportunity;
    }

    /* return k nearest campaigns indexes from list of all similar campaign */
    private List<Integer> getkNearestCampaignOpportunityIndexes(List<Double> kNearestCampaignOpportunityDistances,int k){
        double minimum = Double.MAX_VALUE;
        int minimumIndex =0;
        List<Integer> minimumIndexes = new ArrayList<>();
        while (k>0){
            for(int i=0 ; i< kNearestCampaignOpportunityDistances.size();i++) {
                if (kNearestCampaignOpportunityDistances.get(i) <= minimum && Utils.notInList(minimumIndexes, i)) {
                    minimumIndex = i;
                    minimum =kNearestCampaignOpportunityDistances.get(i);
                }
            }
            minimumIndexes.add(minimumIndex);
            minimum = Double.MAX_VALUE;
            k--;
        }
        System.out.println("minimumIndexes" +minimumIndexes);
        return minimumIndexes;
    }


    /*return the average bid for given list of similar campaigns*/
    public double getSimilarCampaignOpportunityBidAvg(List<CampaignOpportunityBidHistory>
                                                               campaignOpportunityBidHistoriesList){
        double sum =0;
        for(CampaignOpportunityBidHistory campaignOpportunityBidHistories:campaignOpportunityBidHistoriesList){
            sum +=campaignOpportunityBidHistories.getCampaignBid();
        }
        System.out.println("8 nearest campaign sum avg" +sum/campaignOpportunityBidHistoriesList.size());
        return sum/campaignOpportunityBidHistoriesList.size();
    }

    /* return the average profit for given list of similar campaigns*/
    public double getSimilarCampaignOpportunityProfitAvg(List<CampaignOpportunityBidHistory> campaignOpportunityBidHistoriesList){
        double profit =0;
        for(CampaignOpportunityBidHistory campaignOpportunityBidHistories:campaignOpportunityBidHistoriesList){
            profit +=campaignOpportunityBidHistories.getProfit();
        }
        return profit/campaignOpportunityBidHistoriesList.size();
    }

    /*return the average completed rate for given list of similar campaigns */
    public double getSimilarCampaignOpportunityCompleteRateAvg(List<CampaignOpportunityBidHistory> campaignOpportunityBidHistoriesList){
        double completeRate =0;
        for(CampaignOpportunityBidHistory campaignOpportunityBidHistories:campaignOpportunityBidHistoriesList){
            completeRate +=campaignOpportunityBidHistories.getCompletedPart();
        }
        return completeRate/campaignOpportunityBidHistoriesList.size();
    }

    /*This routine gets information about previous campaigns and the current campaign offered
    * and calculate the distance based on the campaign impressionsPerDay, segment, start day of the campaign and
    * total num of impressions
    * we will use this routine result to find k nearest campaigns*/
    private double calcCampaignDistance(CampaignOpportunityBidHistory campaignOpportunityBidHistory,
                                        CampaignOpportunityMessage campaignOpportunity){
        double impressionsDistance = Math.abs((double) campaignOpportunityBidHistory.getCampaignImpressions() -
                (double) campaignOpportunity.getReachImps())
                /((double) campaignOpportunityBidHistory.getCampaignImpressions() + (double) campaignOpportunity.getReachImps());
        double marketSegmentDistance = Math.abs(campaignOpportunityBidHistory.getMarketSegmentRatio() - marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()))
                / (campaignOpportunityBidHistory.getMarketSegmentRatio()+ marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
        double dayStartDistance = Math.abs((double)campaignOpportunityBidHistory.getDayStart() - (double)campaignOpportunity.getDayStart())
                /((double)campaignOpportunityBidHistory.getDayStart() + (double)campaignOpportunity.getDayStart());
        double campaignOpportunityImpsPerDay = (double)campaignOpportunity.getReachImps()/((double)campaignOpportunity.getDayEnd()
                -(double)campaignOpportunity.getDayStart()+1);
        double impressionsPerDayDistance = Math.abs((double)campaignOpportunityBidHistory.getImpressionsPerDay()
                -campaignOpportunityImpsPerDay)
                / ((double)campaignOpportunityBidHistory.getImpressionsPerDay() + (double)campaignOpportunityImpsPerDay);

        return impressionsDistance + marketSegmentDistance + dayStartDistance + impressionsPerDayDistance;

    }

}
