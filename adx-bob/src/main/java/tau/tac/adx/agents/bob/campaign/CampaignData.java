package tau.tac.adx.agents.bob.campaign;

import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

import java.util.Set;

public class CampaignData {
    /* campaign info as reported */
    private CampaignStats stats;
    private double budget;
    /* campaign attributes as set by server */
    private long reachImps;
    private long dayStart;
    private long dayEnd;
    private Set<MarketSegment> targetSegment;
    private double videoCoef;
    private double mobileCoef;
    private String winner;
    private int id;
    private AdxQuery[] campaignQueries;// array of queries relevant for the campaign

    CampaignData() {
        stats = new CampaignStats(0, 0, 0);
        budget = 0.0;
    }

    public CampaignData(InitialCampaignMessage icm) {
        this();
        reachImps = icm.getReachImps();
        dayStart = icm.getDayStart();
        dayEnd = icm.getDayEnd();
        targetSegment = icm.getTargetSegment();
        videoCoef = icm.getVideoCoef();
        mobileCoef = icm.getMobileCoef();
        id = icm.getId();

    }

    public CampaignData(CampaignOpportunityMessage com) {
        this();
        dayStart = com.getDayStart();
        dayEnd = com.getDayEnd();
        id = com.getId();
        reachImps = com.getReachImps();
        targetSegment = com.getTargetSegment();
        mobileCoef = com.getMobileCoef();
        videoCoef = com.getVideoCoef();
    }

    @Override
    public String toString() {
        return "Campaign ID " + id + ": " + "day " + dayStart + " to " + dayEnd
                + " " + targetSegment + ", reach: " + reachImps + " coefs: (v="
                + videoCoef + ", m=" + mobileCoef + ")";
    }

    /**
     * @return remaining campaign impressions
     */
    public int impsTogo() {
        return (int) Math.max(0, reachImps - stats.getTargetedImps());
    }

    public AdxQuery[] getCampaignQueries() {
        return campaignQueries;
    }

    public void setCampaignQueries(AdxQuery[] campaignQueries) {
        this.campaignQueries = campaignQueries;
    }

    public long getReachImps() {
        return reachImps;
    }

    public void setReachImps(long reachImps) {
        this.reachImps = reachImps;
    }

    public long getDayStart() {
        return dayStart;
    }

    public void setDayStart(long dayStart) {
        this.dayStart = dayStart;
    }

    public long getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(long dayEnd) {
        this.dayEnd = dayEnd;
    }

    public long getCampaignLength() {
        return getDayEnd() - getDayStart() + 1;
    }

    public long getReachImpsPerDay() {
        return getReachImps() / getCampaignLength();
    }

    public Set<MarketSegment> getTargetSegment() {
        return targetSegment;
    }

    public void setTargetSegment(Set<MarketSegment> targetSegment) {
        this.targetSegment = targetSegment;
    }

    public double getVideoCoef() {
        return videoCoef;
    }

    public void setVideoCoef(double videoCoef) {
        this.videoCoef = videoCoef;
    }

    public double getMobileCoef() {
        return mobileCoef;
    }

    public void setMobileCoef(double mobileCoef) {
        this.mobileCoef = mobileCoef;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CampaignStats getStats() {
        return stats;
    }

    public void setStats(CampaignStats s) {
        stats.setValues(s);
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double d) {
        budget = d;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
