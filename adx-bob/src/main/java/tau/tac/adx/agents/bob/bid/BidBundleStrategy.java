package tau.tac.adx.agents.bob.bid;

import tau.tac.adx.agents.CampaignData;
//import tau.tac.adx.props.AdxBidBundle;

public class BidBundleStrategy
{
  BidBundleData Current_bid_bundle_parm;
  CampaignData[] running_Campaigns;
  
  public double calcStableBid(BidBundleData bidBundleData)
  {
    double bidCalc = bidBundleData.getAvgPerImp();
    double marketSegPopRatio = 1.0D / bidBundleData.getMarketSegmentPopularity();
    bidCalc = bidCalc * bidBundleData.getGameDayFactor() * bidBundleData.getDaysLeftFactor() * marketSegPopRatio * 
    		bidBundleData.getAdInfoFactor() * bidBundleData.getRandomFactor();
    return bidCalc;
  }
  
  public double calcFirstDayBid(BidBundleData bidBundleData)
  {
    double stableBid = calcStableBid(bidBundleData);
    double avgRevenuePerImp = bidBundleData.getAvgPerImp();
    return Math.max(stableBid, 1.2 * avgRevenuePerImp);
  }
}
