package tau.tac.adx.agents.bob.campaign;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import java.util.logging.Logger;

@Singleton
public class CampaignBidManager {

    private final Logger log = Logger.getLogger(CampaignBidManager.class.getName());

    private GameData gameData;
    private MarketSegmentProbability marketSegmentProbability;
    private CampaignStorage campaignStorage;

    @Inject
    public CampaignBidManager(GameData gameData, MarketSegmentProbability marketSegmentProbability,
                              CampaignStorage campaignStorage) {
        this.gameData = gameData;
        this.marketSegmentProbability = marketSegmentProbability;
        this.campaignStorage = campaignStorage;
    }

    public long generateCampaignBid(CampaignOpportunityMessage campaignOpportunity) {
        int day = gameData.getDay();
        long cmpimps = campaignOpportunity.getReachImps();

        Double greedyBidMillis = Math.ceil(cmpimps * gameData.getQualityScore()) - 1;
        Double spartanBid = Math.floor(cmpimps * 0.1 / gameData.getQualityScore()) + 1;

        long avgImpressionPerDay = cmpimps /
                (campaignOpportunity.getDayEnd() - campaignOpportunity.getDayStart() + 1);

        Double cmpBidMillis;
        if (gameData.getQualityScore() < 0.6)
            cmpBidMillis = greedyBidMillis;
        else if (campaignOpportunity.getDayEnd() - campaignOpportunity.getDayStart() > 6 || avgImpressionPerDay >
                1500 || day >= 54)
            cmpBidMillis = spartanBid;
        else {
            cmpBidMillis = 0.8 * greedyBidMillis
                    - campaignStorage.getOverlappingImps(campaignStorage.getPendingCampaign()) * 0.3;
            cmpBidMillis *= (0.8
                    + 0.2 * marketSegmentProbability.getMarketSegmentsRatio(campaignOpportunity.getTargetSegment()));
            if (day > 5)
                cmpBidMillis *= 0.5 + getActivityRatio(day + 1);
            cmpBidMillis *= Utils.randDouble(0.75, 1.25);
            if (cmpBidMillis > greedyBidMillis)
                cmpBidMillis = greedyBidMillis;
            if (cmpBidMillis < spartanBid)
                cmpBidMillis = spartanBid;
        }

        return cmpBidMillis.longValue();
    }

    private double getActivityRatio(int effectiveDay) {
        long my = campaignStorage.getMyActiveCampaigns(effectiveDay).size();
        long all = campaignStorage.getAllActiveCampaigns(effectiveDay).size();
        int numberOfgents = campaignStorage.getNumberOfAgents();
        System.out.println("my campaign count = " + my);
        System.out.println("all campaign count = " + all);
        return (double) my * numberOfgents / all;
    }
}
