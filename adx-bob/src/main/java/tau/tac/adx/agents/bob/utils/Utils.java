package tau.tac.adx.agents.bob.utils;

import java.util.List;
import java.util.Random;

public class Utils {

    /*find the log (base b) of a*/
    public static double logb(double a, double b) {
        return Math.log(a) / Math.log(b);
    }

    /*return a random double between min to max values*/
    public static double randDouble(double min, double max) {
        double random = new Random().nextDouble();
        double result = min + random * (max - min);

        return result;
    }

    /*Calculate list value's average*/
    public static double listAvg(List<Double> bidsList) {
        double avg = 0;
        for (int i = 0; i < bidsList.size(); i++) {
            avg = avg + bidsList.get(i);
        }
        return avg / bidsList.size();
    }

    public static double effectiveReachRatio(double effectiveUniqueImpressions, long campaignReach) {
        final double a = 4.08577;
        final double b = 3.08577;
        return (2.0 / a) * (Math.atan(a * effectiveUniqueImpressions / campaignReach - b) - Math.atan(-b));
    }

    /*return true iff num is in list*/
    public static boolean notInList(List<Integer> list, int num){
        for( int i : list){
            if(i == num)
                return false;
        }
        return true;
    }
}
