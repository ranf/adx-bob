package tau.tac.adx.agents.bob.campaign;

import java.util.Set;

import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.InitialCampaignMessage;

public class CampaignData {
	/* campaign attributes as set by server */
	private Long reachImps;
	private long dayStart;
	private long dayEnd;
	private Set<MarketSegment> targetSegment;
	private double videoCoef;
	private double mobileCoef;
	private boolean isOurs;//TODO use instead of duplicate collection
	private String winner;
	private int id;
	private AdxQuery[] campaignQueries;// array of queries relevant for the
										// campaign.

	/* campaign info as reported */
	public CampaignStats stats;
	public double budget;

	public CampaignData(InitialCampaignMessage icm) {
		reachImps = icm.getReachImps();
		dayStart = icm.getDayStart();
		dayEnd = icm.getDayEnd();
		targetSegment = icm.getTargetSegment();
		videoCoef = icm.getVideoCoef();
		mobileCoef = icm.getMobileCoef();
		id = icm.getId();

		stats = new CampaignStats(0, 0, 0);
		budget = 0.0;
	}

	public void setBudget(double d) {
		budget = d;
	}

	public CampaignData(CampaignOpportunityMessage com) {
		dayStart = com.getDayStart();
		dayEnd = com.getDayEnd();
		id = com.getId();
		reachImps = com.getReachImps();
		targetSegment = com.getTargetSegment();
		mobileCoef = com.getMobileCoef();
		videoCoef = com.getVideoCoef();
		stats = new CampaignStats(0, 0, 0);
		budget = 0.0;
	}

	@Override
	public String toString() {
		return "Campaign ID " + id + ": " + "day " + dayStart + " to " + dayEnd
				+ " " + targetSegment + ", reach: " + reachImps + " coefs: (v="
				+ videoCoef + ", m=" + mobileCoef + ")";
	}

	/**
	 * 
	 * @return remaining campaign impressions
	 */
	public int impsTogo() {
		return (int) Math.max(0, reachImps - stats.getTargetedImps());
	}

	public void setStats(CampaignStats s) {
		stats.setValues(s);
	}

	public AdxQuery[] getCampaignQueries() {
		return campaignQueries;
	}

	public void setCampaignQueries(AdxQuery[] campaignQueries) {
		this.campaignQueries = campaignQueries;
	}

	public Long getReachImps() {
		return reachImps;
	}

	public void setReachImps(Long reachImps) {
		this.reachImps = reachImps;
	}
	
	public void setOurs(){
		this.isOurs = true;
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
		return getDayEnd() - getDayStart();
	}

	public long getReachImpsPerDay(){
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

	public double getBudget() {
		return budget;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
}
