package com.ssm.util;

/*import com.baomidou.springmvc.common.result.FlowException;
import com.baomidou.springmvc.common.result.OnTimeOutException;*/
import com.ssm.common.FlowException;
import com.ssm.common.OnTimeOutException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;


/**
 * HttpClient工具类， 封装了一些采用HttpClient发送HTTP请求的方法
 * 
 * @author WLing
 * 
 */
public class HttpClientHelper {
	private final static Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);
	//默认采用的http协议的HttpClient对象
	private static CloseableHttpClient httpClient;
	//默认采用的https协议的HttpClient对象
	private static CloseableHttpClient httpsClient;
	private static RequestConfig requestConfig;
	private static PoolingHttpClientConnectionManager connManager;
	private static final int MAX_TIMEOUT1 = 20000; // 7000ms
	private static final int MAX_TIMEOUT2 = 80000; //连接超时 
	private static final int MAX_TIMEOUT3 = 100000; //请求超时
	public static String CHARSET_DEFAULT = "UTF-8";
	
	//httpURLConnection.setRequestProperty("connection", "Keep-Alive");
    //设置cookie管理策略
    //client.getState().setCookiePolicy(CookiePolicy.COMPATIBILITY);
	/**
	 * 最大允许连接数
	 */
	private static final int MAX_TOTAL_CONNECTION = 800;
	static {
		//采用绕过验证的方式处理https请求  
	    SSLContext sslcontext = createIgnoreVerifySSL();  
       // 设置协议http和https对应的处理socket链接工厂的对象  
       Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
           .register("http", PlainConnectionSocketFactory.INSTANCE)
           .register("https", new SSLConnectionSocketFactory(sslcontext))
           .build();  
		// 设置连接池
		connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		
		// 设置连接池大小
		connManager.setMaxTotal(MAX_TOTAL_CONNECTION);
		connManager.setDefaultMaxPerRoute(300);//WL：2016-12-29出现异常ConnectionPoolTimeoutException将40改为300
		// 设置连接超时	// 设置全局的标准cookie策略r.setCookieSpec(CookieSpecs.STANDARD_STRICT);
		/*设置ConnectionPoolTimeout：从连接池中取连接的超时时间;ConnectionPoolTimeoutException
		 *设置ConnectionTimeout：连接超时 ,这定义了通过网络与服务器建立连接的超时时间。ConnectionTimeoutException  ==>HttpHostConnectException
		 *设置 SocketTimeout：请求超时即读取超时 ,这定义了Socket读数据的超时时间，即从服务器获取响应数据需要等待的时间，此处设置为4秒。SocketTimeoutException*/
		requestConfig = RequestConfig.custom().setConnectionRequestTimeout(MAX_TIMEOUT1).setConnectTimeout(MAX_TIMEOUT2).setSocketTimeout(MAX_TIMEOUT3).build();
		//httpClient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).build();// 设置可关闭的HttpClient
		httpClient = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig).build();
		
		httpsClient = HttpClients.createDefault();
	}
	/** 
	 * 绕过验证 
	 *   
	 * @return 
	 */  
	public static SSLContext createIgnoreVerifySSL() {  
	    SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
		    X509TrustManager trustManager = new X509TrustManager() {  
		    	@Override
		        public void checkClientTrusted(  
		                X509Certificate[] paramArrayOfX509Certificate,
		                String paramString) throws CertificateException {  
		        }  
		  
		    	@Override
		        public void checkServerTrusted(  
		                X509Certificate[] paramArrayOfX509Certificate,
		                String paramString) throws CertificateException {  
		        }  
		  
		    	@Override
		        public X509Certificate[] getAcceptedIssuers() {
		            return null;  
		        }  
		    };  
		    sc.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}catch (KeyManagementException e) {
			e.printStackTrace();
		}  
	    return sc;  
	} 
	/**
	 * 释放httpClient连接
	 */

	public static void shutdown() {
		//connManager.shutdown();
		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 组装头部
	 * 
	 * @param map
	 *            头部map信息
	 * @return
	 */
	public static Header[] builderHeader(Map<String, String> map) {
		Header[] headers = new BasicHeader[map.size()];
		int i = 0;
		for (String str : map.keySet()) {
			headers[i] = new BasicHeader(str, map.get(str));
			i++;
		}
		return headers;
	}

	/**
	 * 发送 GET 请求（HTTP），对应的参数连接，例如：account=qdwangxkj&mobile=13105186219&package=0
	 * 
	 * @param params
	 * @return
	 */
	public static String getParams(Map<String, Object> params,boolean urlFlag) {
		StringBuffer param = new StringBuffer();
		int i = 0;
		for (String key : params.keySet()) {
			if (i == 0 && urlFlag){
				param.append("?");
				}
			else if(i!=0){
				param.append("&");
			}
			param.append(key).append("=").append(params.get(key));
			i++;
		}
		return param.toString();
		
	}
	public static String getParamsByDesc(Map<String, Object> params) {//降序
		List<Entry<String,Object>> entryList = new ArrayList<Entry<String,Object>>(params.entrySet());
		Collections.sort(entryList, new Comparator<Entry<String,Object>>() {
			@Override
			public int compare(Entry<String, Object> o1,
					Entry<String, Object> o2) {
				return	(o1.getKey().compareTo(o2.getKey()));
			}
		});
		StringBuffer param = new StringBuffer();
		for(Entry<String,Object> entry: entryList){
			param.append(entry.getKey()).append("=").append(entry.getValue());
			param.append("&");
		}
		return param.toString();
		
	}
	public static String getParamsByAscending(Map<String, String> params) {// 升序
		List<Entry<String, String>> entryList = new ArrayList<Entry<String, String>>(
				params.entrySet());
		Collections.sort(entryList,
				new Comparator<Entry<String, String>>() {
			@Override
					public int compare(Entry<String, String> o1,
							Entry<String, String> o2) {
						//System.out.println(o2.getKey().compareTo(o1.getKey()));
						return (o1.getKey().compareTo(o2.getKey()));
					}
				});
		StringBuffer param = new StringBuffer();
		for (Entry<String, String> entry : entryList) {
			param.append(entry.getKey()).append("=").append(entry.getValue());
			param.append("&");
		}
		return param.toString();

	}
	public static String toFrom(Map<String, String> params) {//map转k-V
		List<Entry<String, String>> entryList = new ArrayList<Entry<String, String>>(
				params.entrySet());
		StringBuffer param = new StringBuffer();
		for (Entry<String, String> entry : entryList) {
			param.append(entry.getKey()).append("=").append(entry.getValue());
			param.append("&");
		}
		return param.toString();

	}
	/**
	 * 发送 GET 请求（HTTP），K-V形式
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public static String get(String url, Map<String, Object> params , String encode) {
		url += getParams(params,true);
		logger.info("==>GET请求URL："+url);
		String result = null;
		// HttpClient httpClient = new DefaultHttpClient();
		CloseableHttpResponse response = null;
		HttpGet httpGet = null;
		try {
			//logger.info("请求参数"+url);
			httpGet = new HttpGet(url);
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpGet);//TODO:===============????????????
				}else{
					response = httpClient.execute(httpGet);
				}
				logger.info("响应："+response);
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常"+pe.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常"+ce.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常"+se.toString());
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常:"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				result = EntityUtils.toString(entity,encode);
				//logger.info("==>响应报文:"+result);
			} catch (Exception e) {//响应异常
				logger.info("==>解析响应报文异常:"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					logger.info("",e);
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						logger.info("",e);
						e.printStackTrace();
					}
				}
			}
			httpGet.releaseConnection();
			shutdown();
		}
		return result;
	}
	
	/**
	 * 发送 GET 请求（HTTP），K-V形式
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static String get(String url, String encode) {
		String result = null;
		// HttpClient httpClient = new DefaultHttpClient();
		CloseableHttpResponse response = null;
		HttpGet httpGet = null;
		try {
			logger.info("请求参数"+url);
			httpGet = new HttpGet(url);
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpClient.execute(httpGet);//TODO:===============????????????
				}else{
					response = httpClient.execute(httpGet);
				}
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常");
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常");
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常");
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				result = EntityUtils.toString(entity,encode);
				//logger.info("==>响应结果"+result);
			} catch (Exception e) {//响应异常
				logger.info("==>解析响应结果异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					logger.info("",e);
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						logger.info("",e);
						e.printStackTrace();
					}
				}
			}
			httpGet.releaseConnection();
			shutdown();
		}
		return result;
	}
	
	/*
	 * 只是暂时适用于调用百度的一个号码查询接口
	 */
	public static String get(String httpUrl, Map<String, Object> params  , String encode , String apiKey) {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    httpUrl += getParams(params,true);

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        // 填入apikey到HTTP header
	        connection.setRequestProperty("apikey",  apiKey);
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, encode));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	
	 //山东联通的请求
	public static String post(String url, String params) {
		url += params;
		String result = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			logger.info("请求参数"+url);
			httpPost = new HttpPost(url);
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpPost);
				}else{
					response = httpClient.execute(httpPost);
				}
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常");
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常");
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常");
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				result = EntityUtils.toString(entity,"UTF-8");
				//logger.info("==>响应结果"+result);
			}catch (Exception e) {//其他异常
				logger.info("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					logger.info("",e);
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						logger.info("",e);
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			shutdown();
		}
		return result;
	}

	/**
	 * 发送 POST 请求（HTTP），【查询参数使用json格式】，并返回字符串
	 * 
	 * @param url
	 *            请求地址
	 * @param json
	 *            请求json参数
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static String post(String url, String json, String charSet) {
		Header[] headers = new Header[] {new BasicHeader("Content-Type", "application/json")};
		StringEntity stringEntity = new StringEntity(json, charSet);// 解决中文乱码问题
		stringEntity.setContentEncoding(charSet);
		String httpStr = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeaders(headers);
		try {
			try {
				response = httpClient.execute(httpPost);
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常:{}", pe.getMessage());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常:{}", ce.getMessage());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常:{}", se.getMessage());
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常:"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				logger.info("响应结果"+httpStr);
			}catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return httpStr;
	}
	/**
	 * 发送 POST 请求（HTTP），K-V形式
	 * TODO   可能有map传过去以后拼接成a=1&b=1类型的字符串
	 * @param url API接口URL
	 * @param params 参数map
	 * @return
	 * @throws 
	 */
	public static String post(String url, Map<String, Object> params,String charSet) {
		logger.info("-------------------"+url+params+charSet);
		String httpStr = null;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
			for (Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(),entry.getValue().toString());
				pairList.add(pair);
			}
			logger.info(pairList.toString());
			String temp = JSONHelper.map2json(params);
			logger.info(temp);
			UrlEncodedFormEntity formentity = new UrlEncodedFormEntity(pairList, Charset.forName(charSet));
			httpPost.setEntity(formentity);
			logger.info(formentity.toString());

			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpPost);
				}else{
					response = httpClient.execute(httpPost);
				}
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常");
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常");
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常");
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				logger.info("响应结果"+httpStr);
			} catch (Exception e) {
				logger.info("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try{
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			shutdown();
		}
		return httpStr;
	}
