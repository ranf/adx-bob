package tau.tac.adx.agents.bob.bid;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.InjectMocks;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.devices.Device;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class BidBundleFactorCalculatorTest extends BaseTestCase {

    @InjectMocks
    private BidBundleFactorCalculator bidBundleFactorCalculator;

    @Test
    public void testBidBundleFactorCalculator() {
        assertThat(bidBundleFactorCalculator).isNotNull();
    }

    @Test
    public void testCalcDayLeftFactor() {
        long campaignLength = 5;
        double oneLeft = bidBundleFactorCalculator.calcDayLeftFactor(campaignLength, 15, 14);
        double twoLeft = bidBundleFactorCalculator.calcDayLeftFactor(campaignLength, 15, 13);
        double fourLeft = bidBundleFactorCalculator.calcDayLeftFactor(campaignLength, 15, 11);
        double fiveLeft = bidBundleFactorCalculator.calcDayLeftFactor(campaignLength, 15, 10);

        assertThat(oneLeft).isLessThan(1.2).isGreaterThan(twoLeft);
        assertThat(twoLeft).isGreaterThan(fourLeft);
        assertThat(fourLeft).isGreaterThan(fiveLeft);
        assertThat(fiveLeft).isGreaterThan(0.9);
    }

    @Test
    public void testCalcGameDaysFactor() {
        List<Double> factors = IntStream.range(0, 60).mapToDouble(d -> bidBundleFactorCalculator.calcGameDaysFactor
                (d)).boxed().collect(Collectors.toList());
        //Decreases as the game progress
        assertThat(Lists.reverse(factors)).isSorted();
        assertThat(factors.get(0)).isGreaterThan(1).isLessThan(1.2);
        assertThat(Lists.reverse(factors).get(0)).isLessThan(1).isGreaterThan(0.8);
    }

    @Test
    public void testCalcMarketSegmentPopularityFactor() {
        double bigSegment = bidBundleFactorCalculator.calcMarketSegmentPopularityFactor(0.6, 0.3);
        double smallSegment = bidBundleFactorCalculator.calcMarketSegmentPopularityFactor(0.2, 0.3);

        assertThat(bigSegment).isEqualTo(1);
        assertThat(smallSegment).isGreaterThan(1).isLessThan(1.5);
    }

    @Test
    public void testCalcCampaignImpRatio() {
        double safeCampaign = bidBundleFactorCalculator.calcCampaignImpRatio(123, 1215, 15, 12, 5);
        double riskyCampaign = bidBundleFactorCalculator.calcCampaignImpRatio(500, 1215, 15, 14, 5);

        assertThat(riskyCampaign).isGreaterThan(0.45);
        assertThat(safeCampaign).isLessThan(0.45);
    }

    @Test
    public void testCalcRandomFactor() {
        double safeRandom = bidBundleFactorCalculator.calcRandomFactor(0.2);
        double riskyRandom = bidBundleFactorCalculator.calcRandomFactor(0.8);

        assertThat(safeRandom).isBetween(0.95, 1.0);
        assertThat(riskyRandom).isBetween(1.0, 1.1);
    }

    @Test
    public void testCalcAdInfoFactor() {
        double mobileCoef = 1.123;
        double videoCoef = 1.321;
        double regular = bidBundleFactorCalculator.calcAdInfoFactor(Device.pc, AdType.text, mobileCoef, videoCoef);
        double video = bidBundleFactorCalculator.calcAdInfoFactor(Device.pc, AdType.video, mobileCoef, videoCoef);
        double mobile = bidBundleFactorCalculator.calcAdInfoFactor(Device.mobile, AdType.text, mobileCoef, videoCoef);
        double mobileAndVideo = bidBundleFactorCalculator.calcAdInfoFactor(Device.mobile, AdType.video, mobileCoef,
                videoCoef);

        assertThat(regular).isEqualTo(1);
        assertThat(video).isEqualTo(videoCoef);
        assertThat(mobile).isEqualTo(mobileCoef);
        assertThat(mobileAndVideo).isEqualTo(videoCoef * mobileCoef);
    }

}
