package tau.tac.adx.agents.bob.utils;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.junit.Test;
import tau.tac.adx.agents.bob.BaseTestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class UtilsTest extends BaseTestCase {

    @Test
    public void testLogb() {
        double result = Utils.logb(0.25, 0.5);
        assertThat(result).isCloseTo(2, within(0.00001));
    }

    @Test
    public void testRandDoubleInRange() {
        List<Pair<Double, Double>> minMaxList = Lists.newArrayList(new Pair<>(123.31, 123.32), new Pair<>(-12.0, 0.0)
                , new Pair<>(10.2, 999.999));
        for (Pair<Double, Double> minMax : minMaxList) {
            double min = minMax.getKey();
            double max = minMax.getValue();
            double result = Utils.randDouble(min, max);
            assertThat(result).isGreaterThan(min).isLessThan(max);
        }
    }

    @Test
    public void testListAvg() {
        List<Double> list = Lists.newArrayList(1.0, 1.1, 1.2, 1.3);
        double result = Utils.listAvg(list);
        assertThat(result).isEqualTo(1.15);
    }

    @Test
    public void testEffectiveReachRatioNearOne() {
        double result = Utils.effectiveReachRatio(123.0, 123);
        assertThat(result).isCloseTo(1.0, within(0.01));
    }

    @Test
    public void testEffectiveReachRatio() {
        double result = Utils.effectiveReachRatio(1538.3, 2400);
        assertThat(result).isCloseTo(0.4, within(0.01));
    }
}
