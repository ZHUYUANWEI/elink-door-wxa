package com.ssm.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;  
  
import org.springframework.beans.factory.InitializingBean;  
import org.springframework.stereotype.Component;  
import org.springframework.web.context.ServletContextAware;  

/*
 * author	wangzhengyu
 * function	实现ServletContextAware，可以获得servletcontext
 * datetime	20171009
 * */
//实现ServletContextAware，可以获得servletcontext  
//@Component注解了，直接在xml里配置这个bean就行了，系统自动调用  
@Component   
public class LoadInMemory implements InitializingBean, ServletContextAware {
		//设置初始化内容相关数值，获取自资源目录下的config文件
	@Override
	public void setServletContext(ServletContext arg0) {
		String authorNme = "wangzhengyu";
		arg0.setAttribute("authorNme",authorNme);
		//获取本地配置文件Config.properties信息
	    ClassLoader cl = this.getClass().getClassLoader();
	    InputStream in = cl.getResourceAsStream("Config.properties");
	    Properties props = new Properties();
	    try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    String apiAddressQR1 = props.getProperty("apiAddressQR1");
	    String apiAddressQR2 = props.getProperty("apiAddressQR2");
	    String apiAddressFace1 = props.getProperty("apiAddressFace1");
	    String apiAddressFace2 = props.getProperty("apiAddressFace2");
	    String serverToken = props.getProperty("serverToken");
	    arg0.setAttribute("serverToken",serverToken);
	    arg0.setAttribute("apiAddressQR1",apiAddressQR1);
	    arg0.setAttribute("apiAddressQR2",apiAddressQR2);
	    arg0.setAttribute("apiAddressFace1",apiAddressFace1);
	    arg0.setAttribute("apiAddressFace2",apiAddressFace2);
	    
	    String VImakeAppRegistered = props.getProperty("VImakeAppRegistered");
	    arg0.setAttribute("VImakeAppRegistered",VImakeAppRegistered);
	    String VImakeDeviceAdded = props.getProperty("VImakeDeviceAdded");
	    arg0.setAttribute("VImakeDeviceAdded",VImakeDeviceAdded);
	    String VImakeWhetherReceiveNotice = props.getProperty("VImakeWhetherReceiveNotice");
	    arg0.setAttribute("VImakeWhetherReceiveNotice",VImakeWhetherReceiveNotice);
//		  ServletContext也是和session一样像一张表，分为属性、值
//
//		      添加属性：setAttribute(String name,Object obj);
//
//		      得到值：getAttribute(String name);
//
//		      删除属性：removeAttribute(String name); 
//
//		      生命周期
//
//		  ServletContext中的属性的生命周期从创建开始，到服务器关闭而结束
		}
	@Override
	public void afterPropertiesSet() throws Exception {
		// 在这个方法里面写 初始化的数据也可以。
		}

} 