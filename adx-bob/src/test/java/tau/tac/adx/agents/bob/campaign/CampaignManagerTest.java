package tau.tac.adx.agents.bob.campaign;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.ucs.UcsManager;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

public class CampaignManagerTest extends BaseTestCase {

	@InjectMocks
	private CampaignManager campaignManager;

	@Mock
	private GameData gameData;
	@Mock
	private UcsManager ucsManager;
	@Mock
	private CampaignBidManager campaignBidManager;
	@Mock
	private CampaignStorage campaignStorage;

	@Test
	public void testCampaignManager() {
		assertThat(campaignManager).isNotNull();
	}

	@Test
	public void testHandleInitialCampaignMessageStoreAddresses() {
		String adxAgentAddress = "dsgsdg", demandAgentAddress = "f43fc";
		InitialCampaignMessage msg = mock(InitialCampaignMessage.class);
		when(msg.getAdxAgentAddress()).thenReturn(adxAgentAddress);
		when(msg.getDemandAgentAddress()).thenReturn(demandAgentAddress);
		when(gameData.getPublisherNames()).thenReturn(new String[] {});

		campaignManager.handleInitialCampaignMessage(msg);

		verify(gameData).setAdxAgentAddress(adxAgentAddress);
		verify(gameData).setDemandAgentAddress(demandAgentAddress);
	}

	@Test
	public void testHandleInitialCampaignMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testHandleICampaignOpportunityMessage() {
		CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
		long campaignBid = 3030;
		double ucsBid = 1.2;
		when(campaignBidManager.generateCampaignBid(any())).thenReturn(campaignBid);
		when(ucsManager.generateUcsBid()).thenReturn(ucsBid);
		
		AdNetBidMessage result = campaignManager.handleICampaignOpportunityMessage(msg);
		
		verify(campaignStorage).acknowledgeCampaign(any());//TODO verify using builder
		verify(campaignStorage).setPendingCampaign(any());
		assertThat(result.getUcsBid()).isEqualTo(ucsBid);
		assertThat(result.getCampaignBudget()).isEqualTo(campaignBid);
	}

	@Test
	public void testHandleCampaignReport() {
		fail("Not yet implemented");
	}

	@Test
	public void testHandleAdNetworkDailyNotification() {
		fail("Not yet implemented");
	}
}
