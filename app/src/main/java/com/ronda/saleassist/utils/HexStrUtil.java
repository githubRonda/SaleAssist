package com.ronda.saleassist.utils;

/**
 * 本工具类主要是 HexString 和 byte[]/byte 以及 String 之间的相互转化
 * <p>
 * byte[]/byte <--> HexString <--> String(即hexString所表示的16进制数所对应的ASCII码的字面量)
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/05/24
 * Version: v1.0
 */

public class HexStrUtil {

    /**
     * byte[]数组转换成十六进制字符串 (本质就是先把每个byte转成16进制字符串，最后在拼接起来即可)
     * eg: bytesToHexString(new byte[]{9,10,15}) --> "090A0F"
     *
     * @param bArray
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        for (int i = 0; i < bArray.length; i++) {
            sb.append(byteToHexString(bArray[i]));
        }
        return sb.toString();
    }

    /**
     * 字节转成16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte b) {
        String tmp = Integer.toHexString(0xFF & b);
        if (tmp.length() < 2) {
            tmp = "0" + tmp;
        }
        return tmp.toUpperCase();
    }


    /**
     * 字符串转换成十六进制字符串（其实就是把对应的字符串中的各个字符转换成ASCII码所对应的16进制字符串形式）
     * eg: str2HexStr("a") --> "61" 其实字符a对应的ASCII码就是 十进制中的 97， 转成16进制就是 61
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4; // 取低八位中的前4位，就是十六进制中的第一位
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f; // 取低八位中的后4位，就是十六进制中的第二位
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 把16进制字符串转换成字节数组, 不足两位的就舍去
     * eg: hexStringToByte("101A") --> [16, 26]
     *
     * @param hexString
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hexString) {
        hexString = hexString.toUpperCase();
        int len = (hexString.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hexString.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    /**
     * 十六进制字符串转换成字符串
     * <p>
     * eg: hexStr2Str("6162")  --> "ab"  说明：61就是a的ASCII码的16进制形式，62就是b的ASCII码的16进制形式
     *
     * @param hexStr
     * @return String
     */
    public static String hexStr2Str(String hexStr) {

        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    /**
     * 十六进制字符串转换字符串 的另一种写法，感觉更好理解
     *
     * @param s
     * @return String
     */
    public static String hexStr2Str2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

}
