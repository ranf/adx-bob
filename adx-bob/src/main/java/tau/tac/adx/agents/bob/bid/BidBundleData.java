package tau.tac.adx.agents.bob.bid;

/*This class gathers all the information we need for the bid bundle */
public class BidBundleData {

    private double avgPerImp; /*The average budget we have per impression, the calculation is
    (campaign budget / campaign reach impressions)*/
    private double daysLeftFactor; /*Factor of how many days left for this campaign - the less days left the more we
    want to get impressions to finish the campaign*/
    private double campaignImpRatio;/*This parameter will tell us about our state in the game, we calculate the
    ratio between the campaign impressions state and days left, if the ratio is low meaning our progress is good,
    otherwise our progress is not that good and we need to be more aggressive and get more impressions*/
    private double randomFactor; /*We added random factor to our bid*/
    private double gameDayFactor;
    private double marketSegmentPopularityFactor; /*Factor of how popular the campaign market segment is, if the market
    segment is popular, meaning there are allot of potential impressions from this segment, then our bid will not
    change, but if the segment is rare, meaning there is not many impressions from this campaign market segment our
    bid will be higher for each impression */
    private double adInfofactor; /*Factor of the ad type and device coefs*/
    private long imprCompetition; /*Factor of how many campaign are running today and have the same market segment as
     our campaign - the competition we have for each impression*/

    public BidBundleData() {
    }

    /*** GETTERS AND SETTERS FOR EACH PARAMETER ***/

    public void setAvgPerImp(double avgPerImp) {
        this.avgPerImp = avgPerImp;
    }

    public double getAvgPerImp() {
        return this.avgPerImp;
    }

    public void setDaysLeftFactor(double factor) {
        this.daysLeftFactor = factor;
    }

    public double getDaysLeftFactor() {
        return this.daysLeftFactor;
    }

    public void setCampaignImpRatio(double campaignImpRatio) {
        this.campaignImpRatio = campaignImpRatio;
    }

    public double getCampaignImpRatio() {
        return this.campaignImpRatio;
    }

    public void setAdInfoFactor(double adInfofactor) {
        this.adInfofactor = adInfofactor;
    }

    public double getAdInfoFactor() {
        return this.adInfofactor;
    }

    public void setMarketSegmentPopularity(double marketSegmentPopularityFactor) {
        this.marketSegmentPopularityFactor = marketSegmentPopularityFactor;
    }

    public double getMarketSegmentPopularity() {
        return this.marketSegmentPopularityFactor;
    }

    public void setRandomFactor(double randomFactor) {
        this.randomFactor = randomFactor;
    }

    public double getRandomFactor() {
        return this.randomFactor;
    }

    public void setGameDayFactor(double gameDayFactor){
        this.gameDayFactor = gameDayFactor;
    }

    public double getGameDayFactor() {
        return this.gameDayFactor;
    }

    @Override
    public String toString() {
        return "BidBundleData [avgPerImp=" + avgPerImp + ", daysLeftFactor=" + daysLeftFactor + ", campaignImpRatio="
                + campaignImpRatio + ", randomFactor=" + randomFactor + ", gameDayFactor=" + gameDayFactor
                + ", marketSegmentPopularityFactor=" + marketSegmentPopularityFactor + ", adInfofactor=" + adInfofactor
                + ", ImprCompetition=" + imprCompetition + "]";
    }

    public long getImprCompetition() {
        return imprCompetition;
    }

    public void setImprCompetition(long imprCompetition) {
        this.imprCompetition = imprCompetition;
    }
}
