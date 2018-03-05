package com.ssm.service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FaceService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	//通过本方法对其进行相关的逻辑处理
	public Map<String,Object> getFaceUpdateMess(HttpServletRequest request) throws Exception{
		//随后返回的主题业务的参数map
		Map<String,Object> map = new HashMap<String, Object>();
		//拆分请求中的参数
        //1.得到servletContext  
        ServletContext sc=request.getServletContext();  
        //2.得到属性和它对应的值
        String http=(String)sc.getAttribute("apiAddressFace1");
        logger.info("API="+http);
    	
    	//--------------------------------获取文件需要放置到的服务器路径，仅测试时使用，正常情况下，生产代码应为注释状态
        String path = request.getSession().getServletContext().getRealPath("/upload") + "/";
        logger.info("11111"+path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        logger.info("path=" + path);
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
                    map.put(name, value);
                    //request.setAttribute(name, value);
                    //logger.info("name=" + name + ",value=" + value);
                } else {
                	picture = item;
                }
            }
            //自定义上传图片的名字为userId.jpg
//            String fileName = "test1234.jpg";
//            String destPath = path + fileName;
//            logger.info("destPath"+destPath);
            logger.info("getFaceUpdate request ParamterMap="+map.toString());
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
    		//关闭流
    		anotherin.close();
//            in.close();
//            out.close();
        } catch (FileUploadException e1) {
            logger.error("", e1);
        } catch (Exception e) {
            logger.error("", e);
        }
		
	
        return map;
	}
	
}


