package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.devices.Device;


public class BidBundleFactorCalculator {

    @Inject
    public BidBundleFactorCalculator() {
    }

    public double calcDayLeftFactor(long campaignLength, long campaignEndDay, int currentDay) {
        long daysLeft = campaignEndDay - currentDay;
        double daysLeftFactor;
        if (daysLeft == 1) {
            daysLeftFactor = 1.175;
        } else if (daysLeft == 2) {
            daysLeftFactor = 1.1;
        } else {
            daysLeftFactor = 0.9 + (((double) (campaignLength - daysLeft + 1)) / (campaignLength * 10));
        }
        return daysLeftFactor;
    }

    public double calcGameDaysFactor(double gameDay) {
        if (gameDay < 20)
            return 1.1;
        else if (gameDay < 40)
            return 1.05;
        else if (gameDay < 50)
            return 1.0;
        else
            return 0.95;
    }

    public double calcMarketSegmentPopularityFactor(double segRatio, double c) {
        return segRatio > c ? 1 : 1.3;
    }

    public double calcCampaignImpRatio(double impsTogo, double reachImps, long dayEnd, long currentDay,
                                       long campaignLength) {
        //checks if the campaign is progressing nicely
        double impressionsLeftRatio = impsTogo / reachImps;
        double daysLeftRatio = (double) (dayEnd - currentDay) / (double) campaignLength;
        return impressionsLeftRatio / daysLeftRatio;
    }

    public double calcRandomFactor(double campRatio) {
        return (campRatio < 0.45) ? Utils.randDouble(0.95, 1.0) : Utils.randDouble(1.0, 1.1);
    }

    public double calcAdInfoFactor(Device device, AdType adType, double mobileCoef, double videoCoef) {
        double adInfoFactor = 1;
        if (adType == AdType.video) {
            adInfoFactor *= videoCoef;
        }
        if (device == Device.mobile) {
            adInfoFactor *= mobileCoef;
        }
        return adInfoFactor;
    }
}
