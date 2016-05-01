package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.utils.Utils;
import tau.tac.adx.devices.Device;

/*This class has all the routines we need to calculate the BidBundleData parameters, `and with this parameters we
build our bids for impressions*/
public class BidBundleFactorCalculator {

    @Inject
    public BidBundleFactorCalculator() {
    }

    /*Factor of how many days left for this campaign - the less days left the more we
    want to get impressions to finish the campaign so we bid  higher,
    the input parameters are the campaign length, the day the campaign ends and the current day in the game.
    we first calculate how many days left until the end of the campaign, and based on that we return our factor*/
    public double calcDayLeftFactor(long campaignLength, long campaignEndDay, int currentDay) {
        long daysLeft = campaignEndDay - currentDay;
        double daysLeftFactor; /*the return value*/
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

    /*Factor of how popular the campaign market segment is , if the market
    segment is bigger then c (the input value), meaning there are allot of potential impressions from this segment,
    then our bid will not  change, but if the segment is rare,
    meaning there are not many impressions from this campaign market segment our
    bid will be higher for each impression */
    public double calcMarketSegmentPopularityFactor(double segRatio, double c) {
        return segRatio > c ? 1 : 1.3;
    }

    /*This parameter will tell us about our state in the game, we calculate the
    ratio between the campaign impressions state and days left state, if the ratio is low meaning our progress is good,
    otherwise our progress is not that good and we need to be more aggressive and get more impressions*/
    public double calcCampaignImpRatio(double impsTogo, double reachImps, long dayEnd, long currentDay,
                                       long campaignLength) {
        double impressionsLeftRatio = impsTogo / reachImps; /*the ratio between how many more impressions we need to
        get to the target number of impression */
        double daysLeftRatio = (double) (dayEnd - currentDay) / (double) campaignLength; /*the ratio between how many
         days left for the campaign and the campaign length*/
        return impressionsLeftRatio / daysLeftRatio;
    }

    /*The bid random factor depends on the campaignImpRatio parameter.
     * the campaignimpRation parameter tells us about our progress in the game, if out progress is good then the
     * campImpRatio is low, and the random factor will be lower, otherwise our progress is not that good and we give
     * higher random factor to enlarge our bid and get more impressions*/
    public double calcRandomFactor(double campRatio) {
        return (campRatio < 0.45) ? Utils.randDouble(0.95, 1.0) : Utils.randDouble(1.0, 1.1);
    }

    /*Factor of the ad type and device coefs, we enlarge the factor if the ad type is video and mobile*/
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
