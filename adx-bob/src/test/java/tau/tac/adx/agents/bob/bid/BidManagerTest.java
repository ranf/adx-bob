package tau.tac.adx.agents.bob.bid;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.learn.CampaignBidBundleHistory;
import tau.tac.adx.agents.bob.learn.LearnStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class BidManagerTest extends BaseTestCase {

    @InjectMocks
    BidManager bidManager;
    @Mock
    LearnStorage learnStorage;
    @Mock
    CampaignStorage campaignStorage;
    @Mock
    MarketSegmentProbability marketSegmentProbability;
    @Mock
    GameData gameData;

    @Test
    public void testBidManger() {
        assertThat(bidManager).isNotNull();
    }

    @Test
    @Ignore
    public void testAddAdnetReport() {
        AdNetworkReport report = new AdNetworkReport();
        AdNetworkKey key1 = mock(AdNetworkKey.class);
        report.addReportEntry(key1);
        int campaignId = 2412;
        CampaignData campaign = mock(CampaignData.class);
        when(campaignStorage.getMyCampaign(campaignId)).thenReturn(campaign);
        long campaignImpressions = 3293;
        when(campaign.getReachImps()).thenReturn(campaignImpressions);
        long campaignBid = 1241;
        when(learnStorage.getCampaignBid(campaignId)).thenReturn(campaignBid);
        int reportDay = 14;
        when(gameData.getDay()).thenReturn(reportDay);
        double bidOnBundle = 0.34;
        when(learnStorage.getBaseBidFromBundle(reportDay - 1, campaignId)).thenReturn(bidOnBundle);

        bidManager.addAdnetReport(report);

        verify(learnStorage).addBidHistory(argThat(new ArgumentMatcher<CampaignBidBundleHistory>() {
            public boolean matches(Object argument) {
                if (!(argument instanceof CampaignBidBundleHistory))
                    return false;
                CampaignBidBundleHistory history = (CampaignBidBundleHistory) argument;
                return history.getCampaignBid() == campaignBid && history.getCampaignImpressions() ==
                        campaignImpressions;
            }
        }));
    }
}
