package com.ssm.controller;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssm.common.Configure;
import com.ssm.common.template.GetCustQRBuildUp;
import com.ssm.common.template.GetVisitorQRBuildUp;
import com.ssm.service.QRCodeService;
import com.ssm.util.DateHelpers;
import com.ssm.util.GetRequestUtils;
import com.ssm.util.ParamterMapSet;
import com.ssm.util.StringBuilder;

import net.sf.json.JSONObject;
/*
 * author wangzhengyu
 * 
 * function 控制和客户二维码有关的交易(take control the transaction between cust and QR control)
 * 
 * datetime 2017-09-29
 * */
@Controller
@SuppressWarnings("all")
public class QRCodeController {
    //注释先锋
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    QRCodeService QRCodeService;
	@RequestMapping("/getGuestQR")
    public void getGuestQR(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String time = DateHelpers.formatDateTime(new Date());
		logger.info("getGuestQR being serve at time "+time);
		//获得前端发送的交易参数，统一转化并存储在map对象中；
		Map ParamterMap = new HashMap();
		//得到servletContext 
		String url = Configure.getConfig(request, "apiAddressQR2");
		//得到token
		String token = Configure.getConfig(request, "serverToken");
		//统一处理前端发送的json参数
		ParamterMap = ParamterMapSet.TakeOutParamters(request);
		logger.info("getGuestQR accept mess="+ParamterMap);
		//进行相关后台交易
		//QRCodeService.getGuestQRmess(request);
		//拼装报文并发送json数据
		//返回前台Json数据
        Map map = new HashMap();
		String resp = null;

		try {
			resp = GetVisitorQRBuildUp.GetAndSendParamters(ParamterMap,url,token);
	        //map.put("resp", resp);
	        Map<String,Object> respMap = new HashMap();
	        respMap = StringBuilder.getOriginalDataFromResquest(resp);
	        map.put("interface", "getGuestQR");
			map.put("errCode", respMap.get("errCode"));
			map.put("errMsg", respMap.get("errMsg"));
			map.put("result", respMap.get("data"));
//			temp = map.get("result") == null ? "" : map.get("result").toString();
//			tempMap = StringBuilder.getOriginalDataFromResquest(temp);
//			map.put("result", tempMap.get("data"));
	        logger.info("-----response format--------"+map.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        map.put("interface", "getGuestQR");
			map.put("errCode", "2000");
			map.put("errMsg", "communication exception");
		}
        JSONObject jsonObject  = JSONObject.fromObject(map);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());
    }
	
	@RequestMapping("/getCustomQR")
    public void getCustomQR(HttpServletRequest request, HttpServletResponse response,String name,String startTime) throws Exception{
		String time = DateHelpers.formatDateTime(new Date());
		logger.info("getCustomQR being serve at time "+time);
		//获得前端发送的交易参数，统一转化并存储在map对象中；
		Map ParamterMap = new HashMap();
		//得到servletContext
		String url = Configure.getConfig(request, "apiAddressQR1");
		//得到token
		String token = Configure.getConfig(request, "serverToken");
		//统一处理前端发送的json参数
		ParamterMap = ParamterMapSet.TakeOutParamters(request);
		logger.info("getCustomQR accept mess="+ParamterMap);
		//进行相关后台交易
		//QRCodeService.getGuestQRmess(request);
		//返回前台Json数据
        Map map = new HashMap();
		String resp = null;
		//临时数据，用于解决特殊的返回报文格式问题
		String temp = null;
		Map tempMap = new HashMap();
		try {
			resp = GetCustQRBuildUp.GetAndSendParamters(ParamterMap,url,token);
	        //map.put("resp", resp);
	        Map<String,Object> respMap = new HashMap();
	        respMap = StringBuilder.getOriginalDataFromResquest(resp);
	        map.put("interface", "getCustomQR");
			map.put("errCode", respMap.get("errCode"));
			map.put("errMsg", respMap.get("errMsg"));
			map.put("result", respMap.get("data"));
//			temp = respMap.get("result") == null ? "" : respMap.get("result").toString();
//			if(!temp.equals("")){
//				tempMap = StringBuilder.getOriginalDataFromResquest(temp);
//				map.put("result", tempMap.get("data"));
//			}else{
//				map.put("result", "");
//			}
			
	        logger.info("-----response format--------"+map.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        map.put("interface", "getCustomQR");
			map.put("errCode", "2000");
			map.put("errMsg", "communication exception");
		}
        JSONObject jsonObject  = JSONObject.fromObject(map);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());
    }
}
