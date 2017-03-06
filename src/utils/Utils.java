package utils;

import tcp.Constant;

import java.util.Random;

public class Utils {
    public static boolean debug = true;

    public static void log(String TAG, String message) {
        if (debug) {
//            System.out.println(TAG + " : " + message);
            System.out.println(message);
        }
    }

    private static Random r = new Random();

    public static boolean testLost() {
        int x = r.nextInt(101);
        if (x <= Constant.LOST_PROBABILITY) {
            return true;
        } else {
            return false;
        }
    }
}
