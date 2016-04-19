package tau.tac.adx.agents.bob.utils;

import java.util.List;

public class Utils {

	public static double logb(double a, double b) {
		return Math.log(a) / Math.log(b);
	}
	
	/*
	 * Calculate list value's average
	 */
	public static double listAvg(List<Double> bidsList) {
		double avg = 0;
		for (int i = 0; i < bidsList.size(); i++) {
			avg = avg + bidsList.get(i);
		}
		return avg / bidsList.size();
	}
}
