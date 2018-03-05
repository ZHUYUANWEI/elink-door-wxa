package com.ssm.util;

import java.util.Map;

import net.sf.json.JSONObject;


public class StringBuilder {
	
	//字符串数组转化为Integer数字数组
	public static Integer[] getOriginalDataFromString(String param){

        String[] array = param.substring(1,param.length()-1).split(",");  
        //System.out.println("字符串转化为数组：");
        Integer[] finallyOpen;
        finallyOpen = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
        	finallyOpen[i] = Integer.valueOf(array[i]);
            //System.out.print(array[i]+"###########");  
        }
		return finallyOpen;	
	}
	
	//字符串json转化为map
	public static Map<String,Object> getOriginalDataFromResquest(String param){
//		Map<String, Object> respMap = new HashMap<String, Object>();
//		String[] array = param.substring(1,param.length()-1).split(",");  
//        for (int i = 0; i < array.length; i++) {
//        	String key = "";
//            String value = "";
//        	String mess[] = array[i].split(":");
//        	key = mess[0];
//        	value = mess[1];
//        	respMap.put(key, value);
//            System.out.println(array[i]+"###########");  
//        }
		JSONObject jasonObject = JSONObject.fromObject(param);
		@SuppressWarnings("unchecked")
		Map<String, Object> respMap = (Map<String, Object>)jasonObject;
        //System.out.println(respMap+"###########");
		return respMap;
	}
	
}
