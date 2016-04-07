package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umich.eecs.tac.props.Ad;
import java.util.Random;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

@Singleton
public class BidManager
{
  private GameData gameData;
  
  @Inject
  public BidManager(GameData gameData)
  {
    this.gameData = gameData;
  }
  
  public AdxBidBundle BuildBidAndAds()
  {
    AdxBidBundle bidBundle = new AdxBidBundle();
    
    int dayBiddingFor = this.gameData.day + 1;
    
    Random random = new Random();
    
    double rbid = 10.0D * random.nextDouble();
    if ((dayBiddingFor >= this.gameData.getCurrCampaign().getDayStart()) && 
      (dayBiddingFor <= this.gameData.getCurrCampaign().getDayEnd()) && 
      (this.gameData.getCurrCampaign().impsTogo() > 0))
    {
      int entCount = 0;
      AdxQuery[] arrayOfAdxQuery;
      int j = (arrayOfAdxQuery = this.gameData.getCurrCampaign().getCampaignQueries()).length;
      for (int i = 0; i < j; i++)
      {
        AdxQuery query = arrayOfAdxQuery[i];
        if (this.gameData.getCurrCampaign().impsTogo() - entCount > 0)
        {
          if (query.getDevice() == Device.pc)
          {
            if (query.getAdType() == AdType.text) {
              entCount++;
            } else {
              entCount = (int)(entCount + this.gameData.getCurrCampaign().getVideoCoef());
            }
          }
          else if (query.getAdType() == AdType.text) {
            entCount = (int)(entCount + this.gameData.getCurrCampaign().getMobileCoef());
          } else {
            entCount = (int)(entCount + (this.gameData.getCurrCampaign().getVideoCoef() + this.gameData.getCurrCampaign().getMobileCoef()));
          }
          bidBundle.addQuery(query, rbid, new Ad(null), 
            this.gameData.getCurrCampaign().getId(), 1);
        }
      }
      double impressionLimit = this.gameData.getCurrCampaign().impsTogo();
      double budgetLimit = this.gameData.getCurrCampaign().budget;
      bidBundle.setCampaignDailyLimit(this.gameData.getCurrCampaign().getId(), 
        (int)impressionLimit, budgetLimit);
      
      System.out.println("Day " + this.gameData.day + ": Updated " + entCount + 
        " Bid Bundle entries for Campaign id " + this.gameData.getCurrCampaign().getId());
    }
    this.gameData.bidBundle = bidBundle;
    return bidBundle;
  }
}
