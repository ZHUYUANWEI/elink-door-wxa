package com.ssm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Uitl {  
		  
		 /** 
		  * 将文件转成base64 字符串 
		  * @param path文件路径 
		  * @return  *  
		  * @throws Exception 
		  */  
		  
		 public static String encodeBase64File(String path) throws Exception {  
		  File file = new File(path);
		  FileInputStream inputFile = new FileInputStream(file);  
		  byte[] buffer = new byte[(int) file.length()];  
		  inputFile.read(buffer);
		  inputFile.close();
		  return new BASE64Encoder().encode(buffer);  
		  
		 }  
		  
		 /** 
		  * 将base64字符解码保存文件 
		  * @param base64Code 
		  * @param targetPath 
		  * @throws Exception 
		  */  
		  
		 public static void decoderBase64File(String base64Code, String targetPath)  
		   throws Exception {  
		  byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);  
		  FileOutputStream out = new FileOutputStream(targetPath);  
		  out.write(buffer);  
		  out.close();  
		  
		 }  
		  
		 /** 
		  * 将base64字符保存文本文件 
		  * @param base64Code 
		  * @param targetPath 
		  * @throws Exception 
		  */  
		  
		 public static void toFile(String base64Code, String targetPath)  
		   throws Exception {  
		  
		  byte[] buffer = base64Code.getBytes();  
		  FileOutputStream out = new FileOutputStream(targetPath);  
		  out.write(buffer);  
		  out.close();  
		 }
		 
		 public static String InputStreamWithBase64(InputStream in){
			// TODO Auto-generated method stub
		        byte[] data = null;
		        
		        // 读取图片字节数组
		        try {
		            //InputStream in = new FileInputStream(imgFilePath);
		            data = new byte[in.available()];
		            in.read(data);
		            in.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        // 对字节数组Base64编码
		        BASE64Encoder encoder = new BASE64Encoder();
		        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
		    }
		 
		 
}
