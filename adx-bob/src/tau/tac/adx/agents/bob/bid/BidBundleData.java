package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Random;
import java.util.Set;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.devices.Device;
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
  public BidBundleData(MarketSegmentProbability marketSegmentProbability)
  {
    this.marketSegmentProbability = marketSegmentProbability;
  }
  
  public void set_avgPerImp(CampaignData currCamp)
  {
    this.avgPerImp = (currCamp.getBudget() / currCamp.getReachImps());
  }
  
  public double get_avgPerImp()
  {
    return this.avgPerImp;
  }
  
  public void set_daysLeftFactor(CampaignData currCamp,GameData gameData)
  {
	  long totalCampaignDays = currCamp.getCampaignLength();
	  long daysLeft = currCamp.getDayEnd() - gameData.day;
	  
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
  
  public void set_campaignImpRatio(CampaignData currCamp, GameData gameData)
  {
	  this.campaignImpRatio = ((currCamp.impsTogo()/currCamp.getReachImps())/( gameData.day  /currCamp.getCampaignLength()));
  }
  
  public double get_campaignImpRatio()
  {
    return this.campaignImpRatio;
  }
  
  public void set_adInfoFactor(CampaignData currCamp, AdxQuery currAdXQuery)
  {
    if (currAdXQuery.getDevice() == Device.pc)
    {
      if (currAdXQuery.getAdType() == AdType.text) {
        this.adInfofactor = 1.0D;
      } else if (currAdXQuery.getAdType() == AdType.video) {
        this.adInfofactor = currCamp.getVideoCoef();
      }
    }
    else if (currAdXQuery.getDevice() == Device.mobile) {
      if (currAdXQuery.getAdType() == AdType.text) {
        this.adInfofactor = currCamp.getMobileCoef();
      } else if (currAdXQuery.getAdType() == AdType.video) {
        this.adInfofactor = (currCamp.getMobileCoef() * currCamp.getVideoCoef());
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
    double segRatio = this.marketSegmentProbability.getMarketSegmentsRatio(targetSeg).doubleValue();
    if (segRatio > c) {
      this.marketSegmentPopularity = segRatio;
    } else {
      this.marketSegmentPopularity = (segRatio * c);
    }
  }
  
  public double get_marketSegmentPopularity()
  {
    return this.marketSegmentPopularity;
  }
  
  public void set_randomFactor()
  {
    double days_left = get_daysLeftFactor();
    double camp_ratio = get_campaignImpRatio();
    if ((days_left <= 3.0D) && (camp_ratio > 0.55D)) {
      this.randomFactor = randDouble(0.95D, 1.0D);
    } else if (randDouble(0.0D, 1.0D) < 0.2D) {
      this.randomFactor = Math.max(this.marketSegmentPopularity / 2.0D, randDouble(0.0D, 1.0D));
    }
  }
  
  public double get_randomFactor()
  {
    return this.randomFactor;
  }
  
  public void set_gameDayFactor(int daysPassed)
  {
    this.gameDayFactor = (2.3D / daysPassed);
  }
  
  public double get_gameDayFactor()
  {
    return this.gameDayFactor;
  }
  
  public static double randDouble(double min, double max)
  {
    double random = new Random().nextDouble();
    double result = min + random * (max - min);
    
    return result;
  }
}
