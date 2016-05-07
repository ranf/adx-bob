package tau.tac.adx.agents.bob.learn;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class LearnManagerTest extends BaseTestCase {

    @InjectMocks
    private LearnManager learnManager;
    @Mock
    private CampaignStorage campaignStorage;
    @Mock
    private LearnStorage learnStorage;
    @Mock
    private MarketSegmentProbability marketSegmentProbability;

    @Test
    public void testLearnManager() {
        assertThat(learnManager).isNotNull();
    }

    @Test
    public void testStoreEndingCampaign() {
        int campaignId = 12341;
        int day = 24;
        long reach = 10000;
        int impsToGo = 1000;
        double err = Utils.effectiveReachRatio(9000, 10000);
        double costs = 1000;
        long campaignBid = 5200;
        double campaignBudget = 5000;
        CampaignData endingCampaign = mock(CampaignData.class);
        when(endingCampaign.getId()).thenReturn(campaignId);
        when(endingCampaign.getReachImps()).thenReturn(reach);
        when(endingCampaign.impsTogo()).thenReturn(impsToGo);
        when(endingCampaign.getDayStart()).thenReturn(21L);
        when(endingCampaign.getBudget()).thenReturn(campaignBudget);
        when(campaignStorage.getMyEndingCampaigns(day)).thenReturn(Lists.newArrayList(endingCampaign));
        when(marketSegmentProbability.getMarketSegmentsRatio(any())).thenReturn(0.5);
        when(learnStorage.getCampaignBidBundlesCost(campaignId)).thenReturn(costs);
        when(learnStorage.getCampaignBid(campaignId)).thenReturn(campaignBid);

        learnManager.storeEndingCampaigns(day);

        verify(learnStorage).addCampaignBidHistory(argThat(new ArgumentMatcher<CampaignOpportunityBidHistory>() {
            public boolean matches(Object argument) {
                if (!(argument instanceof CampaignOpportunityBidHistory))
                    return false;
                CampaignOpportunityBidHistory history = (CampaignOpportunityBidHistory) argument;
                return history.getCampaignBid() == campaignBid && history.getCampaignImpressions() == reach && history
                        .getCompletedPart() == err && history.getDayStart() == 21 && history.getProfit() ==
                        campaignBudget * err - costs;
            }
        }));
    }
}
