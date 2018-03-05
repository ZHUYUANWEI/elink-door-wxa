package com.ssm.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;


/**      
 * request 对象的相关操作
 * @author zhangtengda        
 * @version 1.0      
 * @created 2015年5月2日 下午8:25:43     
 */       
public class GetRequestUtils {

    /***
     * 获取 request 中 json 字符串的内容
     * 
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(HttpServletRequest request)
            throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if (submitMehtod.equals("GET")) {
        	System.out.println(new String(request.getQueryString().getBytes("UTF-8"),"UTF-8"));
        	System.out.println(new String(request.getQueryString().getBytes("ISO8859-1"),"UTF-8"));
        	return new String(request.getQueryString().getBytes("ISO8859-1"),"UTF-8");
        // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /**      
     * 描述:获取 post 请求的 byte[] 数组
     * <pre>
     * 举例：
     * </pre>
     * @param request
     * @return
     * @throws IOException      
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if(contentLength<0){
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**      
     * 描述:获取 post 请求内容
     * <pre>
     * 举例：
     * </pre>
     * @param request
     * @return
     * @throws IOException      
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        //System.out.println(new String(buffer, charEncoding));
        System.out.println("POST Request");
        //System.out.println("POST"+new String(buffer, charEncoding));
        return new String(buffer, charEncoding);
    }
    public static String getOriginalData(HttpServletRequest request) throws IOException{
    	String submitMehtod = request.getMethod();
        if (submitMehtod.equals("GET")) {
        	//System.out.println(new String(request.getQueryString()));
        	//System.out.println(new String(request.getQueryString().getBytes("UTF-8"),"UTF-8"));
        	//System.out.println(new String(request.getQueryString().getBytes("ISO8859-1"),"UTF-8"));
        	return new String(request.getQueryString().getBytes("ISO8859-1"),"UTF-8");
        // POST
        } else {
        	String temp = null;
        	//System.out.println(new String(request.getQueryString()));
            return temp;
        }
    }
}