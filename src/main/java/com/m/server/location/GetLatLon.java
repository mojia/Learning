package com.m.server.location;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

public class GetLatLon {

    /**
     * 
     * @param int
     * @return
     */
    public static String int2hex(int value) {
        String str = Integer.toHexString(value);
        String strfmt = "00000000";
        return strfmt.substring(0, strfmt.length() - str.length()) + str;
    }

    /**
     * 
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 
     * @param int cid, int lac, int mnc, int mcc
     * @return
     */
    public static byte[] sendDataFormat(int cid, int lac, int mnc, int mcc) {
        String string1 = "000E00000000000000000000000000001B0000000000000000000000030000";
        String string2 = "FFFFFFFF00000000";

        String retStr = string1 + int2hex(cid) + int2hex(lac) + int2hex(mnc)
                + int2hex(mcc) + string2;

        return hexStringToByte(retStr.toUpperCase());
    }

    /**
     * 
     * @param byte[]
     * @return
     */
    public static double[] getDataFormat(byte[] byteArr) {

        double[] retArr = new double[2];

        String resHexStr = bytesToHexString(byteArr);
        // System.out.println(resHexStr);

        String latHexStr = resHexStr.substring(14, 22);
        String lonHexStr = resHexStr.substring(22, 30);

        retArr[0] = Integer.parseInt(latHexStr, 16) / 1000000.0;
        retArr[1] = Integer.parseInt(lonHexStr, 16) / 1000000.0;

        return retArr;
    }

    @Test
    public void testcase() {
        double[] re = getLatLon(122704505, 54144, 1, 460);
        System.out.println(re[0] + "," + re[1]);
    }

    /**
     * 
     * @param int cid, int lac, int mnc, int mcc
     * @return
     */
    public static double[] getLatLon(int cid, int lac, int mnc, int mcc) {

        double[] retArr = new double[2];
        try {

            String urlAddr = "http://www.google.com/glm/mmap";

            byte[] postByteArr = sendDataFormat(cid, lac, mnc, mcc);

            URL url = new URL(urlAddr);
            URLConnection rulConnection = url.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;

            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(30000);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);

            httpUrlConnection.setRequestProperty("Content-type",
                    "application/binary");
            httpUrlConnection.setRequestProperty("Content-Length",
                    String.valueOf(postByteArr.length));
            httpUrlConnection.setRequestMethod("POST");

            DataOutputStream dos = new DataOutputStream(
                    httpUrlConnection.getOutputStream());
            dos.write(postByteArr);
            dos.flush();
            dos.close();

            httpUrlConnection.connect();
            InputStream inStrm = null;
            int BUFFER_SIZE = 4096;
            inStrm = httpUrlConnection.getInputStream();
            // ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // byte[] data = new byte[BUFFER_SIZE];
            // int count = -1;
            // while ((count = inStrm.read(data, 0, BUFFER_SIZE)) != -1) {
            // outStream.write(data, 0, count);
            // }
            //
            // byte[] res = outStream.toByteArray();
            // retArr = getDataFormat(res);
            DataInputStream dataInputStream = new DataInputStream(inStrm);

            // ---interpret the response obtained---
            short a = dataInputStream.readShort();
            System.out.println("a:" + a);
            dataInputStream.readByte();
            int code = dataInputStream.readInt();

            if (code == 0) {
                retArr[0] = (double) dataInputStream.readInt() / 1000000D;
                retArr[1] = (double) dataInputStream.readInt() / 1000000D;
                int aa = dataInputStream.readInt();
                int bb = dataInputStream.readInt();
                // int cc = dataInputStream.readInt();
                // String cc = dataInputStream.readUTF();
                System.out.println("Accuracy = " + aa + " bb = " + bb);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return retArr;
    }

    public static void main(String[] args) {
        double[] locate1 = getLatLon(18896, 4414, 0, 460);
        System.out.println("cellid = 18896 ,lac = 4414, locate : " + locate1[0]
                + "," + locate1[1]);
    }
}
