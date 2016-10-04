package com.simon.session.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.servlet.http.HttpServletRequest;

public class RequestAddressUtil {
		
	private static final String x_Real_IP = "X-Real-IP";
	private static final String x_forwarded_for = "x-forwarded-for" ;
	private static final String Proxy_Client_IP = "Proxy-Client-IP" ;
	private static final String WL_Proxy_Client_IP = "WL-Proxy-Client-IP" ;
	private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP" ;
	private static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR" ;
	private static final String unknown = "unknown" ;
	private static final String defaultIp = "127.0.0.1";
    /**
    * 通过HttpServletRequest返回IP地址
    * @param request HttpServletRequest
    * @return ip String
    * @throws Exception
    */
   public static String getIpAddr(final HttpServletRequest request) {
       String ip = request.getHeader(x_Real_IP);
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {  
           String ips = request.getHeader(x_forwarded_for);           
           if(ips !=null && ips.length()>0){
               int index = ips.indexOf(",");
               ip= ips.substring(0,index);
           }else{
               ip = ips;
           }
       }  
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
           ip = request.getHeader(Proxy_Client_IP);
       }
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
           ip = request.getHeader(WL_Proxy_Client_IP);
       }
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
           ip = request.getHeader(HTTP_CLIENT_IP);
       }
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
           ip = request.getHeader(HTTP_X_FORWARDED_FOR);
       }
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
           ip = request.getRemoteAddr();
       }
       if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
    	   ip = defaultIp;
       }
       return ip;
   }

   /**
   * 通过IP地址获取MAC地址
   * @param ip String,127.0.0.1格式
   * @return mac String
   * @throws Exception
   */
   @Deprecated
  public static String getMACAddress(final String ip) throws Exception {
      String line = "";
      String macAddress = "";
      final String MAC_ADDRESS_PREFIX = "MAC Address = ";
      final String LOOPBACK_ADDRESS = "127.0.0.1";
      //如果为127.0.0.1,则获取本地MAC地址。
      if (LOOPBACK_ADDRESS.equals(ip)) {
          final InetAddress inetAddress = InetAddress.getLocalHost();
          //貌似此方法需要JDK1.6。
          final byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
          //下面代码是把mac地址拼装成String
          final StringBuilder sb = new StringBuilder();
          for (int i = 0; i < mac.length; i++) {
              if (i != 0) {
                  sb.append("-");
              }
              //mac[i] & 0xFF 是为了把byte转化为正整数
              final String s = Integer.toHexString(mac[i] & 0xFF);
              sb.append(s.length() == 1 ? 0 + s : s);
          }
          //把字符串所有小写字母改为大写成为正规的mac地址并返回
          macAddress = sb.toString().trim().toUpperCase();
          return macAddress;
      }
      //获取非本地IP的MAC地址
      try {
          final Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
          final InputStreamReader isr = new InputStreamReader(p.getInputStream());
          final BufferedReader br = new BufferedReader(isr);
          while ((line = br.readLine()) != null) {
              if (line != null) {
                  final int index = line.indexOf(MAC_ADDRESS_PREFIX);
                  if (index != -1) {
                      macAddress = line.substring(index + MAC_ADDRESS_PREFIX.length()).trim().toUpperCase();
                  }
              }
          }
          br.close();
      } catch (final IOException e) {
          e.printStackTrace(System.out);
      }
      return macAddress;
  }

}
