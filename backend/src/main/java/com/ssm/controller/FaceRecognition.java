package com.ssm.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.ssm.common.Configure;
import com.ssm.common.template.GetCustQRBuildUp;
import com.ssm.common.template.GetFaceImg;
import com.ssm.common.template.UpLoadFaceImg;
import com.ssm.model.LoadInfo;
import com.ssm.util.Base64Uitl;
import com.ssm.util.DateHelpers;
import com.ssm.util.HttpClientHelper;
import com.ssm.util.JSONHelper;
import com.ssm.util.ObjectAndByte;
import com.ssm.util.ParamterMapSet;
import com.ssm.util.StringBuilder;

import net.sf.json.JSONObject;

@Controller
@SuppressWarnings("all")
public class FaceRecognition {
	
    //注释先锋
    private Logger logger = LoggerFactory.getLogger(getClass());
	
    //获取人脸信息
	@RequestMapping("/getFaceInfo")
    public void getFaceInfo(HttpServletRequest request, HttpServletResponse response,String name,String startTime) throws Exception{
		String time = DateHelpers.formatDateTime(new Date());
		logger.info("getFaceInfo being serve at time "+time);
		//获得前端发送的交易参数，统一转化并存储在map对象中；
		Map ParamterMap = new HashMap();
		//得到servletContext 
		String url = Configure.getConfig(request, "apiAddressFace2");
		//得到token
		String token = Configure.getConfig(request, "serverToken");
		ParamterMap = ParamterMapSet.TakeOutListParamters(request);
		logger.info("getFaceInfo accept mess======="+ParamterMap);
		//进行相关后台业务逻辑处理交易
		//QRCodeService.getGuestQRmess(request);
		//返回前台Json数据
        Map map = new HashMap();
		String resp = null;
		try {
			resp = GetFaceImg.GetAndSendParamters(ParamterMap,url,token);
	        //map.put("resp", resp);
	        Map<String,Object> respMap = new HashMap();
	        respMap = StringBuilder.getOriginalDataFromResquest(resp);
	        map.put("interface", "getFaceInfo");
			map.put("errCode", respMap.get("errCode"));
			map.put("errMsg", respMap.get("errMsg"));
			map.put("result", respMap.get("data"));
		} catch (Exception e) {
			e.printStackTrace();
	        map.put("interface", "getFaceInfo");
			map.put("errCode", "2000");
			map.put("errMsg", "communication exception");
		}
        logger.info("------response format------"+resp);
        JSONObject jsonObject  = JSONObject.fromObject(map);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());
    }
	
	//更新人脸图片，由于涉及文字信息与图片的处理及报文发送，无逻辑处理，所以本交易未解耦
    @RequestMapping("/getFaceUpdate")
    public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	//获取程序执行开始的时间
    	long startTime=System.currentTimeMillis();
		String time = DateHelpers.formatDateTime(new Date());
		logger.info("getFaceUpdate being serve at time "+time);
    	//获得前端发送的交易参数，统一转化并存储在map对象中；
		Map ParamterMap = new HashMap();
        //后端的返回报文与参数
		Map map = new HashMap();
		String resp = null;
		//得到servletContext
		String url = Configure.getConfig(request, "apiAddressFace1");
		//得到token
		String token = Configure.getConfig(request, "serverToken");
		//涉及判断图片大小的部分
		Boolean outSizeFlag = false;
		String photoSize = null;
//        //1.得到servletContext  
//        ServletContext sc=request.getServletContext();  
//        //2.得到属性和它对应的值
//        String http=(String)sc.getAttribute("apiAddressFace1");
//        String url = http;
//        logger.info("API="+http);
    	//--------------------------------获取文件需要放置到的服务器路径，仅测试时使用，正常情况下，生产代码应为注释状态
//        String path = request.getSession().getServletContext().getRealPath("/upload") + "/";
//        logger.info("11111"+path);
//        File dir = new File(path);
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        logger.info("path=" + path);
        //--------------------------------↑获取文件需要放置到的服务器路径，仅测试时使用，正常情况下，生产代码应为注释状态
        request.setCharacterEncoding("utf-8");  //设置编码
        //获得磁盘文件条目工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //如果没以下两行设置的话,上传大的文件会占用很多内存，
        //设置暂时存放的存储室,这个存储室可以和最终存储文件的目录不同
        /**
         * 原理: 它是先存到暂时存储室，然后再真正写到对应目录的硬盘上，
         * 按理来说当上传一个文件时，其实是上传了两份，第一个是以 .tem 格式的
         * 然后再将其真正写到对应目录的硬盘上
         */
