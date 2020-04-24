package cn.hayring.sharingmachine.utils;

/**
 * Created by yangyibo on 17/2/7.
 */

import java.security.MessageDigest;
import java.util.Random;

/**
 * MD5加密工具
 */
public class MD5Util {


    public static String encode(String password, String salt) {
        password = password + salt;
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


//    public static void main(String[] args) {
//        System.out.println(MD5Util.encode("KID1412"));
//
//
//    }


    private static char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static char[] digit = new char[10];

    static {
        System.arraycopy(hex, 0, digit, 0, 10);
    }

    private static final int SALT_LENGTH = 8;

    private static final int NUMBER_LENGTH = 8;

    /**
     * 自定义简单生成盐，是一个随机生成的长度为16的字符串，每一个字符是随机的十六进制字符
     */
    public static String getSalt() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SALT_LENGTH);
        for (int i = 0; i < SALT_LENGTH; i++) {
            sb.append(hex[random.nextInt(36)]);
        }
        return sb.toString();
    }

    public static String getNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SALT_LENGTH);
        for (int i = 0; i < SALT_LENGTH; i++) {
            sb.append(hex[random.nextInt(10)]);
        }
        return sb.toString();
    }
}