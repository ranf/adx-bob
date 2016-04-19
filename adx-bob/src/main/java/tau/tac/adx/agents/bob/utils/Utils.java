package tau.tac.adx.agents.bob.utils;

import java.util.Random;
import java.util.List;

public class Utils {

	public static double logb(double a, double b) {
		return Math.log(a) / Math.log(b);
	}


public static double randDouble(double min, double max) {
	double random = new Random().nextDouble();
	double result = min + random * (max - min);

	return result;
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
