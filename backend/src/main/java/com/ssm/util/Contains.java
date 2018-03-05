package com.ssm.util;

import java.util.Arrays;

/*
 * author	wangzhengyu
 * function	相关数据是否被所选择的数据类型是否被包含(the data whether exist in the datatype which we chose)
 * */
public class Contains {

    //判断数据是否被包含
    public static boolean constantList(String[] arr,String targetValue){
        return Arrays.asList(arr).contains(targetValue);
    }
	
}
