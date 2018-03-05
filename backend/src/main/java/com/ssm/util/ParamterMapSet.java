package com.ssm.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.ssm.common.BusinessSettings;

/*
 * author	wangzhengyu
 * datatime 2017-09-30
 * */
public class ParamterMapSet {
	
	public static Map<String, Object> TakeOutParamters(HttpServletRequest request) throws IOException{
		//创建返回的map对象
		Map<String, Object> paramter = new HashMap<String, Object>();
//    	String temp1 = request.getParameter("visitorName");
//    	String tempvalue1 = new String(temp1.getBytes("ISO8859-1"),"UTF-8");
		//获得传输请求的方法post，get
		//String submitMehtod = request.getMethod();
		//获得前台Json输入数据
		String mess = GetRequestUtils.getRequestJsonString(request);
//		通过数据进行遍历
		String messArr[] = mess.split("&");
		System.out.println("messArr"+messArr);
        for (int i=0;i<messArr.length;i++){
            String key = "";
            String value = "";
        	String messUnit[] = messArr[i].split("=");
            key = messUnit[0].toString().trim();
            value = messUnit[1].toString().trim();
            //判断相关数据是否包含在需要转译的汉语提示符内
            if(Contains.constantList(BusinessSettings.GBKinside, key)){
            	String name = request.getParameter(key);
            	value = new String(name.getBytes("ISO8859-1"),"UTF-8");
            }else if(Contains.constantList(BusinessSettings.Integerinside, key)){
            	//如果是数组内容，将从数据中重新进行格式转换后发送
            	Integer param = Integer.parseInt(request.getParameter(key));
            	paramter.put(key, param);
            	continue;
            }else if(Contains.constantList(BusinessSettings.DateInside, key)){
            	value = request.getParameter(key);
            }
            paramter.put(key,value);
        }
        System.out.println("paramter="+paramter);
		return paramter;
	}
	
	//获取前端发送的数组信息
	public static Map<String, Object> TakeOutListParamters(HttpServletRequest request) throws IOException{
		//创建返回的map对象
		Map<String, Object> paramter = new HashMap<String, Object>();
		//获得前台原始数据
		String mess = GetRequestUtils.getOriginalData(request);
//		通过数据进行遍历
		String messArr[] = mess.split("&"); 
		System.out.println(messArr);
        for (int i=0;i<messArr.length;i++){
            String key = "";
            String value = "";
        	String messUnit[] = messArr[i].split("=");
            key = messUnit[0].toString().trim();
            value = messUnit[1].toString().trim();
            //判断相关数据是否包含在需要转译的汉语提示符内
            if(Contains.constantList(BusinessSettings.GBKinside, key)){
            	String name = request.getParameter(key);
            	value = new String(name.getBytes("ISO8859-1"),"UTF-8");
            }else if(Contains.constantList(BusinessSettings.Arrinside, key)){
            	value = request.getParameter(key);
            	//如果是数组内容，将从数据中重新进行格式转换后发送
            	Integer[] paramArr = StringBuilder.getOriginalDataFromString(value);
            	paramter.put(key, paramArr);
            	continue;
            }else if(Contains.constantList(BusinessSettings.DateInside, key)){
            	value = request.getParameter(key);
            }
            paramter.put(key,value);
        }
        System.out.println("paramter="+paramter);
		return paramter;
	}
	
	//获取前端发送的数组信息
	public static Map<String, String> TakeOutPhotoParamters(HttpServletRequest request) throws IOException{
		//创建返回的map对象
		Map<String, String> paramter = new HashMap<String, String>();
		//获得前台原始数据
		String mess = GetRequestUtils.getOriginalData(request);
//		通过数据进行遍历
		String messArr[] = mess.split("&"); 
		System.out.println(messArr);
        for (int i=0;i<messArr.length;i++){
            String key = "";
            String value = "";
        	String messUnit[] = messArr[i].split("=");
            key = messUnit[0].toString().trim();
            value = messUnit[1].toString().trim();
            //判断相关数据是否包含在需要转译的汉语提示符内
            if(Contains.constantList(BusinessSettings.GBKinside, key)){
            	String name = request.getParameter(key);
            	value = new String(name.getBytes("ISO8859-1"),"UTF-8");
            }else if(Contains.constantList(BusinessSettings.DateInside, key)){
            	value = request.getParameter(key);
            }
            paramter.put(key,value);
        }
        System.out.println("paramter="+paramter);
		return paramter;
	}
	//获取前端发送的数组信息
	public static Map<String, Object> TakeOutMapParamters(HttpServletRequest request) throws IOException{
		//获得前台原始数据
        Map<String,Object> parmMap =new HashMap<String,Object>();  
        //方式一：getParameterMap()，获得请求参数map  
        Map<String, String[]> map= request.getParameterMap();  
        //参数名称  
        Set<String> key=map.keySet();  
        //参数迭代器  
        Iterator<String> iterator = key.iterator();  
        while(iterator.hasNext()){  
            String k=iterator.next();
            if(Contains.constantList(BusinessSettings.Integerinside, k)){
            	parmMap.put(k, Integer.parseInt(map.get(k)[0]));
            	continue;
            }
            parmMap.put(k, map.get(k)[0]);  
        }  
        System.out.println("parmMap====="+parmMap.toString());  
        System.out.println("paramter="+parmMap);
		return parmMap;
	}
}
