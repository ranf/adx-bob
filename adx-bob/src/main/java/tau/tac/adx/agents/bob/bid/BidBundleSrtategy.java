package tau.tac.adx.agents.bob.bid;

import tau.tac.adx.agents.CampaignData;

//TODO all class is not used
public class BidBundleSrtategy
{
  BidBundleData Current_bid_bundle_parm;
  CampaignData[] running_Campaigns;
  
  public double calc_stable_bid(BidBundleData parambidBundleData)
  {
    double bidCalc = parambidBundleData.getAvgPerImp();
    double marketSegPopRatio = 1.0D / parambidBundleData.get_marketSegmentPopularity();
    bidCalc = bidCalc * parambidBundleData.get_gameDayFactor() * parambidBundleData.getDaysLeftFactor() * marketSegPopRatio * 
      parambidBundleData.get_adInfoFactor() * parambidBundleData.get_randomFactor();
    return bidCalc;
  }
  
  public double calc_first_days_bid(BidBundleData parambidBundle)
  {
    double stableBid = calc_stable_bid(parambidBundle);
    double avgRevenuePerImp = parambidBundle.getAvgPerImp();
    return Math.max(stableBid, 1.0D * avgRevenuePerImp);
  }
}
