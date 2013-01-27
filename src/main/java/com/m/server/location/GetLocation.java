package com.m.server.location;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetLocation {

    public static void main(String[] args) throws JSONException {
        String path = "/Users/wangxin09/Documents/locaJSON.txt";
        String str;
        try {
            str = readJSONFromFile(path);
        } catch (IOException e) {
            System.out.println("read file error\n" + e);
            return;
        }
        List<int[]> lacArgs = preparedArgs(str);

        for (int[] item : lacArgs) {
            double[] locate = new double[2];
            locate = getLatLon(item[0], item[1], item[2], item[3]);
            System.out.println(locate[0] + "," + locate[1]);
        }
    }

    private static String readJSONFromFile(String path) throws IOException {
        StringBuffer result = new StringBuffer();

        BufferedReader reader = new BufferedReader(new FileReader(
                new File(path)));

        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    private static List<int[]> preparedArgs(String str) throws JSONException {
        List<int[]> result = new ArrayList<int[]>();

        JSONObject jo = new JSONObject(str);
        JSONArray cellTowers = jo.getJSONArray("cell_towers");
        for (int i = 0; i < cellTowers.length(); i++) {
            JSONObject item = (JSONObject) cellTowers.get(i);
            int cellID = item.getInt("cell_id");
            int lac = item.getInt("location_area_code");
            int mnc = item.getInt("mobile_network_code");
            int mcc = item.getInt("mobile_country_code");

            int[] codes = { cellID, lac, mnc, mcc };
            result.add(codes);
        }

        return result;
    }

    /**
     * 获取lat、lon总入口
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
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = inStrm.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }

            byte[] res = outStream.toByteArray();
            retArr = getDataFormat(res);

        } catch (Exception ex) {
        }
        return retArr;
    }

    /**
     * 把int转为十六进制，并且格式为8位，前补足0。
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
     * 把16进制字符串转换成字节数组
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
     * 把字节数组转换成16进制字符串
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
     * 发送数据格式化，cid+lac+mnc+mcc 转为byte[]
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
     * 接收数据格式化，截取出lat、lon
     * 
     * @param byte[]
     * @return
     * @throws UnsupportedEncodingException
     */
    public static double[] getDataFormat(byte[] byteArr)
            throws UnsupportedEncodingException {
        double[] retArr = new double[2];

        String resHexStr = bytesToHexString(byteArr);

        String latHexStr = resHexStr.substring(14, 22);
        String lonHexStr = resHexStr.substring(22, 30);

        retArr[0] = Integer.parseInt(latHexStr, 16) / 1000000.0;
        retArr[1] = Integer.parseInt(lonHexStr, 16) / 1000000.0;

        return retArr;
    }

}
