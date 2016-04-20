package tau.tac.adx.agents.bob.bid;

import java.util.Random;

import com.google.inject.Inject;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.agents.bob.utils.Utils;


public class BidBundleFactorCalculator {

	@Inject
	public BidBundleFactorCalculator() {
	}

	public double calcDayLeftFactor(long campaignLength, long campaignEndDay, int currentDay) {
		long daysLeft = campaignEndDay - currentDay;
		double daysLeftFactor;
		if (daysLeft == 1) {
			daysLeftFactor = 3.2D;
		}
		if (daysLeft == 2) {
			daysLeftFactor = 2.7D;
		} else {
			daysLeftFactor = (1.2D * (1 - ((double)(campaignLength - daysLeft) / 10D)));
		}
		return daysLeftFactor;
	}
	
	public double calcGameDaysFactor (double gameDay){
		return 1;
		
	}

	// TODO- need to check the initialization in (segRatio > c)
	public double calcMarketSegmentPopularityFactor(double segRatio, double c) {
		double marketSegmentPopularityFactor;
		if (segRatio > c) {
			// this.marketSegmentPopularity = segRatio;
			marketSegmentPopularityFactor = 1;
		} else {
			marketSegmentPopularityFactor = 1.3;
		}
		return marketSegmentPopularityFactor;
	}

	public double calcCampaignImpRatio(double impsTogo, double reachImps, long dayEnd, long currentDay,
			long camapaignLength) {
		double impressionsLeftRatio = impsTogo / reachImps;
		double daysLeftRatio = (double)(dayEnd - currentDay) / (double)camapaignLength;
		return impressionsLeftRatio / daysLeftRatio;
	}

	public double calcRandomFactor(double daysLeftFactor, double campRatio) {
		double randomFactor;
		if ((daysLeftFactor < 1.8D) && (campRatio < 0.45D)) {
			randomFactor = Utils.randDouble(0.95D, 1.0D);
		} else {
			randomFactor = Utils.randDouble(1.0D, 1.1D);
		}
		return randomFactor;
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


}
