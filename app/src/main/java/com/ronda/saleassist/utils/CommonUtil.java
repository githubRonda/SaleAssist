package com.ronda.saleassist.utils;


/*
 * 协议的形式是这样的：0XEA,0xEA,0X00,0X05,  0X00,0X04,0X56,   0x12,0x34,0x56,0XEB,0XEB
 * 核心是：两位16进制字符表示真正的一个字节的数据，所以蓝牙传输时的数据中一个字节表示明文的两个字符
 *
 * byte[] --> String 和  byte[] --> hexString 是不同的
 * 1. byte数组转成字符串：其实是把数组中的每一个元素所对应的数值，查找ASC码表，找到对应的字符，数组的长度是多少，字符串的长度也是多少
 * 2. byte数组转成16进制字符串：其实是把数组中的每一个元素（即8位二进制）分成两半，每一半有4位二进制数，再把这4位二进制数转成一个16进制字符串。byte数组的长度是16进制字符串长度的一半
 * 同理：把16进制的字符串转成字节数组，和把普通字符串转成字节数组也是不一样的。前者是两个字符转成一个byte,后者是一个字符就转成一个byte
 */

public class CommonUtil {

    //扫码卖货指令的十六进制字符串的构建
    public static String getDADBHexStr(int count) {
        String strData = "dada" + intToStr(count, 4) + "dbdb";
        return strData;
    }


    public static String getQrcodeStr(String msg) {
//        0XEE,0xEE,0X00,0X01,0X12,0X34,0X12,0X34,0X12,0X34, 0X12,0X34,0X56,0XEF,0XEF
        return msg.substring(8, 26);
    }

    public static int getFlag(String hexStr) {

        return Integer.parseInt(hexStr.substring(4, 8));
    }

    /*
     * 根据信息生成16进制字符串的字节数组，方便于向蓝牙模块输出
     */
    public static byte[] toEAEB(int count, int price, int lastSum) {
        //eaea0003,000456,001578,ebeb  少了两个字节
        String strData = "eaea" + intToStr(count, 4) + intToStr(price, 6) + intToStr(lastSum, 6) + "ebeb";
        return hexStringToBytes(strData);
    }

    public static byte[] toEAEB(String hexData) {
        return hexStringToBytes(hexData);
    }

    public static String getEAEBHexStr(int count, int price, int lastSum) {
        String strData = "eaea" + intToStr(count, 4) + intToStr(price, 6) + intToStr(lastSum, 6) + "ebeb";
        return strData;
    }

    public static int getEAEBPrice(String strData) {
        //String  temp = "eaea5191000018000090ebeb";
        String str = strData.substring(8, 14);
        int price = Integer.parseInt(str);
        //System.out.println(str);
        //System.out.println(weight);
        return price;
    }

    public static int getEAEBLastSum(String strData) {
        //String  temp = "eaea5191000018000090ebeb";
        String str = strData.substring(14, 20);
        int lastSum = Integer.parseInt(str);
        //System.out.println(str);
        //System.out.println(weight);
        return lastSum;
    }


    public static byte[] toECED(int count, int payId) {
        String strData = "ecec" + intToStr(count, 4) + intToStr(payId, 2) + "eded";
        //System.out.println(strData);
        return hexStringToBytes(strData);
    }

    public static byte[] toECED(String hexData) {
        return hexStringToBytes(hexData);
    }

    public static String getECEDHexStr(int count, int payId) {
        String strData = "ecec" + intToStr(count, 4) + intToStr(payId, 2) + "eded";
        return strData;
    }


    public static int getECEDPayId(String strData) {
        //ECEC 0001 01 EDED
        String str = strData.substring(8, 10);
        int lastSum = Integer.parseInt(str);
        return lastSum;
    }


    public static byte[] toEEEF(int count, int lastSum) {
        //eaea0003,00000456,00001578,ebeb
        String strData = "eeee" + intToStr(count, 4) + intToStr(lastSum, 8) + "efef";
        //System.out.println(strData);
        return hexStringToBytes(strData);
    }


    /**
     * 将int类型的数据变成指定长度的字符串
     *
     * @param data
     * @param len
     * @return
     */
    public static String intToStr(int data, int len) {

        String str = data + "";
        if (str.length() > len) {
            throw new RuntimeException("长度过界");
        }
        for (int i = str.length(); i < len; i++) {
            str = "0" + str;
        }

        return str;
    }

    /**
     * 从上行的10字节所表示的字符串数据中获取其中的重量信息，返回int类型
     *
     * @param weightStr
     * @return
     */

    public static int getWeight(String weightStr) {
        //String  temp = "fafa519100001890fbfb";
        String str = weightStr.substring(8, 16);
        int weight = Integer.parseInt(str);
        //System.out.println(str);
        //System.out.println(weight);
        return weight;
    }

    public static int getEAEBCount(String strData) {
        //String  temp = "eaea5191000018000090ebeb";
        String str = strData.substring(4, 8);
        int count = Integer.parseInt(str);
        //System.out.println(weight);
        return count;
    }


    /**
     * byte数组转换成16进制字符串
     * 每个byte分两半各4位，每4位转成一个16进制的字符，若前4位的二进制为0，转成字符串后的前面也会补上0
     * 这说明，转换后的字符串中的字符个数一定是偶数个
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte数组转换成16进制字符串数组
     * 即一个byte数据转成两个16进制的字符，并且这两个字符组合成一个字符串，不足的话会以“0”字符填充
     * byte数组的每一个元素都是如此，最后返回一个String 类型的数组
     *
     * @param src
     * @return
     */
    public static String[] bytesToHexStrings(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String[] str = new String[src.length];

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str[i] = "0";
            }
            str[i] = hv;
        }
        return str;
    }

    /**
     * 把16进制字符串转换成字节数组。
     * 从hex16进制字符串中的0下标开始，每两位16进制字符转成一个字节类型，返回的是一个byte数组。
     * eg: fb 转成字节数组的话，数组长度为1，而不是2.这是因为f和b各占4位，合起来是8位（1byte）
     * <p>
     * 有一个要求：该16进制的源字符串中的字符个数必须是偶数个，而且每一个必须是0~9、a~f、A~F
     * 若字符个数是奇数个的话，会把最后一个16进制字符给舍去
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {

        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];

        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * @函数功能: BCD码转为10进制串(阿拉伯数据)
     * @输入参数: BCD码
     * @输出结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
    }

    /**
     * @函数功能: 10进制串转为BCD码
     * @输入参数: 10进制串
     * @输出结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }


}
