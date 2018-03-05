package com.ssm.common.template;

import java.util.HashMap;
import java.util.Map;

import com.ssm.common.Configure;
import com.ssm.util.HttpClientHelper;

/*
 * author wangzhengyu
 * 
 * function 获取访客的json拼装
 * 
 * datetime 2017-09-29
 * */
public class GetVisitorQRBuildUp {


public static String GetAndSendParamters(Map map,String http,String temp) throws Exception{
//	String token = "";
//	String cardNo = "";
//	String personId = "";
//	String period = "";
//	String unlockCount = "";
//	String visitorName = "";
//	String phone = "";
//	String startTime = "";
//	String endTime = "";
	
	//保留原参数外，对部分需要处理的参数进行拼接
	
	//发送并获取相关请求参数
	//String http = "http://192.168.0.141:8081/QrService/getvisitorqrcode";
	//String temp = "ef0bb79623db2518cbfbd67cdfeff8ab";
	//很多非空判断没有做
	//String projectId = "hk8700projectIdTest";
    Map<String, Object> chargeMap = new HashMap<String, Object>();
    chargeMap.put("projectId",map.get("projectId"));
    chargeMap.put("token",temp);
    chargeMap.put("cardNo",map.get("cardNo"));
    chargeMap.put("personId",map.get("personId"));
    chargeMap.put("period", map.get("period"));
    chargeMap.put("unlockCount",map.get("unlockCount"));
    chargeMap.put("visitorName",map.get("visitorName"));
    chargeMap.put("phone",map.get("phone"));
    chargeMap.put("startTime",map.get("startTime"));
    chargeMap.put("endTime",map.get("endTime"));
    String result=HttpClientHelper.httpPostWithJSON(http, chargeMap, Configure.CHARSET_DEFAULT);
	return result;
	 
 }
}
