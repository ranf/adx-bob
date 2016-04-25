package tau.tac.adx.agents.bob.bid;

public class BidBundleData {

    private double avgPerImp;
    private double daysLeftFactor;
    private double campaignImpRatio;
    private double randomFactor;
    private double gameDayFactor;
    private double marketSegmentPopularityFactor;
    private double adInfofactor;
    private long imprCompetition;

    public BidBundleData() {
    }

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

    // TODO- need to check the initialization in (segRatio > c)
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
