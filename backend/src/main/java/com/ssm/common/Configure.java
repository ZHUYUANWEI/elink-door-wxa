package com.ssm.common;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * author wangzhengyu
 * 
 * function 公共配置参数(common config center)
 * 
 * datetime 2017-09-29
 * */
public class Configure {
    //注释先锋
    private static Logger logger = LoggerFactory.getLogger(Configure.class);
    
	public static String SYSTEM_NAME = "";
	
	public static String SYSTEM_TOKEN = "wzy741";
	
	public static String CHARSET_DEFAULT = "UTF-8";
	//临时参数值，正式上线时，不可从此获取

    //获取内存中的Servlet参数
	public static String getConfig(HttpServletRequest request,String name) throws Exception{
        //1.得到servletContext  
        ServletContext sc=request.getServletContext();  
        //2.得到属性和它对应的值
        String http=(String)sc.getAttribute(name);
        logger.info("API="+http);
		return http;
	}
	
	
	
//	public static String GetConfig() throws IOException, FileNotFoundException {
//		// TODO Auto-generated method stub
//		
//	    ClassLoader cl = Configure.class.getClassLoader();
//	    InputStream in = cl.getResourceAsStream("Config.properties");
//	    Properties props = new Properties();
//	    props.load(in);
//	    String value = props.getProperty("ipAddress");
//	    System.out.println(value);
////		Properties pro = new Properties();
////		FileInputStream in = new FileInputStream("WEB-INF\\Config.properties");
////		pro.load(in);
////        String value = pro.getProperty("username");
////        System.out.println(value);
////		in.close();
//	    return value; 
//	}
	
}
