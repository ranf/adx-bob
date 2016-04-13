package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;

import java.util.Random;
import java.util.Set;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

public class BidBundleData {
	private double avgPerImp;
	private double daysLeftFactor;
	private double campaignImpRatio;
	private double randomFactor;
	private double gameDayFactor;
	private double marketSegmentPopularity;
	private double adInfofactor;
	
	
	public BidBundleData(AdxBidBundle bidBundle, CampaignData campaign, GameData gameData, AdxQuery query)
	{
		setAvgPerImp(campaign);
		setDaysLeftFactor(campaign, gameData.getDay());
		setCampaignImpRatio(campaign, gameData);
		setRandomFactor();
		setGameDayFactor(gameData.getDay());
		setMarketSegmentPopularity(campaign, 1.8); //1.8 - random value, need to test
		setAdInfoFactor(campaign, query);	
		
	}

	private MarketSegmentProbability marketSegmentProbability;

	@Inject
	public BidBundleData(MarketSegmentProbability marketSegmentProbability) {
		this.marketSegmentProbability = marketSegmentProbability;
	}

	public void setAvgPerImp(CampaignData campaign) {
		this.avgPerImp = campaign.getBudget() / campaign.getReachImps();
	}

	public double getAvgPerImp() {
		return this.avgPerImp;
	}

	public void setDaysLeftFactor(CampaignData campaign, long currentDay) {
		long totalCampaignDays = campaign.getCampaignLength();
		long daysLeft = campaign.getDayEnd() - currentDay;

		if (daysLeft == 1) {
			this.daysLeftFactor = 2.7D;
		}
		if (daysLeft == 2) {
			this.daysLeftFactor = 1.8D;
		} else {
			this.daysLeftFactor = (1.2D * (1 - (totalCampaignDays - daysLeft) / 10));
		}
	}

	public double getDaysLeftFactor() {
		return this.daysLeftFactor;
	}

	public void setCampaignImpRatio(CampaignData currCamp, GameData gameData) {
		this.campaignImpRatio = ((currCamp.impsTogo() / currCamp.getReachImps())
				/ (gameData.getDay()/* TODO wrong use of day */ / currCamp.getCampaignLength()));
	}

	public double getCampaignImpRatio() {
		return this.campaignImpRatio;
	}

	public void setAdInfoFactor(CampaignData currCamp, AdxQuery currAdXQuery) {
		if (currAdXQuery.getDevice() == Device.pc) {
			if (currAdXQuery.getAdType() == AdType.text) {
				this.adInfofactor = 1.0D;
			} else if (currAdXQuery.getAdType() == AdType.video) {
				this.adInfofactor = currCamp.getVideoCoef();
			}
		} else if (currAdXQuery.getDevice() == Device.mobile) {
			if (currAdXQuery.getAdType() == AdType.text) {
				this.adInfofactor = currCamp.getMobileCoef();
			} else if (currAdXQuery.getAdType() == AdType.video) {
				this.adInfofactor = (currCamp.getMobileCoef() * currCamp.getVideoCoef());
			}
		}
	}

	public double getAdInfoFactor() {
		return this.adInfofactor;
	}

	public void setMarketSegmentPopularity(CampaignData currCamp, double c) {
		Set<MarketSegment> targetSeg = currCamp.getTargetSegment();
		double segRatio = this.marketSegmentProbability.getMarketSegmentsRatio(targetSeg).doubleValue();
		if (segRatio > c) {
			this.marketSegmentPopularity = segRatio;
		} else {
			this.marketSegmentPopularity = (segRatio * c);
		}
	}

	public double getMarketSegmentPopularity() {
		return this.marketSegmentPopularity;
	}

	public void setRandomFactor() {
		double days_left = getDaysLeftFactor();
		double camp_ratio = getCampaignImpRatio();
		if ((days_left <= 3.0D) && (camp_ratio > 0.55D)) {
			this.randomFactor = randDouble(0.95D, 1.0D);
		} else if (randDouble(0.0D, 1.0D) < 0.2D) {
			this.randomFactor = Math.max(this.marketSegmentPopularity / 2.0D, randDouble(0.0D, 1.0D));
		}
	}

	public double getRandomFactor() {
		return this.randomFactor;
	}

	public void setGameDayFactor(int daysPassed) { //how many days passed since the beginning of the game
		this.gameDayFactor = (2.3D / daysPassed);
	}

	public double getGameDayFactor() {
		return this.gameDayFactor;
	}

	public static double randDouble(double min, double max) {
		double random = new Random().nextDouble();
		double result = min + random * (max - min);

		return result;
	}
}
