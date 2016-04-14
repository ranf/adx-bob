package tau.tac.adx.agents.bob.campaign;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

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

		long safeGap = 3;
		assertThat(bid).isLessThan(maximalBid - safeGap).isGreaterThan(minimalBid + safeGap);

	}

	@Test
	public void testGenerateCampaignBidLateGameLowerBid() {
		long reach = 1030;
		double quality = 0.95;
		int early = 10;
		int late = 50;
		CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
		when(gameData.getQualityScore()).thenReturn(quality);
		when(msg.getReachImps()).thenReturn(reach);
		when(random.nextDouble()).thenReturn(0.5);
		when(campaignStorage.getAllActiveCampaigns(anyInt())).thenReturn(Arrays.asList(mock(CampaignData.class)));
		
		when(gameData.getDay()).thenReturn(early).thenReturn(late);
		long earlyBid = campaignBidManager.generateCampaignBid(msg);
		long lateBid = campaignBidManager.generateCampaignBid(msg);

		assertThat(earlyBid).isLessThan(lateBid);
	}
}
