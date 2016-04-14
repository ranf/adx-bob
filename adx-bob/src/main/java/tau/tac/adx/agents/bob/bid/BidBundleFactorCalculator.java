package tau.tac.adx.agents.bob.bid;

import java.util.Random;

import com.google.inject.Inject;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;

public class BidBundleFactorCalculator {

	@Inject
	public BidBundleFactorCalculator() {
	}

	public double calcDayLeftFactor(long campaignLength, long campaignEndDay, int currentDay) {
		long daysLeft = campaignEndDay - currentDay;
		double daysLeftFactor;
		if (daysLeft == 1) {
			daysLeftFactor = 2.7D;
		}
		if (daysLeft == 2) {
			daysLeftFactor = 1.8D;
		} else {
			daysLeftFactor = (1.2D * (1 - (campaignLength - daysLeft) / 10));
		}
		return daysLeftFactor;
	}

	// TODO- need to check the initialization in (segRatio > c)
	public double calcMarketSegmentPopularityFactor(double segRatio, double c) {
		double marketSegmentPopularityFactor;
		if (segRatio > c) {
			// this.marketSegmentPopularity = segRatio;
			marketSegmentPopularityFactor = 1;
		} else {
			marketSegmentPopularityFactor = (segRatio * 1.1);
		}
		return marketSegmentPopularityFactor;
	}

	public double calcCampaignImpRatio(double impsTogo, double reachImps, long dayEnd, long currentDay,
			long camapaignLength) {
		return ((impsTogo / reachImps) / ((dayEnd - currentDay) / camapaignLength));
	}

	public double calcRandomFactor(double daysLeftFactor, double campRatio) {
		double randomFactor;
		if ((daysLeftFactor < 1.8D) && (campRatio < 0.45D)) {
			randomFactor = randDouble(0.95D, 1.0D);
		} else {
			randomFactor = randDouble(1.0D, 1.1D);
		}
		return randomFactor;
		// } else if (randDouble(0.0D, 1.0D) < 0.2D) {
		// this.randomFactor = Math.max(this.marketSegmentPopularity / 2.0D,
		// randDouble(0.0D, 1.0D));
		// }
	}

	public double calcAdInfoFactor(Device device, AdType adType, double mobileCoef, double videoCoef) {
		double adInfoFactor = 0;
		if (device == Device.pc) {
			if (adType == AdType.text) {
				adInfoFactor = 1.0D;
			} else if (adType == AdType.video) {
				adInfoFactor = videoCoef;
			}
		} else if (device == Device.mobile) {
			if (adType == AdType.text) {
				adInfoFactor = mobileCoef;
			} else if (adType == AdType.video) {
				adInfoFactor = (mobileCoef * videoCoef);
			}
		}
		return adInfoFactor;
	}

	private static double randDouble(double min, double max) {
		double random = new Random().nextDouble();
		double result = min + random * (max - min);

		return result;
	}
}
