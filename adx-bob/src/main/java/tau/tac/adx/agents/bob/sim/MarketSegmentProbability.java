package tau.tac.adx.agents.bob.sim;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import tau.tac.adx.agents.bob.utils.PropertiesLoader;
import tau.tac.adx.report.adn.MarketSegment;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MarketSegmentProbability {

	private final String CONFIG_PATH = "sim.conf";
	private final String MARKET_SEGMENT_PREFIX = "population";

	private long totalPopulation;
	//TODO determine if market segments work using conjunction or disjunction 
	private Map<Set<MarketSegment>, Double> marketSegmentRatios;

	private PropertiesLoader propLoader;

	@Inject
	public MarketSegmentProbability(PropertiesLoader propLoader) {
		this.propLoader = propLoader;
	}

	public void load() throws IOException {
		Properties prop = propLoader.getPropertiesFromResource(CONFIG_PATH);
		String totalString = prop.getProperty(MARKET_SEGMENT_PREFIX + ".total");
		totalPopulation = Long.parseLong(totalString);

		List<Set<MarketSegment>> allSegments = MarketSegment.marketSegments();
		marketSegmentRatios = new HashMap<Set<MarketSegment>, Double>();
		for (Set<MarketSegment> segments : allSegments) {
			String configKey = MARKET_SEGMENT_PREFIX + ".";
			configKey += segementSetToString(segments);
			String populationString = prop.getProperty(configKey);
			if (populationString != null && !populationString.isEmpty()) {
				Double prob = ((double) Long.parseLong(populationString))
						/ totalPopulation;
				marketSegmentRatios.put(segments, prob);
			}
		}
	}

	public void reset() {
		totalPopulation = 0;
		marketSegmentRatios = null;
	}

	public Double getMarketSegmentsRatio(Set<MarketSegment> segments) {
		return marketSegmentRatios.get(segments);
	}

	private String segementSetToString(Set<MarketSegment> segments) {
		String result = "";
		if (segments.contains(MarketSegment.MALE))
			result += "M";
		else if (segments.contains(MarketSegment.FEMALE))
			result += "F";
		if (segments.contains(MarketSegment.YOUNG))
			result += "Y";
		else if (segments.contains(MarketSegment.OLD))
			result += "O";
		if (segments.contains(MarketSegment.LOW_INCOME))
			result += "L";
		else if (segments.contains(MarketSegment.HIGH_INCOME))
			result += "H";
		return result;
	}
}
