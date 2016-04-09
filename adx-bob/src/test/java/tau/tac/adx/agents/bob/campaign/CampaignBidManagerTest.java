package tau.tac.adx.agents.bob.campaign;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

	@Test
	public void testCampaignManager() {
		assertThat(campaignBidManager).isNotNull();
	}

	@Test
	public void testGenerateCampaignBidValid() {
		long reach = 1030;
		double quality = 0.95;
		long maximalBid = (long)(reach*quality);
		long minimalBid = (long)(reach*0.1/quality);
		CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
		when(gameData.getQualityScore()).thenReturn(quality);
		when(msg.getReachImps()).thenReturn(reach);
		
		long bid = campaignBidManager.generateCampaignBid(msg);

		assertThat(bid).isLessThan(maximalBid);
		assertThat(bid).isGreaterThan(minimalBid);
	}
}
