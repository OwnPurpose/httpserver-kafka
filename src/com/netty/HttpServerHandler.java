package com.netty;

import static io.netty.buffer.Unpooled.copiedBuffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.kafka.KafkaProducer;
import com.protobuff.AccountBook;
import com.protobuff.AccountBook.Account;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

/**
 * <p>
 *  This is an Handler class to handle each request.
 * </p>
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter{

	private String jsonParamString= null;
	JSONObject jsonObject;
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
	    try {

	        if (msg instanceof HttpRequest) {
	        	
	            HttpRequest request = (HttpRequest) msg;
	            
	            jsonObject =  getParametersHandler(request);
	            
	            String acctName = jsonObject.getString("acctNumber");
	            String acctNumber = jsonObject.getString("acctNumber");
	            String acctHolderName = jsonObject.getString("acctNumber");
	            
	            Account account = AccountBook.Account.newBuilder().
	            setAcctNumber(acctName).
	            setAcctName(acctNumber).
	            setAcctHolderName(acctHolderName).build();
	            
	            KafkaProducer producer = new KafkaProducer();
	            
	            producer.send(account);
	            
	            final String responseMessage = "Account Data sent to Kafka Queue...";
	            
	            FullHttpResponse response = new DefaultFullHttpResponse(
	                      HttpVersion.HTTP_1_1,
	                      HttpResponseStatus.OK,
	                      copiedBuffer(responseMessage.getBytes())
	                    );
	            
	            if (HttpHeaders.isKeepAlive(request))
                {
                  response.headers().set(
                    HttpHeaders.Names.CONNECTION,
                    HttpHeaders.Values.KEEP_ALIVE
                  );
                }
                response.headers().set(HttpHeaders.Names.CONTENT_TYPE,
                  "text/plain");
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                  responseMessage.length());
                                    
                ctx.writeAndFlush(response);
	        }
	    }
	    catch(Exception e) { e.printStackTrace(); }
	}

	/**
 	* <p>
	*  @return JSONObject
 	*  This method will create JSONObject object for both GET and POST from queryParam and postBody respectively.
 	* </p>
 	*/
	private JSONObject getParametersHandler(HttpRequest request) throws IOException {
	   
		Map<String, Object> map = new HashMap<String, Object>();
	    
	    JSONObject jsonObject = null;
	    
	    if (request.getMethod().name().equals("POST")) {
	    	
	        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
	        List<InterfaceHttpData> interfaceHttpDatas = decoder.getBodyHttpDatas();
	        
	        for (InterfaceHttpData data : interfaceHttpDatas) {
	        	
	            Attribute attribute = (Attribute) data;
	            map.put(attribute.getName(), attribute.getValue());
	        }
	        
	        jsonParamString =(String) map.get("json");
	        map.clear();
	        
	        jsonObject = new JSONObject(jsonParamString);
	    } 
	    else {
	       
	    	QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
	    	
	        String jsonParamString = queryStringDecoder.parameters().get("json").get(0);
	        
	        jsonObject = new JSONObject(jsonParamString);
	    }
	    
	    return jsonObject;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		ctx.close();
	}
}
