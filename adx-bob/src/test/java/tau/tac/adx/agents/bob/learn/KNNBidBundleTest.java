package tau.tac.adx.agents.bob.learn;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is for testing the Knn Bid bundle logic
 */
public class KNNBidBundleTest extends BaseTestCase {

    @InjectMocks
    private KNNBidBundle knnBidBundle;
    @Mock
    private MarketSegmentProbability marketSegmentProbability;
    @Mock
    private LearnStorage learnStorage;

    @Test
    public void testKnnBidBundle(){
        assertThat(knnBidBundle).isNotNull();
    }

    @Test
    public void testGetSimilarBidBundle(){
        when(learnStorage.getCampaignBidBundleHistories()).thenReturn(generateBidBundleHistoryList());
        CampaignData campaign = generateCampaignData();
        MarketSegmentProbability marketSegmentProbability = generateMarketSegmentProbability();
        assertThat(knnBidBundle.getSimilarBidBundle(campaign, 0.7)).isNotEmpty().isNotNull();
    }

    @Test
    public void testCalcBidDistance()
    {
        int i;
        CampaignData campaign = generateCampaignData();
        double distance;
        when(learnStorage.getCampaignBidBundleHistories()).thenReturn(generateBidBundleHistoryList());
        for (i = 0; i < 8; i++)
        {
            CampaignBidBundleHistory campaignBidBundleHistory = learnStorage.getCampaignBidBundleHistories().get(i);
            distance = knnBidBundle.calcBidDistance(campaignBidBundleHistory, campaign);
            assertThat(distance).isGreaterThan(0.001);
        }
    }

    private List<CampaignBidBundleHistory> generateBidBundleHistoryList(){
        return Lists.newArrayList(new CampaignBidBundleHistory(1000,200,0.25,(long)0.02,10,200, 12345,
                        generateBidResult(0.012, 1000, 700, 0.012) ),
                new CampaignBidBundleHistory(2000,200,0.15,(long)0.012,12,500, 12346,
                        generateBidResult(0.0142, 1000, 600, 0.01) ),
                new CampaignBidBundleHistory(1001,199,0.25,(long)0.02,14,180, 12347,
                        generateBidResult(0.013, 800, 800, 0.0184) ),
                new CampaignBidBundleHistory(1500,500,0.18,(long)0.0258,11,250, 12348,
                        generateBidResult(0.152, 1500, 200, 0.084) ),
                new CampaignBidBundleHistory(1000,500,0.4,(long)0.0102,9,1000, 12349,
                        generateBidResult(0.247, 500, 100, 0.024) ),
                new CampaignBidBundleHistory(900,90,0.30,(long)0.301,5,200, 22345,
                        generateBidResult(0.034, 800, 800, 0.102) ),
                new CampaignBidBundleHistory(2000,500,0.25,(long)0.02001,20,100, 32345,
                        generateBidResult(0.021, 1500, 100, 0.314) ),
                new CampaignBidBundleHistory(1000,200,0.22,(long)0.0195,32,400, 42345,
                        generateBidResult(0.051, 800, 700, 0.0547) )
                );
    }

    private BidResult generateBidResult(double bid, int bidCount, int winCount, double cost)
    {
        BidResult bidResult = new BidResult(bid, new AdNetworkReportEntry() {{
            setBidCount(bidCount);
            setCost(cost);
            setWinCount(winCount);
        }});

        return bidResult;
    }

    private CampaignData generateCampaignData()
    {
        long reach = 1000;
        double budget = 220;
        CampaignData campaign = mock(CampaignData.class);
        when(campaign.getReachImps()).thenReturn(reach);
        when(campaign.getBudget()).thenReturn(budget);
        return campaign;
    }

    private MarketSegmentProbability generateMarketSegmentProbability()
    {
        double marketSegmentsRatio = 0.02;
        MarketSegmentProbability marketSegmentProbability = mock(MarketSegmentProbability.class);
        when(marketSegmentProbability.getMarketSegmentsRatio(any())).thenReturn(marketSegmentsRatio);
        return marketSegmentProbability;
    }




}
