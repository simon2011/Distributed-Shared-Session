package com.simon.session.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* @ClassName: DestUtil  
* @Description: Dest加密解密 
* @author simon 
* @date 2016年9月11日 下午2:04:00  
*
 */
public class DestUtil {
	
	private final static String DES = "DES";
	private final  static String DEFAULT_CRYPT_KEY = "__SESSIONID_";
	private final static Logger log = LoggerFactory.getLogger(DestUtil.class.getName());
	
	   /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
    
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
    
    
       
    /** 
     * java字节码转字符串   转成16进制字符串
     * @param b 
     * @return 
     */
    public static String byte2hex(byte[] b) { //一个字节的数
 
        String hs = "";
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            //整数转成十六进制表示
 
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //转成大写
 
    }
 
    /**
     * 字符串转java字节码
     * @param b
     * @return
     */
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
 
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }
    
    /**
     * Description 根据键值进行加密
     * @param data 
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) {
         	
    	try {
			   byte[] bt = encrypt(data.getBytes(), key.getBytes());			    
		       return byte2hex(bt);
		} catch (Exception e) {
			log.error("byte2hex error ");
			e.printStackTrace();
		}
		return null;
    }
    
	public static String encrypt(String data) {
		try {
			return byte2hex(encrypt(data.getBytes(),DEFAULT_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
			log.error("byte2hex error... ");
			e.printStackTrace();
		}
		return null;
	}
	
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key) {
        if (data == null)
            return null;        
		try {
			byte[] bt = decrypt(hex2byte(data.getBytes()), key.getBytes());
			return new String(bt);
		} catch (Exception e) {
			log.error("hex2byre error...");
			e.printStackTrace();
		}
		return null;
    }
    
	public static String decrypt(String data) {
		 if (data == null)
	            return null;    
		try {
			return new String(decrypt(hex2byte(data.getBytes()),DEFAULT_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
			log.error("hex2byre error...");
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		String key = "127.0.0.1";
		String url = "1nowl6ybj69lt";
		String url2 = DestUtil.encrypt(url, key);
		System.err.println(url2);
		String url3 = DestUtil.decrypt(url2, key);
		System.out.println(url3);
	}
    
}
