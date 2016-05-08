package tau.tac.adx.agents.bob.campaign;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.learn.LearnStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.ucs.UcsManager;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.demand.*;

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
        when(gameData.getPublisherNames()).thenReturn(new String[]{"pub1", "pub2"});

        campaignManager.handleInitialCampaignMessage(msg);

        verify(campaignStorage).acknowledgeCampaign(any());
        verify(campaignStorage).addMyCampaign(argThat(new ArgumentMatcher<CampaignData>() {
            @Override
            public boolean matches(Object argument) {
                if (!(argument instanceof CampaignData))
                    return false;
                CampaignData campaign = (CampaignData) argument;
                return campaign.getId() == campaignId && campaign.getBudget() * 1000 == campaignBudget
                        && campaign.getCampaignQueries().length == 8; //one per publisher
            }
        }));
        verify(learnStorage).saveCampaignBid(campaignId, campaignBudget);
    }

    @Test
    public void testHandleCampaignOpportunityMessage() {
        CampaignOpportunityMessage msg = mock(CampaignOpportunityMessage.class);
        long campaignBid = 3030;
        double ucsBid = 1.2;
        int campaignId = 2323;
        when(msg.getId()).thenReturn(campaignId);
        when(campaignBidManager.generateCampaignBid(any())).thenReturn(campaignBid);
        when(ucsManager.generateUcsBid()).thenReturn(ucsBid);

        AdNetBidMessage result = campaignManager.handleCampaignOpportunityMessage(msg);

        verify(campaignStorage).acknowledgeCampaign(argThat(new ArgumentMatcher<CampaignData>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof CampaignData && ((CampaignData) argument).getId() == campaignId;
            }
        }));
        verify(campaignStorage).setPendingCampaign(argThat(new ArgumentMatcher<CampaignData>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof CampaignData && ((CampaignData) argument).getId() == campaignId;
            }
        }));
        verify(learnStorage).saveCampaignBid(campaignId, campaignBid);
        assertThat(result.getUcsBid()).isEqualTo(ucsBid);
        assertThat(result.getCampaignBudget()).isEqualTo(campaignBid);
        assertThat(result.getCampaignId()).isEqualTo(campaignId);
    }

    @Test
    public void testHandleCampaignReport() {
        CampaignReport report = new CampaignReport();
        int campaignId1 = 13512;
        int campaignId2 = 532111;
        CampaignStats stats1 = new CampaignStats(0.1, 0.2, 0.3);
        CampaignStats stats2 = new CampaignStats(10.3, 8.2, 5.1);
        report.addReportEntry(new CampaignReportKey(campaignId1)).setCampaignStats(stats1);
        report.addReportEntry(new CampaignReportKey(campaignId2)).setCampaignStats(stats2);

        campaignManager.handleCampaignReport(report);

        verify(campaignStorage).setCampaignStats(eq(campaignId1), argThat(new ArgumentMatcher<CampaignStats>() {
            @Override
            public boolean matches(Object argument) {
                return argument.toString().equals(stats1.toString());
            }
        }));
        verify(campaignStorage).setCampaignStats(eq(campaignId2), argThat(new ArgumentMatcher<CampaignStats>() {
            @Override
            public boolean matches(Object argument) {
                return argument.toString().equals(stats2.toString());
            }
        }));
    }

    @Test
    public void testHandleAdNetworkDailyNotificationWonCampaign() {
        AdNetworkDailyNotification msg = mock(AdNetworkDailyNotification.class);
        int campaignId = 2323;
        long campaignBudget = 35211;
        when(msg.getCampaignId()).thenReturn(campaignId);
        when(msg.getCostMillis()).thenReturn(campaignBudget);
        when(gameData.getPublisherNames()).thenReturn(new String[]{"pub1", "pub2"});
        when(campaignStorage.getPendingCampaign()).thenReturn(new CampaignData() {{
            setId(campaignId);
        }});

        campaignManager.handleAdNetworkDailyNotification(msg);

        ArgumentCaptor<CampaignData> campaignCaptor = ArgumentCaptor.forClass(CampaignData.class);
        verify(campaignStorage, times(1)).addMyCampaign(campaignCaptor.capture());
        CampaignData savedCampaign = campaignCaptor.getValue();
        assertThat(savedCampaign.getCampaignQueries().length).isEqualTo(8);
        assertThat(savedCampaign.getId()).isEqualTo(campaignId);
        assertThat(savedCampaign.getBudget()).isEqualTo(campaignBudget / 1000.0);
    }

    @Test
    public void testHandleAdNetworkDailyNotificationLostCampaign() {
        AdNetworkDailyNotification msg = mock(AdNetworkDailyNotification.class);
        String winner = "someone";
        int campaignId = 3264423;
        when(msg.getWinner()).thenReturn(winner);
        when(msg.getCampaignId()).thenReturn(campaignId);
        campaignManager.handleAdNetworkDailyNotification(msg);

        verify(campaignStorage).setCampaignWinner(campaignId, winner);
        verify(campaignStorage, never()).addMyCampaign(any());
    }

    @Test
    public void testHandleAdNetworkDailyNotificationUpdateInfo() {
        AdNetworkDailyNotification msg = mock(AdNetworkDailyNotification.class);
        int campaignId = 3264423;
        double quality = 1.1;
        double ucsLevel = 0.91;
        double ucsBid = 0.2;
        when(msg.getCampaignId()).thenReturn(campaignId);
        when(msg.getQualityScore()).thenReturn(quality);
        when(msg.getServiceLevel()).thenReturn(ucsLevel);
        when(gameData.getUcsBid()).thenReturn(ucsBid);
        campaignManager.handleAdNetworkDailyNotification(msg);

        verify(gameData).setQualityScore(quality);
        verify(ucsManager).addToCurrentGameUcsBids(ucsLevel, ucsBid);
    }
}