/*	http json请求
 * wangzhengyu
 * 20171006
 * */
	public static String httpPostWithJSON(String url, Map<String, Object> params,String charSet) throws Exception {
		//设置http超时设置，时间设定为8s
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				  .setSocketTimeout(8000)
				  .setConnectTimeout(8000)
				  .setConnectionRequestTimeout(8000)
				  .build();

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.custom()
        		.setDefaultRequestConfig(defaultRequestConfig)
        	    .build();
        String respContent = null;
		String jsonParam = JSONHelper.map2json(params);
		logger.info("--------Request format---------"+jsonParam);
		
//     json方式
        StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题    
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setConfig(defaultRequestConfig);
//        表单方式
//        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>(); 
//        pairList.add(new BasicNameValuePair("name", "admin"));
//        pairList.add(new BasicNameValuePair("pass", "123456"));
//        httpPost.setEntity(new UrlEncodedFormEntity(pairList, "utf-8"));
        //----------------
        CloseableHttpResponse resp = null;
        try{
            resp = client.execute(httpPost);
            //如果相关内容符合
            if(resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he,"UTF-8");
            }
        }catch(Exception e){
        	e.printStackTrace();
        	respContent = "Connection refused with the API back end!";
        }finally {
			if (resp != null) {
				try{
					EntityUtils.consume(resp.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						resp.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			shutdown();
		}

        logger.info("Respond format"+respContent);
        return respContent;
    }
	
	/*	http form请求
	 * wangzhengyu
	 * 20171008
	 * */
	/**
	 * file form post
	 * 
	 * @param url
	 * @param params
	 * @param file
	 * @return
	 */
	public static String postFormData(String url, Map<String, Object> params, String bytes, String fileName) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		String result = null;
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		//builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
		//builder.setContentType(ContentType.MULTIPART_FORM_DATA);
		// int count=0;
		// for (File file:files) {
		// builder.addBinaryBody("file"+count, file);
		// count++;
		// }
		ContentType strContentPhoto = ContentType.create("multipart/form-data", Charset.forName("UTF-8"));
		ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
		builder.addTextBody(fileName, bytes,strContentPhoto);
		for (Entry<String, Object> entry : params.entrySet()) {
			builder.addTextBody(entry.getKey(), entry.getValue().toString(), strContent);
		}
		HttpEntity entity = builder.build();// 生成 HTTP POST 实体
		
		httpPost.setEntity(entity);// 设置请求参数
		logger.info("send format"+httpPost.toString());
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);// 发起请求
			// 并返回请求的响应
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();
				result = EntityUtils.toString(httpEntity, "UTF-8");
				logger.info("responseData="+result);
			} else {
				logger.error("拉取失败,错误编码为：" + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return result;

	}
	
	
	/**
	 * 发送get请求【查询参数使用字符串】，并返回字符串
	 * 
	 * @param url 请求地址
	 * @param headers 请求头部数组
	 * @param charSet 请参数编码格式
	 * @return
	 */
	public static String getHeaders(String url,Map<String, String> headers, String charSet) {
		String httpStr = null;
		CloseableHttpResponse response = null;
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeaders(builderHeader(headers));
		try {
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpGet);
				}else{
					response = httpClient.execute(httpGet);
				}
				logger.info("响应："+response);
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常"+pe.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常"+ce.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常"+se.toString());
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				logger.info("响应结果"+httpStr);
			}catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpGet.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return httpStr;
	}
	
	
	
	/**
	 * 发送post请求【查询参数使用字符串】，并返回字符串
	 * 
	 * @param url 请求地址
	 * @param headers 请求头部数组
	 * @param charSet 请参数编码格式
	 * @return
	 */
	public static String post(String url, String requestContext,Map<String, String> headers, String charSet) {
		StringEntity stringEntity = new StringEntity(requestContext, charSet);// 解决中文乱码问题
		stringEntity.setContentEncoding(charSet);
		String httpStr = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeaders(builderHeader(headers));
		try {
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpPost);
				}else{
					response = httpClient.execute(httpPost);
				}
				try {
					logger.info("响应："+response+"Ma:"+response.getStatusLine().getStatusCode());
				} catch (Exception e) {
				}
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常"+pe.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常"+ce.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常"+se.toString());
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				logger.info("响应结果"+httpStr);
			}catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return httpStr;
	}
	
	/**
	 * 发送post请求【查询参数使用独立的参数生成】并返回字符串
	 * 
	 * @param url 请求地址
	 * @param map 请求参数Map
	 * @param headers 请求头部Map
	 * @param charSet 请参数编码格式
	 * @return 
	 */
	public static String post(String url, Map<String, String> map,Map<String, String> headers, String charSet) {
		String httpStr = null;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : map.keySet()) {
			NameValuePair nvp = new BasicNameValuePair(key, map.get(key));
			nvps.add(nvp);
		}
		try {
			HttpEntity entity = new UrlEncodedFormEntity(nvps, charSet);
			HttpPost httpPost = null;
			CloseableHttpResponse response = null;
			try {
				httpPost = new HttpPost(url);
				httpPost.setEntity(entity);
				httpPost.setHeaders(builderHeader(headers));
				try {
					response = httpClient.execute(httpPost);
					logger.info("响应："+response);
				} catch (ConnectionPoolTimeoutException pe) {//请求异常
					logger.warn("==>ConnectionPoolTimeoutException异常"+pe.toString());
					throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
				}catch (HttpHostConnectException ce) {//请求异常
					logger.warn("==>HttpHostConnectException异常"+ce.toString());
					throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
				}catch (SocketTimeoutException se) {//响应异常
					logger.warn("==>SocketTimeoutException异常"+se.toString());
					throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
				} catch (Exception e) {//其他异常//TODO:throw之后的是否执行？finally怎么办
					logger.warn("==>Exception异常"+e.toString());
					e.printStackTrace();
					throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
				}
				HttpEntity httpEntity = response.getEntity();
				try {
					httpStr = EntityUtils.toString(httpEntity, charSet);
				} catch (Exception e) {
					e.printStackTrace();
					throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
				}
			} finally {
				if (response != null) {
					try {
						EntityUtils.consume(response.getEntity());
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
						//关闭CloseableHttpResponse
						try {
							response.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				httpPost.releaseConnection();
				shutdown();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpStr;
	}
	
	/**
	 * HJ 2016年10月26日12:00:23修改--返回的是map
	 * 山东移动用
	 * 发送post请求【查询参数使用字符串】，并返回字符串
	 * 
	 * @param url 请求地址
	 * @param headers 请求头部数组
	 * @param charSet 请参数编码格式
	 * @return
	 */
	public static Map<String, String> postReturnMap(String url, String requestContext,Map<String, String> headers, String charSet) {
		StringEntity stringEntity = new StringEntity(requestContext, charSet);// 解决中文乱码问题
		stringEntity.setContentEncoding(charSet);
		Map<String, String> returnMap = new HashMap<String,String>();
		String httpStr = null;
		String httpCodeStr = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeaders(builderHeader(headers));
		try {
			try {
				if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpPost);
				}else{
					response = httpClient.execute(httpPost);
				}
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常"+pe.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常"+ce.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常"+se.toString());
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				logger.info("响应结果"+httpStr);
			}catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			httpCodeStr = String.valueOf(response.getStatusLine().getStatusCode());
			
			returnMap.put("responseXML", httpStr);
			returnMap.put("responseCode", httpCodeStr);
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return returnMap;
	}
	
	/**
	 * HJ 2016-11-30 11:33:13
	 * 北京流量互联
	 * 
	 * @param url 请求地址
	 * @param requestContext 请求参数字符串
	 * @return
	 */
	public static String postHttpBJHL(String url, String requestContext) {
		String response;
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			HttpURLConnection connect = (HttpURLConnection) (new URL(url)
					.openConnection());
			connect.setRequestMethod("POST");
			connect.setDoOutput(true);
			connect.setConnectTimeout(MAX_TIMEOUT2);
			connect.setReadTimeout(MAX_TIMEOUT3);
			connect.setDoInput(true);

			// 连接
			connect.connect();

			// 发送数据
			connect.getOutputStream().write(requestContext.getBytes("UTF-8"));

			// 接收数据
			int responseCode = connect.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = connect.getInputStream();
				byte[] data = new byte[1024];
				int len = 0;
				while ((len = in.read(data, 0, data.length)) != -1) {
					outStream.write(data, 0, len);
				}
				in.close();
			}
			// 关闭连接
			connect.disconnect();
			response = outStream.toString("UTF-8");
		}catch (ConnectionPoolTimeoutException pe) {//请求异常
			logger.warn("==>ConnectionPoolTimeoutException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
		}catch (HttpHostConnectException ce) {//请求异常
			logger.warn("==>HttpHostConnectException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
		}catch (SocketTimeoutException se) {//响应异常
			logger.warn("==>SocketTimeoutException异常");
			throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
		} catch (Exception e) {//其他异常
			logger.warn("==>Exception异常"+e.toString());
			e.printStackTrace();
			throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
		}
		return response;
	}
	
	/**
	 * HJ 2016-10-26 20:04:17修改--返回的是map
	 * 山东移动用postHttps
	 * 发送post请求【查询参数使用字符串】，并返回字符串
	 * 
	 * @param url 请求地址
	 * @param requestContext 请求参数字符串
	 * @param token 请求头部数组
	 * @param signatrue 请参数编码格式
	 * @return
	 */
	public static Map<String, String> postHttpsReturnMap(String url, String requestContext,String token, String signatrue) {
		OutputStream outputStream = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		Map<String, String> returnMap = new HashMap<String,String>();
		StringBuffer resultBuffer = new StringBuffer();
		OutputStreamWriter outputStreamWriter = null;
		String tempLine = null;
		try {
			//以下三句话是发送https请求专用
			SSLContext sc;
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },new java.security.SecureRandom());
			URL localURL = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) localURL.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(MAX_TIMEOUT2);
			conn.setReadTimeout(MAX_TIMEOUT3);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/xml");
			if(StringUtils.isNotBlank(token)){
				conn.addRequestProperty("4GGOGO-Auth-Token", token);
            }
            if(StringUtils.isNotBlank(signatrue)){
            	conn.addRequestProperty("HTTP-X-4GGOGO-Signature", signatrue);
            }
            // 设置套接工厂
            conn.setSSLSocketFactory(sc.getSocketFactory());
            
			outputStream = conn.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(requestContext);
			outputStreamWriter.flush();
			inputStream = conn.getInputStream();
			//没有close
			
			inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
			reader = new BufferedReader(inputStreamReader);
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
			String httpCodeStr = String.valueOf(conn.getResponseCode());
			logger.info("http响应状态为" + httpCodeStr);
			String httpStr = resultBuffer.toString();
			logger.info("httpStr状态为" + httpStr);
			returnMap.put("responseXML", httpStr);
			returnMap.put("responseCode", httpCodeStr);
		}catch (ConnectionPoolTimeoutException pe) {//请求异常
			logger.warn("==>ConnectionPoolTimeoutException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
		}catch (HttpHostConnectException ce) {//请求异常
			logger.warn("==>HttpHostConnectException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
		}catch (SocketTimeoutException se) {//响应异常
			logger.warn("==>SocketTimeoutException异常");
			throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
		} catch (Exception e) {//其他异常
			logger.warn("==>Exception异常"+e.toString());
			e.printStackTrace();
			throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
		}finally {
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnMap;
	}
	
	
	/**
	 * HJ 2016-10-26 20:04:17修改--返回的是map
	 * 山东移动用getHttps
	 * 发送post请求【查询参数使用字符串】，并返回字符串
	 * 
	 * @param url 请求地址
	 * @param requestContext 请求参数字符串
	 * @param token 请求头部数组
	 * @param signatrue 请参数编码格式
	 * @return
	 */
	public static String getHttpsReturnMap(String url, String requestContext,String token, String signatrue) {
		OutputStream outputStream = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		String result = "";
		OutputStreamWriter outputStreamWriter = null;
		try {
			//以下三句话是发送https请求专用
			SSLContext sc;
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },new java.security.SecureRandom());
            URL localURL = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) localURL.openConnection();
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            // 打开和URL之间的连接
			conn = (HttpsURLConnection) localURL.openConnection();
            // 设置通用的请求属性
			conn.setConnectTimeout(MAX_TIMEOUT2);
			conn.setReadTimeout(MAX_TIMEOUT3);
			conn.setSSLSocketFactory(sc.getSocketFactory());
            //setRequestProperty设置头文件
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			
			if(StringUtils.isNotBlank(token)){
				conn.addRequestProperty("4GGOGO-Auth-Token", token);
			}
			if(StringUtils.isNotBlank(signatrue)){
				conn.addRequestProperty("HTTP-X-4GGOGO-Signature", signatrue);
			}
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setUseCaches(false);
            // 建立实际的连接
			conn.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                logger.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
		}catch (ConnectionPoolTimeoutException pe) {//请求异常
			logger.warn("==>ConnectionPoolTimeoutException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
		}catch (HttpHostConnectException ce) {//请求异常
			logger.warn("==>HttpHostConnectException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
		}catch (SocketTimeoutException se) {//响应异常
			logger.warn("==>SocketTimeoutException异常");
			throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
		} catch (Exception e) {//其他异常
			logger.warn("==>Exception异常"+e.toString());
			e.printStackTrace();
			throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
		}finally {
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * HJ 2016-12-1 10:55:57修改--返回的是map
	 * 黑龙江移动用postHttp
	 * 发送post请求【查询参数使用字符串】，并返回字符串
	 * 
	 * @param url 请求地址
	 * @param requestContext 请求参数字符串
	 * @param token 
	 * @param signatrue 
	 * @return
	 */
	public static Map<String, String> postHttpReturnMap(String url, String requestContext,String token, String signatrue) {
		OutputStream outputStream = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		Map<String, String> returnMap = new HashMap<String,String>();
		StringBuffer resultBuffer = new StringBuffer();
		OutputStreamWriter outputStreamWriter = null;
		String tempLine = null;
		try {
			URL localURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) localURL.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(MAX_TIMEOUT2);
			conn.setReadTimeout(MAX_TIMEOUT3);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/xml");
			if(StringUtils.isNotBlank(token)){
				conn.addRequestProperty("4GGOGO-Auth-Token", token);
            }
            if(StringUtils.isNotBlank(signatrue)){
            	conn.addRequestProperty("HTTP-X-4GGOGO-Signature", signatrue);
            }
            // 设置套接工厂
            
			outputStream = conn.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(requestContext);
			outputStreamWriter.flush();
			inputStream = conn.getInputStream();
			//没有close
			
			inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
			reader = new BufferedReader(inputStreamReader);
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
			String httpCodeStr = String.valueOf(conn.getResponseCode());
			logger.info("http响应状态为" + httpCodeStr);
			String httpStr = resultBuffer.toString();
			logger.info("httpStr状态为" + httpStr);
			returnMap.put("responseXML", httpStr);
			returnMap.put("responseCode", httpCodeStr);
		}catch (ConnectionPoolTimeoutException pe) {//请求异常
			logger.warn("==>ConnectionPoolTimeoutException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
		}catch (HttpHostConnectException ce) {//请求异常
			logger.warn("==>HttpHostConnectException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
		}catch (SocketTimeoutException se) {//响应异常
			logger.warn("==>SocketTimeoutException异常");
			throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
		} catch (Exception e) {//其他异常
			logger.warn("==>Exception异常"+e.toString());
			e.printStackTrace();
			throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
		}finally {
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	//https  发送xml请求的辅助方法
	private static class TrustAnyTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}
	//https  发送xml请求的辅助方法
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
	//https  发送xml请求的方法
	public static String sendHttpsXmlRequest(String url,String params){
		OutputStream outputStream = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		OutputStreamWriter outputStreamWriter = null;
		String tempLine = null;
		try {
			//以下三句话是发送https请求专用
			SSLContext sc;
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },new java.security.SecureRandom());
			URL localURL = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) localURL.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(MAX_TIMEOUT2);
			conn.setReadTimeout(MAX_TIMEOUT3);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "text/plain");
			outputStream = conn.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(params);
			outputStreamWriter.flush();
			inputStream = conn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
			reader = new BufferedReader(inputStreamReader);
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
		}catch (ConnectionPoolTimeoutException pe) {//请求异常
			logger.warn("==>ConnectionPoolTimeoutException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
		}catch (HttpHostConnectException ce) {//请求异常
			logger.warn("==>HttpHostConnectException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
		}catch (SocketTimeoutException se) {//响应异常
			logger.warn("==>SocketTimeoutException异常");
			throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
		} catch (Exception e) {//其他异常
			logger.warn("==>Exception异常"+e.toString());
			e.printStackTrace();
			throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
		}finally {
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultBuffer.toString();
	}
	public static String sendHttpXmlRequest(String url,String params){
		OutputStream outputStream = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		OutputStreamWriter outputStreamWriter = null;
		String tempLine = null;
		try {
			URL localURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) localURL.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "text/plain");
			outputStream = conn.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(params);
			outputStreamWriter.flush();
			inputStream = conn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
			reader = new BufferedReader(inputStreamReader);
			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
		}catch (ConnectionPoolTimeoutException pe) {//请求异常
			logger.warn("==>ConnectionPoolTimeoutException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
		}catch (HttpHostConnectException ce) {//请求异常
			logger.warn("==>HttpHostConnectException异常");
			throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
		}catch (SocketTimeoutException se) {//响应异常
			logger.warn("==>SocketTimeoutException异常");
			throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
		} catch (Exception e) {//其他异常
			logger.warn("==>Exception异常"+e.toString());
			e.printStackTrace();
			throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
		}finally {
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultBuffer.toString();
	}
	/*
	 * 发送webservice请求
	 */
	public static String callWebservice(String url,String request){
		String intXml = request;
		String endpointAddress = url;
		Service service = new Service();
		String ret = null;
		try {
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(endpointAddress));
			QName qn = new QName("http://ws.huawei.com", "intCall");
			Object obj = call.invoke(qn, new Object[] { intXml });
			 ret = (String) obj;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret;
	}
	/* public static void main(String[] args) {
		 Map<String, Object> params = new HashMap<String, Object>();
		 String encode = "UTF-8";
		 params.put("grant_type", "authorization_code");
		 params.put("secret", "3d4cb31e4e4777193542155a6e08d82e");
		 params.put("appid", "wx2b86c5ad6b5b324e");
		 params.put("code", "041gdC410nG8uB1zzn610fsy410gdC4r");
		 get("https://api.weixin.qq.com/sns/oauth2/access_token",params,encode);
		 logger.info("HTTPS请求1");
		 logger.info(get("https://api.weixin.qq.com/sns/oauth2/access_token",params,encode));
		 logger.info("HTTPS请求2");
	 }*/
	
	/**
	 * 使用证书发送post请求
	 * 	目前微信在使用这个方法
	 * @param url 请求地址
	 * @param headers 请求头部数组
	 * @param charSet 请参数编码格式
	 * @return
	 */
	public static String SSLPost(String url, String requestContext,Map<String, String> headers, String charSet, String certPath, String certMchId)  throws Exception{
		KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(certPath));
		try {
		    keyStore.load(instream, certMchId.toCharArray());
		} finally {
		    instream.close();
		}
		
		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom()
		        .loadKeyMaterial(keyStore, certMchId.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		        sslcontext,
		        new String[] { "TLSv1" },
		        null,
		        SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom()
		        .setSSLSocketFactory(sslsf)
		        .build();
	        
		
		StringEntity stringEntity = new StringEntity(requestContext, charSet);// 解决中文乱码问题
		stringEntity.setContentEncoding(charSet);
		String httpStr = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeaders(builderHeader(headers));
		try {
			try {
				/*if(url.toLowerCase().startsWith("https://")){
					response = httpsClient.execute(httpPost);
				}else{
				}*/
				response = httpclient.execute(httpPost);
				logger.info("响应："+response);
			}catch (ConnectionPoolTimeoutException pe) {//请求异常
				logger.warn("==>ConnectionPoolTimeoutException异常"+pe.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,pe.toString());
			}catch (HttpHostConnectException ce) {//请求异常
				logger.warn("==>HttpHostConnectException异常"+ce.toString());
				throw new FlowException(OnTimeOutException.CONNECTION_TIMEOUT,ce.toString());
			}catch (SocketTimeoutException se) {//响应异常
				logger.warn("==>SocketTimeoutException异常"+se.toString());
				throw new FlowException(FlowException.SYSTEM_FLOW,se.toString());
			} catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString()+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
			HttpEntity entity = response.getEntity();
			try {
				httpStr = EntityUtils.toString(entity, charSet);
				logger.info("响应结果"+httpStr);
			}catch (Exception e) {//其他异常
				logger.warn("==>Exception异常"+e.toString());
				e.printStackTrace();
				throw new FlowException(FlowException.SYSTEM_FLOW,e.toString());
			}
		}finally {
			if (response != null) {
				try {
					//释放资源
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//关闭CloseableHttpResponse
					try {
						response.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			httpPost.releaseConnection();
			//关闭HttpClient
			shutdown();
		}
		return httpStr;
	}
	public static void showParams(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<?> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {  
            String paramName = (String) paramNames.nextElement();  
            String[] paramValues = request.getParameterValues(paramName);  
            if (paramValues.length == 1) {  
                String paramValue = paramValues[0];  
                if (paramValue.length() != 0) {  
                    map.put(paramName, paramValue);  
                }  
            }  
        }  
  
        Set<Entry<String, String>> set = map.entrySet();
        //logger.info("------------------------------");  
        for (Entry entry : set) {
        	logger.info("GET请求报文："+entry.getKey() + ":" + entry.getValue());  
        }  
        //logger.info("------------------------------");  
    } 
}
