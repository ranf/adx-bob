package tau.tac.adx.agents.bob.campaign;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.ucs.UcsManager;
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
		fail("Not yet implemented");
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
