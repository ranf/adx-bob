package tau.tac.adx.agents.bob.learn;

import org.assertj.core.data.Offset;
import org.junit.Test;
import org.mockito.InjectMocks;
import tau.tac.adx.agents.bob.BaseTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class LearnStorageTest extends BaseTestCase {
    @InjectMocks
    private LearnStorage learnStorage;

    @Test
    public void testLearnStorage() {
        assertThat(learnStorage).isNotNull();
    }

    @Test
    public void testAddToCampaignCost() {
        int campaignId = 412;
        Offset<Double> epsilon = within(0.00001);
        learnStorage.addToCampaignCost(campaignId, 1.2);
        assertThat(learnStorage.getCampaignBidBundlesCost(campaignId)).isCloseTo(1.2, epsilon);
        learnStorage.addToCampaignCost(campaignId, 4.1);
        assertThat(learnStorage.getCampaignBidBundlesCost(campaignId)).isCloseTo(5.3, epsilon);
        learnStorage.addToCampaignCost(campaignId, 400.1);
        assertThat(learnStorage.getCampaignBidBundlesCost(campaignId)).isCloseTo(405.4, epsilon);
    }
}
