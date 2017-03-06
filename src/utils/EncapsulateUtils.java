package utils;

import tcp.Constant;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Created by Dandoh on 11/29/15.
 */
public class EncapsulateUtils {

    public static byte[][] splitData(byte[] data) {
        int length = data.length;
        int numPacket = (length - 1)/ Constant.SIZE_DATA + 1;
        byte[][] res = new byte[numPacket][];

        int from = 0;
        for (int i = 0; i < numPacket; i++) {
            int to = 0;
            if (from + Constant.SIZE_DATA < length) {
                to = from + Constant.SIZE_DATA;
            } else {
                to = length;
            }

            res[i] = Arrays.copyOfRange(data, from, to);
            from = to;
        }
        Utils.log("Split", res.length + " ");

        return res;
    }


    public static DatagramPacket encapsulate(byte[] data, int seqnum) {
        // add 5 byte header to front; TODO

        byte[] addedSeqData = concat(intToByteArray(seqnum), data);
        byte[] checksum = CheckSumUtils.getCheckSumBytes(addedSeqData);
        byte[] actualData = concat(checksum, addedSeqData);

        return new DatagramPacket(actualData, actualData.length);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }


    public static byte[] getActualData(DatagramPacket data) {
        return Arrays.copyOfRange(data.getData(), 5, data.getLength());
    }

    public static int getSequenceNumber(DatagramPacket packet) {
        byte[] bytes = Arrays.copyOfRange(packet.getData(), 1, 5);
        int sequenceNumber = new BigInteger(bytes).intValue();

        return sequenceNumber;
    }



}
