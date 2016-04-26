package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.props.AdxQuery;

@Singleton
public class BidBundleDataBuilder {

    private BidBundleFactorCalculator bidBundleFactorCalculator;
    private GameData gameData;
    private MarketSegmentProbability marketSegmentProbability;
    private CampaignStorage campaignStorage;

    @Inject
    public BidBundleDataBuilder(BidBundleFactorCalculator bidBundleFactorCalculator, GameData gameData,
                                MarketSegmentProbability marketSegmentProbability, CampaignStorage campaignStorage) {
        this.bidBundleFactorCalculator = bidBundleFactorCalculator;
        this.gameData = gameData;
        this.marketSegmentProbability = marketSegmentProbability;
        this.campaignStorage = campaignStorage;
    }

    public BidBundleData build(CampaignData campaign, AdxQuery query, CampaignStorage campaignStorage) {
        BidBundleData data = new BidBundleData();
        data.setAvgPerImp(campaign.getBudget() / campaign.getReachImps());
        data.setDaysLeftFactor(bidBundleFactorCalculator.calcDayLeftFactor(campaign.getCampaignLength(),
                campaign.getDayEnd(), gameData.getDay()));
        //TODO: 0.2 is random value need to check
        data.setMarketSegmentPopularity(bidBundleFactorCalculator.calcMarketSegmentPopularityFactor
                (marketSegmentProbability.getMarketSegmentsRatio(campaign.getTargetSegment()), 0.35));
        data.setCampaignImpRatio(bidBundleFactorCalculator.calcCampaignImpRatio(campaign.impsTogo(), campaign
                .getReachImps(), campaign.getDayEnd(), gameData.getDay(), campaign.getCampaignLength()));
        data.setRandomFactor(bidBundleFactorCalculator.calcRandomFactor(data.getDaysLeftFactor(), data
                .getCampaignImpRatio()));
        data.setGameDayFactor(bidBundleFactorCalculator.calcGameDaysFactor(gameData.getDay()));
        data.setAdInfoFactor(bidBundleFactorCalculator.calcAdInfoFactor(query.getDevice(), query.getAdType(),
                campaign.getMobileCoef(), campaign.getVideoCoef()));
        data.setImprCompetition(campaignStorage.getOverlappingImps(campaign));
        return data;
    }

}
