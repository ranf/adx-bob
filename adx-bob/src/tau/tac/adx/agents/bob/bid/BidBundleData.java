package tau.tac.adx.agents.bob.bid;

import java.util.Random;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import tau.tac.adx.agents.CampaignData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.devices.Device;
import tau.tac.adx.ads.properties.AdType;
//import tau.tac.adx.parser.Auctions.AdType;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;



@Singleton
public class BidBundleData
{
  double avgPerImp;
  double daysLeftFactor;
  double campaignImpRatio;
  double randomFactor;
  double gameDayFactor;
  double marketSegmentPopularity;
  double adInfofactor;
  private MarketSegmentProbability marketSegmentProbability;
  
  @Inject
  public BidBundleData(MarketSegmentProbability marketSegmentProbability){
	this.marketSegmentProbability = marketSegmentProbability;
	  
  }
  
  public void set_avgPerImp(double budget, long reachImps)
  {
    this.avgPerImp = (budget / reachImps);
  }
  
  public double get_avgPerImp()
  {
    return this.avgPerImp;
  }
  
  public void set_daysLeftFactor(int daysLeft, int totalCampaignDays)
  {
    if (daysLeft == 1) {
      this.daysLeftFactor = 2.7D;
    }
    if (daysLeft == 2) {
      this.daysLeftFactor = 1.8D;
    } else {
      this.daysLeftFactor = (1.2D * (1 - (totalCampaignDays - daysLeft) / 10));
    }
  }
  
  public double get_daysLeftFactor()
  {
    return this.daysLeftFactor;
  }
  
  public void set_campaignImpRatio(int ImpLeft, int TotalImp, int daysLeft, int totalCampaignDays)
  {
    this.campaignImpRatio = ((ImpLeft / TotalImp) / (daysLeft / totalCampaignDays));
  }
  
  public double get_campaignImpRatio()
  {
    return this.campaignImpRatio;
  }
  
  public void set_adInfoFactor (CampaignData currCamp ,AdxQuery currAdXQuery)
  {
	 if (currAdXQuery.getDevice() == Device.pc) 
	 {
		if (currAdXQuery.getAdType() == AdType.text)
		{
			this.adInfofactor = 1;
		}
		else if (currAdXQuery.getAdType() == AdType.video)
		{
			this.adInfofactor = currCamp.getVideoCoef();
		}
	 }
	 
	 else if (currAdXQuery.getDevice() == Device.mobile) 
	 {
		if (currAdXQuery.getAdType() == AdType.text)
		{
			this.adInfofactor = currCamp.getMobileCoef();
		}
		else if (currAdXQuery.getAdType() == AdType.video)
		{
			this.adInfofactor = currCamp.getMobileCoef() * currCamp.getVideoCoef() ;
		}
	 }  
	  
  }
  
  public double get_adInfoFactor()
  {
	  return this.adInfofactor;
  }
  
  public void set_marketSegmentPopularity(CampaignData currCamp, double c)
  {
	  Set<MarketSegment> targetSeg = currCamp.getTargetSegment();
	  double segRatio = marketSegmentProbability.getMarketSegmentsRatio(targetSeg);
	  
	  if (segRatio > c)
	  {
		  this.marketSegmentPopularity = segRatio;
	  }
	  
	  else
	  {
		  this.marketSegmentPopularity = segRatio * c;
	  }
  }
  
  public double get_marketSegmentPopularity()
  {
	  return this.marketSegmentPopularity;
  }
  
  public void set_randomFactor()
  {
	  double days_left;
	  double camp_ratio;
	  days_left = get_daysLeftFactor();
	  camp_ratio =  get_campaignImpRatio();
	  
	  if ((days_left <= 3) && (camp_ratio > 0.55))
	  {
		  this.randomFactor = randDouble(0.95, 1);
	  }
	  
	  else if (randDouble(0, 1) < 0.2)
	  {
		  this.randomFactor = Math.max(this.marketSegmentPopularity / 2, randDouble(0,1));
	  }	  
  }
  
  public double get_randomFactor()
  {
    return this.randomFactor;
  }
  
  public static double randDouble(double min, double max) 
  {
	    double random = new Random().nextDouble();
	    double result = min + (random * (max - min));

	    return result;
  }
  
  
}
