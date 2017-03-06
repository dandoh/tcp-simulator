package utils;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Created by Dandoh on 11/19/15.
 */
public class CheckSumUtils {

    private static final String TAG = CheckSumUtils.class.getSimpleName();

    public static boolean isCorrect(DatagramPacket datagramPacket) {

        // TODO -- extract checksum and check
        byte[] data = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
        byte checkSum = data[0];

        byte sum = 0;
        for (int i = 1; i < data.length; i++) {
            sum += data[i];
        }

        if (sum == checkSum) {
//            Utils.log(TAG, "is correct");
            return true;
        } else {
            return false;
        }
    }


    public static byte[] getCheckSumBytes(byte[] origin) {

        byte[] result = new byte[1];

        for (int i = 0; i < origin.length; i++) {
            result[0] = (byte) (result[0] + origin[i]);
        }
        return result;
    }






}
