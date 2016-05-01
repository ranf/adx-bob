package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.props.AdxQuery;

/*This class is used whenever we want to build all the data we need to calculate the bid bundle*/
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

    /*This routine creates new BidBundleData type by calculating each factor is the struct*/
    public BidBundleData build(CampaignData campaign, AdxQuery query) {
        BidBundleData data = new BidBundleData();
        data.setAvgPerImp(campaign.getBudget() / campaign.getReachImps());
        data.setDaysLeftFactor(bidBundleFactorCalculator.calcDayLeftFactor(campaign.getCampaignLength(),
                campaign.getDayEnd(), gameData.getDay()));
        data.setMarketSegmentPopularity(bidBundleFactorCalculator.calcMarketSegmentPopularityFactor
                (marketSegmentProbability.getMarketSegmentsRatio(campaign.getTargetSegment()), 0.35));
        data.setCampaignImpRatio(bidBundleFactorCalculator.calcCampaignImpRatio(campaign.impsTogo(), campaign
                .getReachImps(), campaign.getDayEnd(), gameData.getDay(), campaign.getCampaignLength()));
        data.setRandomFactor(bidBundleFactorCalculator.calcRandomFactor(data.getCampaignImpRatio()));
        data.setGameDayFactor(bidBundleFactorCalculator.calcGameDaysFactor(gameData.getDay()));
        data.setAdInfoFactor(bidBundleFactorCalculator.calcAdInfoFactor(query.getDevice(), query.getAdType(),
                campaign.getMobileCoef(), campaign.getVideoCoef()));
        data.setImprCompetition(campaignStorage.getOverlappingImps(campaign));
        return data;
    }

}
