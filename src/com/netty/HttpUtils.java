package com.netty;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * <p>
 *  This is an Util class to handle both GET and POST request
 * </p>
 */

public class HttpUtils {

	CloseableHttpClient client = null;
	String responceString = null;
	
	public String doJsonPost(String url, Map<String,String> param) throws Exception {
		
		try {
			client = HttpClientBuilder.create().build();
			
			JSONObject postParamJson = new JSONObject();
			
			for(Map.Entry<String, String> entry : param.entrySet()) {
				postParamJson.put(entry.getKey(), entry.getValue());
			}
			
			HttpPost postReq = new HttpPost(url);
			
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("json", postParamJson.toString()));
			
			postReq.setEntity(new UrlEncodedFormEntity(nvps));
			
			CloseableHttpResponse responce = client.execute(postReq);
			
			responceString = EntityUtils.toString(responce.getEntity(),"UTF-8");
			
		}catch(Exception e){
			throw new RuntimeException("Executing Post Request Failed With Exception",e);
		}
		finally{
			client.close();
		}
		return responceString;
	}
	
	public String doJsonGet(String url, Map<String,String> param) throws Exception {
		
		try {
			client = HttpClientBuilder.create().build();
			
			JSONObject postParamJson = new JSONObject();
			
			for(Map.Entry<String, String> entry : param.entrySet()) {
				postParamJson.put(entry.getKey(), entry.getValue());
			}
			
			HttpGet getReq = new HttpGet(url+"/param?json="+URLEncoder.encode(postParamJson.toString()));
			
			CloseableHttpResponse responce = client.execute(getReq);
			
			responceString = EntityUtils.toString(responce.getEntity(),"UTF-8");
		}
		catch(Exception e) { 
			e.printStackTrace(); 
		}
		finally{
			client.close();
		}
		
		return responceString;
	}
	
}
