package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.learn.CampaignBidBundleHistory;
import tau.tac.adx.agents.bob.learn.KNNBidBundle;
import tau.tac.adx.agents.bob.utils.Utils;

import java.util.List;
import java.util.logging.Logger;

/*This class contains all bid bundle strategies function we need to calculate out bid*/
public class BidBundleStrategy {

    private KNNBidBundle knnBidBundle;

    private final Logger log = Logger.getLogger(BidBundleStrategy.class.getName());

    @Inject
    public BidBundleStrategy(KNNBidBundle knnBidBundle) {
        this.knnBidBundle = knnBidBundle;
    }

    /*This routine calculates our stable bid bundle.
    * we calculate bidCalc as a function of all the parameters in the bidBundleData*/
    public double calcStableBid(BidBundleData bidBundleData, int dayInGame, CampaignData campaign) {
        double epsilon = 0.7;
        /*The initial value of the bid is the average cost for each impression (budget/reach) */
        double bidCalc = bidBundleData.getAvgPerImp();
        double bidBundleMillis;
        double marketSegPopRatio =1D/ bidBundleData.getMarketSegmentPopularity();
        bidCalc = bidCalc * bidBundleData.getGameDayFactor() * bidBundleData.getDaysLeftFactor() * marketSegPopRatio
                * bidBundleData.getAdInfoFactor() * bidBundleData.getRandomFactor() * ((double) bidBundleData
                .getImprCompetition() / 100);
        /*After the first ten days of the game we test if there is enough data to calculate the Knn factor*/
        if (dayInGame > 10) {
            if (isBidBundleHistoryOver4( campaign, epsilon)) {
                double knnBid = calcKNNFactor( dayInGame, campaign, epsilon);
                log.info("The bid bundle before KNN factor is " + bidCalc);
                bidBundleMillis = 0.95 * bidCalc + 0.05 * knnBid;
                log.info("The bid bundle after KNN factor is " + bidBundleMillis);
            }
            else {
                bidBundleMillis = bidCalc;
            }
        }
        else {
            bidBundleMillis = bidCalc;
        }
        return bidBundleMillis;
    }

    /*at the first 12 days of the game, our main purpose is to get high rating so we can get more campaigns, so we
    use this strategy at these days - we calculate the stable bid (same strategy as for the rest of the game), and
    multiply it random double between 1 to 1.1*/
    public double calcFirstDayBid(BidBundleData bidBundleData, int dayInGame,  CampaignData campaign) {
        double stableBid = calcStableBid(bidBundleData, dayInGame,  campaign);
     //   double avgRevenuePerImp = bidBundleData.getAvgPerImp();
        return stableBid* Utils.randDouble(1,1.1);
    }

    /*At the last 8 days of the game we dont care anymore about our rating and we try to get as much profit as we
    can, so we bid a lower then usual and try to get impressions in lower cost*/
    public double calcLastDaysBid(BidBundleData bidBundleData, int dayInGame,  CampaignData campaign){
        double stableBid = calcStableBid(bidBundleData, dayInGame,  campaign);
        return Math.min(stableBid, bidBundleData.getAvgPerImp());
    }


    /*This routine used to check if we have enough information to calculate the Knn factor, if we dont have at least
    4 similar bid bundles we will ot calculate the factor*/
    private boolean isBidBundleHistoryOver4( CampaignData campaign, double epsilon){
        int bidBundleHistorySize = knnBidBundle.getSimilarBidBundle(campaign, epsilon).size();
        log.info("Bid Bundle similar bid are " + bidBundleHistorySize );
        if( bidBundleHistorySize > 4)
            return true;
        return false;
    }

    /*This routine calculate the Knn factor, based on the day in the game we are we decide on the Knn factor*/
    private double calcKNNFactor(int dayInGame, CampaignData campaign, double epsilon)
    {
        List<CampaignBidBundleHistory> similarBidBundle = knnBidBundle.getSimilarBidBundle(campaign,
                epsilon);
        double similarBidBundleAvg = knnBidBundle.getSimilarBidBundleAvg(similarBidBundle);
        if(dayInGame < 20){
                similarBidBundleAvg *= 1.1;
        }
        else if(dayInGame < 40){
            similarBidBundleAvg *= 1.05;
        }
        else if(dayInGame < 50){
            similarBidBundleAvg *= 1.04;
        }
        log.info ("Bid bundle KNN factor for campaign id : " + campaign.getId() + " is : " + similarBidBundleAvg);
        return similarBidBundleAvg;

    }



}
