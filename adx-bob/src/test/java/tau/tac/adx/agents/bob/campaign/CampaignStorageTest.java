package tau.tac.adx.agents.bob.campaign;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.MarketSegment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CampaignStorageTest extends BaseTestCase {

    @InjectMocks
    private CampaignStorage campaignStorage;
    @Mock
    private MarketSegmentProbability marketSegmentProbability;

    @Test
    public void testCampaignStorage() {
        assertThat(campaignStorage).isNotNull();
    }

    @Test
    public void testAcknowledgeCampaign() {
        campaignStorage.acknowledgeCampaign(mock(CampaignData.class));
    }

    @Test
    public void testAddMyCampaign() {
        campaignStorage.addMyCampaign(mock(CampaignData.class));
    }

    @Test
    public void testGetMyCampaign() throws Exception {
        CampaignData campaign = mock(CampaignData.class);
        int id = 3252;
        when(campaign.getId()).thenReturn(id);
        campaignStorage.addMyCampaign(campaign);

        CampaignData result = campaignStorage.getMyCampaign(id);

        assertThat(result).isSameAs(campaign);
    }

    @Test
    public void testReset() {
        campaignStorage.addMyCampaign(new CampaignData());
        campaignStorage.acknowledgeCampaign(new CampaignData());
        campaignStorage.setPendingCampaign(new CampaignData());
        int myBefore = campaignStorage.getMyEndingCampaigns(0).size();
        int allBefore = campaignStorage.getAllActiveCampaigns(0).size();
        CampaignData pending = campaignStorage.getPendingCampaign();
        assertThat(myBefore).isEqualTo(allBefore).isEqualTo(1);
        assertThat(pending).isNotNull();

        campaignStorage.reset();
        int myAfter = campaignStorage.getMyEndingCampaigns(0).size();
        int allAfter = campaignStorage.getAllActiveCampaigns(0).size();
        pending = campaignStorage.getPendingCampaign();

        assertThat(myAfter).isEqualTo(allAfter).isEqualTo(0);
        assertThat(pending).isNull();
    }

    @Test
    public void testNumberOfAgents() {
        int id = 3252;
        CampaignData campaign = new CampaignData(){{
            setId(id);
        }};
        campaignStorage.acknowledgeCampaign(campaign);
        campaignStorage.setCampaignWinner(id, "the other one");
        int result = campaignStorage.getNumberOfAgents();
        assertThat(result).isEqualTo(2);//me and the other one
    }

    @Test
    public void testSetCampaignStats() {
        int id = 3252;
        CampaignData campaign = new CampaignData();
        campaign.setId(id);
        CampaignStats stats = new CampaignStats(123.1, 435.3, 123.32);
        campaignStorage.addMyCampaign(campaign);
        campaignStorage.setCampaignStats(id, stats);

        CampaignData updatedCampaign = campaignStorage.getMyCampaign(id);
        CampaignStats newStats = updatedCampaign.getStats();

        assertThat(newStats.getCost()).isEqualTo(stats.getCost());
        assertThat(newStats.getOtherImps()).isEqualTo(stats.getOtherImps());
        assertThat(newStats.getTargetedImps()).isEqualTo(stats.getTargetedImps());
    }

    @Test
    public void testGetOverlappingImps() {
        assertThat(campaignStorage.getOverlappingImps(new CampaignData())).isEqualTo(0);
        CampaignData campaignOfInterest = new CampaignData() {{
            setDayStart(12);
            setDayEnd(15);
            setReachImps(500);
            setId(1241);
            setTargetSegment(Sets.newHashSet(MarketSegment.FEMALE));
        }};
        campaignStorage.acknowledgeCampaign(new CampaignData() {{
            setDayStart(20);
            setDayEnd(25);
            setReachImps(1000);
            setTargetSegment(Sets.newHashSet(MarketSegment.FEMALE));
        }});
        //not overlapping
        assertThat(campaignStorage.getOverlappingImps(new CampaignData())).isEqualTo(0);


        campaignStorage.acknowledgeCampaign(new CampaignData() {{
            setDayStart(10);
            setDayEnd(15);
            setReachImps(1000);
            setTargetSegment(Sets.newHashSet(MarketSegment.FEMALE));
        }});

        long overlappingImpsSimilar = campaignStorage.getOverlappingImps(campaignOfInterest);
        assertThat(overlappingImpsSimilar).isGreaterThan(250).isLessThan(2000);

        campaignStorage.acknowledgeCampaign(new CampaignData() {{
            setDayStart(10);
            setDayEnd(15);
            setReachImps(4000);
            setTargetSegment(Sets.newHashSet(MarketSegment.FEMALE));
        }});

        long overlappingImpsMultiple = campaignStorage.getOverlappingImps(campaignOfInterest);
        assertThat(overlappingImpsMultiple).isGreaterThan(overlappingImpsSimilar).isLessThan(5000);

        campaignStorage.reset();

        campaignStorage.acknowledgeCampaign(new CampaignData() {{
            setDayStart(10);
            setDayEnd(15);
            setReachImps(1000);
            setTargetSegment(Sets.newHashSet(MarketSegment.MALE));
        }});

        long overlappingImpsDifferentSegment = campaignStorage.getOverlappingImps(campaignOfInterest);
        assertThat(overlappingImpsDifferentSegment).isLessThan(overlappingImpsSimilar);
    }

    @Test
    @Ignore
    public void testTotalActiveCampaignsImpsCount() {
        fail("not implemented + actual method not used");
    }

    @Test
    public void testGetMyActiveCampaigns() {
        List<CampaignData> activeCampaigns = Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(1);
                    setDayEnd(5);
                    setReachImps(100);
                }},
                new CampaignData() {{
                    setDayStart(5);
                    setDayEnd(10);
                    setReachImps(100);
                }},
                new CampaignData() {{
                    setDayStart(4);
                    setDayEnd(6);
                    setReachImps(100);
                }});
        List<CampaignData> inactiveCampaigns = Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(4);
                    setDayEnd(6);
                }},
                new CampaignData() {{
                    setDayStart(10);
                    setDayEnd(15);
                    setReachImps(100);
                }});
        activeCampaigns.forEach(c -> campaignStorage.addMyCampaign(c));
        inactiveCampaigns.forEach(c -> campaignStorage.addMyCampaign(c));

        List<CampaignData> result = campaignStorage.getMyActiveCampaigns(5);

        assertThat(result).containsAll(activeCampaigns);
        assertThat(result).doesNotContainAnyElementsOf(inactiveCampaigns);
    }

    @Test
    public void testGetAllActiveCampaigns() throws Exception {
        List<CampaignData> activeCampaigns = Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(1);
                    setDayEnd(5);
                }},
                new CampaignData() {{
                    setDayStart(5);
                    setDayEnd(10);
                }},
                new CampaignData() {{
                    setDayStart(4);
                    setDayEnd(6);
                }});
        List<CampaignData> inactiveCampaigns = Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(2);
                    setDayEnd(3);
                }},
                new CampaignData() {{
                    setDayStart(10);
                    setDayEnd(15);
                }});
        activeCampaigns.forEach(c -> campaignStorage.acknowledgeCampaign(c));
        inactiveCampaigns.forEach(c -> campaignStorage.acknowledgeCampaign(c));

        List<CampaignData> result = campaignStorage.getAllActiveCampaigns(5);

        assertThat(result).containsAll(activeCampaigns);
        assertThat(result).doesNotContainAnyElementsOf(inactiveCampaigns);
    }

    @Test
    public void testPendingCampaign() {
        CampaignData campaign = new CampaignData();
        campaignStorage.setPendingCampaign(campaign);
        CampaignData result = campaignStorage.getPendingCampaign();
        assertThat(result).isSameAs(campaign);
    }

    @Test
    public void testGetMyEndingCampaigns() {
        List<CampaignData> endingCampaigns = Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(1);
                    setDayEnd(5);
                }});
        List<CampaignData> nonEndingCampaigns = Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(5);
                    setDayEnd(10);
                }},
                new CampaignData() {{
                    setDayStart(4);
                    setDayEnd(6);
                    setReachImps(100);
                }});
        endingCampaigns.forEach(c -> campaignStorage.addMyCampaign(c));
        nonEndingCampaigns.forEach(c -> campaignStorage.addMyCampaign(c));

        List<CampaignData> result = campaignStorage.getMyEndingCampaigns(5);

        assertThat(result).containsAll(endingCampaigns);
        assertThat(result).doesNotContainAnyElementsOf(nonEndingCampaigns);
    }

    @Test
    public void testGetTotalNumberOfRemainingImpression() {
        Lists.newArrayList(
                new CampaignData() {{
                    setDayStart(23);
                    setDayEnd(35);
                    setReachImps(1000);
                }},
                new CampaignData() {{
                    setDayStart(1);
                    setDayEnd(5);
                    setReachImps(100);
                }},
                new CampaignData() {{
                    setDayStart(5);
                    setDayEnd(10);
                    setReachImps(100);
                }},
                new CampaignData() {{
                    setDayStart(4);
                    setDayEnd(6);
                    setReachImps(100);
                    setStats(new CampaignStats(50, 0, 0));
                }}).forEach(c -> campaignStorage.addMyCampaign(c));

        int result = campaignStorage.getTotalNumberOfRemainingImpression(5);
        assertThat(result).isEqualTo(100 + 100 + 50);
    }

    @Test
    public void testIsMarketSegmentPercentageLow() {
        assertThat(campaignStorage.isMarketSegmentPercentageLow(25, 0.5)).isFalse();
        campaignStorage.addMyCampaign(new CampaignData() {{
            setDayStart(23);
            setDayEnd(35);
            setReachImps(1000);
            setTargetSegment(Sets.newHashSet(MarketSegment.FEMALE));
        }});

        when(marketSegmentProbability.getMarketSegmentsRatio(any())).thenReturn(0.75);
        assertThat(campaignStorage.isMarketSegmentPercentageLow(5, 0.5)).isFalse();

        when(marketSegmentProbability.getMarketSegmentsRatio(any())).thenReturn(0.25);
        assertThat(campaignStorage.isMarketSegmentPercentageLow(25, 0.5)).isTrue();
    }

}
