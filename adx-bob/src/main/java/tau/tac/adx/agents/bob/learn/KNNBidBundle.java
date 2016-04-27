package tau.tac.adx.agents.bob.learn;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.bid.BidManager;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*This class contains all Knn related functions in order to calculate Knn factor for the bid bundle
* K nearest neighbors is a simple algorithm that stores all available cases and classifies new cases
* based on a similarity measure (e.g., distance functions).
* In our case we seek for similar campaigns from the past and use their average bid bundles as a factor
* in the bid bundle strategy*/

public class KNNBidBundle {

    private MarketSegmentProbability marketSegmentProbability;

	private final Logger log = Logger.getLogger(KNNBidBundle.class.getName());

	@Inject
    public KNNBidBundle(MarketSegmentProbability marketSegmentProbability) {
        this.marketSegmentProbability = marketSegmentProbability;
    }

	/*This routine create a list of all campaigns that are similar to the current campaign
	* the similarity is based on the distance function (distance < apsilon) and on the test isGoodBid*/
	public List<CampaignBidBundleHistory> getSimilarBidBundle(LearnStorage learnStorage, CampaignData currCampaign,
															  double epsilon) {
		List<CampaignBidBundleHistory> similarBidBundle =  new ArrayList<CampaignBidBundleHistory>();
		double distance;
		/*loop over all campaigns bid bundle history
		and decide whether to add the campaign to the similar bids list or not */
		for (CampaignBidBundleHistory campaignbidbundlehistory: learnStorage.getCampaignBidBundleHistories())
		{
			distance = calcBidDistance(campaignbidbundlehistory, currCampaign);
		/*	log.info("The bid bundle distance between campaign id = " + currCampaign.getId() + " and campaign history" +
				" id = " +
					campaignbidbundlehistory.getId() + " is : " + distance);	*/
			if (distance < epsilon)	{
				if (isGoodBid(campaignbidbundlehistory)) {
					similarBidBundle.add(campaignbidbundlehistory);
				}
			}
		}
		return similarBidBundle;
	}

	/* This routine calculate the average bids of all similar bid bundle, the result will be our Knn factor */
	public double getSimilarBidBundleAvg(List<CampaignBidBundleHistory> BidBundleHistoriesList){
		double sum =0;
		for(CampaignBidBundleHistory campaignbidbundlehistory: BidBundleHistoriesList){
			sum +=campaignbidbundlehistory.getBidResults().getBid();
		}
		return sum/BidBundleHistoriesList.size();
	}

    /*This routine gets information about previous campaign and the current campaign
    * and calculate the distance based on the campaign impressions, segment and budget
    * we will use this routine result to calculate the KNNBidBundle factor*/

    public double calcBidDistance(CampaignBidBundleHistory campaignBidBundleHistory, CampaignData campaign){
		/*Impression distance - the distance between how many impression the previous campaign had to get
		* and how many impressions the current campaign need to get*/
        double impressionsDistance = Math.abs((double)campaignBidBundleHistory.getCampaignImpressions() - (double)campaign.getReachImps())
				/((double)campaignBidBundleHistory.getCampaignImpressions() + (double)campaign.getReachImps());
		/*market segment distance - the distance between the previous campaign market segment ratio
		* and the current campaign market segment ratio*/
        double marketSegmentDistance = Math.abs(campaignBidBundleHistory.getMarketSegmentRatio() - marketSegmentProbability.getMarketSegmentsRatio(campaign.getTargetSegment())
				/(campaignBidBundleHistory.getMarketSegmentRatio() + marketSegmentProbability.getMarketSegmentsRatio
				(campaign.getTargetSegment())));
        double currentCampaignBudgetPerImps = campaign.getBudget()/(double)campaign.getReachImps();
        double historyCampaignBudgetPerImps = campaignBidBundleHistory.getBudget()/(double)campaignBidBundleHistory
				.getCampaignImpressions();
		/*Budget per impression distance - the distance between the previous campaign budget per impression
		* and the current campaign budget per impression*/
        double budgetPerImpressionDistance =  Math.abs(historyCampaignBudgetPerImps - currentCampaignBudgetPerImps)
				/(historyCampaignBudgetPerImps + currentCampaignBudgetPerImps);
		/*The total distance between the campaigns (sum of all three factors)*/
		double distance = impressionsDistance + marketSegmentDistance + budgetPerImpressionDistance;

        return distance;
    }

	/*This routine checks is the bid bundle was good, meaning we got more than 0.5 impressions we bet for
	* and the impression cost is more then 0.001*/
	public boolean isGoodBid (CampaignBidBundleHistory campaignBidBundleHistory)
	{
		if (((double)campaignBidBundleHistory.getBidResults().getReport().getBidCount() /
				(double)campaignBidBundleHistory.getBidResults().getReport().getWinCount() > 0.5)
						&&(campaignBidBundleHistory.getBidResults().getReport().getCost() /
							(double)campaignBidBundleHistory.getBidResults().getReport().getWinCount()> 0.0001)){
			return true;
		}
		else{
			return false;
		}
	}

}
