package tau.tac.adx.agents.bob.bid;

import tau.tac.adx.agents.CampaignData;

public class BidBundleSrtategy
{
  BidBundleData Current_bid_bundle_parm;
  CampaignData[] running_Campaigns;
  
  public double calc_stable_bid(BidBundleData parambidBundleData)
  {
    double bidCalc = parambidBundleData.get_avgPerImp();
    double marketSegPopRatio = 1.0D / parambidBundleData.get_marketSegmentPopularity();
    bidCalc = bidCalc * parambidBundleData.get_gameDayFactor() * parambidBundleData.get_daysLeftFactor() * marketSegPopRatio * 
      parambidBundleData.get_adInfoFactor() * parambidBundleData.get_randomFactor();
    return bidCalc;
  }
  
  public double calc_first_days_bid(BidBundleData parambidBundle, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double stableBid = calc_stable_bid(parambidBundle);
    double avgRevenuePerImp = parambidBundle.get_avgPerImp();
    return Math.max(stableBid, 1.0D * avgRevenuePerImp);
  }
}