//        factory.setRepository(dir);
        //设置缓存的大小，当上传文件的容量超过该缓存时，直接放到暂时存储室
        factory.setSizeThreshold(1024 * 1024);
        //高水平的API文件上传处理
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> list = upload.parseRequest(request);
            FileItem picture = null;
            for (FileItem item : list) {
                //获取表单的属性名字
                String name = item.getFieldName();
                //如果获取的表单信息是普通的 文本 信息
                if (item.isFormField()) {
                    //获取用户具体输入的字符串
                    String value = item.getString();
                    ParamterMap.put(name, value);
                    //request.setAttribute(name, value);
                    //logger.info("name=" + name + ",value=" + value);
                } else {
                	picture = item;
                	//如果图片大于10M
                	logger.info("photo size="+item.getSize());
                	if(item.getSize()> 10*1024*1024){
                		logger.info("photo size out of ranage");
                		outSizeFlag = true;
                	}
                }
            }
            //文件后缀部分判断
            String photoSuffix = null;
            String photo = ParamterMap.get("filePath").toString();
            photoSuffix = photo.substring(photo.length()-3);
            //logger.info("there is a "+ photoSuffix + " picture");
            //自定义上传图片的名字为userId.jpg
//            String fileName = "test1234.jpg";
//            String destPath = path + fileName;
//            logger.info("destPath"+destPath);
            logger.info("getFaceUpdate request ParamterMap"+ParamterMap.toString());
            //真正写到磁盘上
//            File file = new File(destPath);
//            OutputStream out = new FileOutputStream(file);
//            InputStream in = picture.getInputStream();
            InputStream anotherin = picture.getInputStream();
//            int length = 0;
//            byte[] buf = new byte[1024];
//            // in.read(buf) 每次读到的数据存放在buf 数组中
//            while ((length = in.read(buf)) != -1) {
//                //在buf数组中取出数据写到（输出流）磁盘上
//                out.write(buf, 0, length);
//            }
            Map params = new HashMap();
            params.put("token", token);
            params.put("projectId", ParamterMap.get("projectId"));
            params.put("cardNo", ParamterMap.get("cardNo"));
            params.put("personId",ParamterMap.get("personId"));
            String fileName = "photo";
//            String url = "http://192.168.0.141:8081/faceservice/uploadPersonFaceImg";
            String bytes = Base64Uitl.InputStreamWithBase64(anotherin);

    		//String resp = null;
            if(outSizeFlag){
    			map.put("errCode", "0");
    			map.put("errMsg", "photo size out of ranage");
            }else if(!photoSuffix.equals("jpg")){
    			map.put("errCode", "0");
    			map.put("errMsg", "photo type not match");
            }else{
        		try {
        			resp = UpLoadFaceImg.GetAndSendParamters(url, params, bytes, fileName);
        	        Map<String,Object> respMap = new HashMap();
        	        respMap = StringBuilder.getOriginalDataFromResquest(resp);
        	        map.put("interface", "getFaceUpdate");
        			map.put("errCode", respMap.get("errCode"));
        			map.put("errMsg", respMap.get("errMsg"));
        			map.put("result", respMap.get("data"));
        		} catch (Exception e) {
        			e.printStackTrace();
        	        map.put("interface", "getFaceUpdate");
        			map.put("errCode", "2000");
        			map.put("errMsg", "communication exception");
        		}
            }
            	
    		//关闭流
    		anotherin.close();
//            in.close();
//            out.close();
//        } catch (FileUploadException e1) {
//            logger.error("", e1);
        } catch (Exception e) {
            logger.error("", e);
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        logger.info("程序运行时间： "+(endTime-startTime)+"ms");
        JSONObject jsonObject  = JSONObject.fromObject(map);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(jsonObject.toString());   
    }
}
