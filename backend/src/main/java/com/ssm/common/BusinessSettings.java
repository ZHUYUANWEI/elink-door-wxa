package com.ssm.common;

/*
 * 
 * author	wangzhengyu
 * function	为服务组建，提供系统配置外的额外配置辅助(service to the module for more settings which have no relation about the system)
 * datatime	2017-09-30
 * 
 * */
public class BusinessSettings {
	
	//存在汉语的数据名称配置表
	public static String[] GBKinside = {"visitorName"};
	public static String[] Arrinside = {"personIds"};
	public static String[] Integerinside = {"personId","unlockCount","acceptNotice"};
	public static String[] DateInside = {"startTime","endTime"};
}
