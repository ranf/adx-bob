package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;

public class BidBundleStrategy {

	@Inject
	public BidBundleStrategy() {
	}

	public double calcStableBid(BidBundleData bidBundleData) {
		double bidCalc = bidBundleData.getAvgPerImp();
		double marketSegPopRatio = 1.0D / bidBundleData.getMarketSegmentPopularity();
		bidCalc = bidCalc * bidBundleData.getGameDayFactor() * bidBundleData.getDaysLeftFactor() * marketSegPopRatio
				* bidBundleData.getAdInfoFactor() * bidBundleData.getRandomFactor() * ((double)bidBundleData.getImprCompetition()/100);//(Math.log((double)bidBundleData.getImprCompetition()));
		return bidCalc;
	}

	public double calcFirstDayBid(BidBundleData bidBundleData) {
		double stableBid = calcStableBid(bidBundleData);
		double avgRevenuePerImp = bidBundleData.getAvgPerImp();
		return Math.max(stableBid, 80 * avgRevenuePerImp);
	}
}
