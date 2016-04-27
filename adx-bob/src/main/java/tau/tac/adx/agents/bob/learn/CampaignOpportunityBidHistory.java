package tau.tac.adx.agents.bob.learn;

public class CampaignOpportunityBidHistory {
    private long id;
    private long campaignBid;
    private boolean won;
    private double profit;
    private double completedPart;//TODO rename to effectiveReachRatio

    private long campaignImpressions;
    private long impressionsPerDay;
    private double marketSegmentRatio;
    private int dayStart;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCampaignBid() {
        return campaignBid;
    }

    public void setCampaignBid(long campaignBid) {
        this.campaignBid = campaignBid;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getCompletedPart() {
        return completedPart;
    }

    public void setCompletedPart(double completedPart) {
        this.completedPart = completedPart;
    }

    public long getCampaignImpressions() {
        return campaignImpressions;
    }

    public void setCampaignImpressions(long campaignImpressions) {
        this.campaignImpressions = campaignImpressions;
    }

    public long getImpressionsPerDay() {
        return impressionsPerDay;
    }

    public void setImpressionsPerDay(long impressionsPerDay) {
        this.impressionsPerDay = impressionsPerDay;
    }

    public double getMarketSegmentRatio() {
        return marketSegmentRatio;
    }

    public void setMarketSegmentRatio(double marketSegmentRatio) {
        this.marketSegmentRatio = marketSegmentRatio;
    }

    public int getDayStart() {
        return dayStart;
    }

    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }

    @Override
    public String toString() {
        return "CampaignOpportunityBidHistory{" +
                "id=" + id +
                ", campaignBid=" + campaignBid +
                ", won=" + won +
                ", profit=" + profit +
                ", completedPart=" + completedPart +
                ", campaignImpressions=" + campaignImpressions +
                ", impressionsPerDay=" + impressionsPerDay +
                ", marketSegmentRatio=" + marketSegmentRatio +
                ", dayStart=" + dayStart +
                '}';
    }
}
