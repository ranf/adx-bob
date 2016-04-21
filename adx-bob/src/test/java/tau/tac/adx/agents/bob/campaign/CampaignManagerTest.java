package tau.tac.adx.agents.bob.campaign;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.learn.LearnStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.ucs.UcsManager;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    @Mock
    private LearnStorage learnStorage;

    @Test
    public void testCampaignManager() {
        assertThat(campaignManager).isNotNull();
    }

    @Test
    public void testHandleInitialCampaignMessageStoreAddresses() {
        String adxAgentAddress = "dsgsdg", demandAgentAddress = "f43fc";
        InitialCampaignMessage msg = mock(InitialCampaignMessage.class);
        long campaignBudget = 35211;
        when(msg.getAdxAgentAddress()).thenReturn(adxAgentAddress);
        when(msg.getDemandAgentAddress()).thenReturn(demandAgentAddress);
        when(msg.getBudgetMillis()).thenReturn(campaignBudget);
        when(gameData.getPublisherNames()).thenReturn(new String[]{});

        campaignManager.handleInitialCampaignMessage(msg);

        verify(gameData).setAdxAgentAddress(adxAgentAddress);
        verify(gameData).setDemandAgentAddress(demandAgentAddress);
    }

    @Test
    public void testHandleInitialCampaignMessageSaveCampaign() {
        InitialCampaignMessage msg = mock(InitialCampaignMessage.class);
        int campaignId = 2323;
        long campaignBudget = 35211;
        when(msg.getId()).thenReturn(campaignId);
        when(msg.getBudgetMillis()).thenReturn(campaignBudget);
        when(gameData.getPublisherNames()).thenReturn(new String[]{});

        campaignManager.handleInitialCampaignMessage(msg);

        verify(campaignStorage).addMyCampaign(argThat(new ArgumentMatcher<CampaignData>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof CampaignData && ((CampaignData) argument).getId() == campaignId;
            }
        }));
        verify(learnStorage).saveCampaignBid(campaignId, campaignBudget);
    }

    @Test
    public void testHandleICampaignOpportunityMessage() {
        CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
        long campaignBid = 3030;
        double ucsBid = 1.2;
        int campaignId = 2323;
        when(msg.getId()).thenReturn(campaignId);
        when(campaignBidManager.generateCampaignBid(any())).thenReturn(campaignBid);
        when(ucsManager.generateUcsBid()).thenReturn(ucsBid);

        AdNetBidMessage result = campaignManager.handleICampaignOpportunityMessage(msg);

        verify(campaignStorage).acknowledgeCampaign(any());//TODO verify using builder
        verify(campaignStorage).setPendingCampaign(any());
        verify(learnStorage).saveCampaignBid(campaignId, campaignBid);
        assertThat(result.getUcsBid()).isEqualTo(ucsBid);
        assertThat(result.getCampaignBudget()).isEqualTo(campaignBid);
    }
}
