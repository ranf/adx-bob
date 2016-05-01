package tau.tac.adx.agents.bob.campaign;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.learn.KnnCampaignOpportunityBid;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CampaignBidManagerTest extends BaseTestCase {

    @InjectMocks
    private CampaignBidManager campaignBidManager;
    @Mock
    private GameData gameData;
    @Mock
    private MarketSegmentProbability marketSegmentProbability;
    @Mock
    private CampaignStorage campaignStorage;
    @Mock
    private Random random;
    @Mock
    KnnCampaignOpportunityBid knnCampaignOpportunityBid;

    @Test
    public void testCampaignManager() {
        assertThat(campaignBidManager).isNotNull();
    }

    @Test
    public void testGenerateCampaignBidValid() {
        long reach = 1030;
        double quality = 0.95;
        long maximalBid = (long) (reach * quality);
        long minimalBid = (long) (reach * 0.1 / quality);
        CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
        when(gameData.getQualityScore()).thenReturn(quality);
        when(msg.getReachImps()).thenReturn(reach);
        when(gameData.getDay()).thenReturn(1);

        long bid = campaignBidManager.generateCampaignBid(msg);

        assertThat(bid).isLessThan(maximalBid).isGreaterThan(minimalBid);
    }

    @Test
    public void testGenerateCampaignBidNotExtreme() {
        long reach = 1030;
        double quality = 0.95;
        long maximalBid = (long) (reach * quality);
        long minimalBid = (long) (reach * 0.1 / quality);
        CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
        when(gameData.getQualityScore()).thenReturn(quality);
        when(msg.getReachImps()).thenReturn(reach);
        when(gameData.getDay()).thenReturn(1);

        long bid = campaignBidManager.generateCampaignBid(msg);

        long safeGap = 10;
        assertThat(bid).isLessThan(maximalBid - safeGap).isGreaterThan(minimalBid + safeGap);

    }

    @Test
    public void testGreedyBidForLowQuality() {
        double quality = 0.4;
        long reach = 1030;
        long greedyBid = (long) Math.ceil(reach * quality) - 1;
        int day = 20;
        CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
        when(gameData.getQualityScore()).thenReturn(quality);
        when(msg.getReachImps()).thenReturn(reach);
        when(random.nextDouble()).thenReturn(0.5);
        when(campaignStorage.getAllActiveCampaigns(anyInt())).thenReturn(Arrays.asList(mock(CampaignData.class)));
        when(gameData.getDay()).thenReturn(day);

        long bid = campaignBidManager.generateCampaignBid(msg);

        assertThat(bid).isEqualTo(greedyBid);
    }

    @Test
    public void testGenerateCampaignBidSpartanLateGame() {
        long reach = 1030;
        double quality = 0.95;
        int regular = 20;
        int late = 55;
        long spartanBid = (long) Math.floor(reach * 0.1 / quality) + 1;
        CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
        when(gameData.getQualityScore()).thenReturn(quality);
        when(msg.getReachImps()).thenReturn(reach);
        when(random.nextDouble()).thenReturn(0.5);
        when(campaignStorage.getAllActiveCampaigns(anyInt())).thenReturn(Arrays.asList(mock(CampaignData.class)));

        when(gameData.getDay()).thenReturn(regular).thenReturn(late);
        long regularBid = campaignBidManager.generateCampaignBid(msg);
        long lateBid = campaignBidManager.generateCampaignBid(msg);

        verify(gameData, times(2)).getDay();
        assertThat(lateBid).isEqualTo(spartanBid).isLessThan(regularBid);
    }
}
