package com.ssm.common.template;

import java.util.HashMap;
import java.util.Map;

import com.ssm.util.HttpClientHelper;

/*
 * author wangzhengyu
 * 
 * function 获取访客的json拼装
 * 
 * datetime 2017-09-29
 * */
public class UpLoadFaceImg {


public static String GetAndSendParamters(String url, Map<String, Object> params, String bytes, String fileName) throws Exception{
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
	
	String temp = "ef0bb79623db2518cbfbd67cdfeff8ab";
	//很多非空判断没有做
	//String projectId = "hk8700projectIdTest";
    Map<String, Object> chargeMap = new HashMap<String, Object>();
    chargeMap.put("projectId",params.get("projectId"));
    chargeMap.put("token",temp);
    chargeMap.put("cardNo",params.get("cardNo"));
    chargeMap.put("personId",params.get("personId"));
    chargeMap.put("photo", params.get("photo"));
    String result=HttpClientHelper.postFormData(url, params, bytes, fileName);
	return result;
	 
 }
}
