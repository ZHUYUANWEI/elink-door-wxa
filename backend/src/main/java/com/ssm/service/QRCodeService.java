package com.ssm.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QRCodeService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	//通过后台API获取客户二维码URL
	public Map<Object, Object> getCustQRmess(){
		logger.info("--------------");
		Map<Object, Object> map = new HashMap<Object, Object>();
	
		return map;
	}
	
	//通过后台API获取方可二维码URL
	public Map<Object, Object> getGuestQRmess(HttpServletRequest request){
		//拆分请求中的参数
		
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		return map;
	}
	
	
}
