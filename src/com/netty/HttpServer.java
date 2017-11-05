package com.netty;

import com.netty.HttpServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServer {

	private int port;
	boolean live=false;
	
	private EventLoopGroup master = new NioEventLoopGroup();
	private EventLoopGroup slave = new NioEventLoopGroup();
	private ServerBootstrap bs = new ServerBootstrap();
	private ChannelFuture channel ;
	
	public HttpServer(int port){
		this.port = port;
	}
	
	public void start() throws InterruptedException {
		
		live=true;
		
		bs.group(master, slave).
		channel(NioServerSocketChannel.class).
		childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				ChannelPipeline p = ch.pipeline();
				
				 p.addLast("encoder", new HttpResponseEncoder());
				 p.addLast("decoder", new HttpRequestDecoder());
				 p.addLast("aggregator", new HttpObjectAggregator(1048576));
				 p.addLast("http-chunked", new ChunkedWriteHandler());
				 p.addLast("handler", new HttpServerHandler());
			}
		});
		
		channel = bs.bind(port).sync();
	}
	
	public void shutdown() {
	
		master.shutdownGracefully();
		slave.shutdownGracefully();
		
		try {
			channel.channel().closeFuture().sync();
			System.out.println("Server shutdown...");
		}
		catch (InterruptedException e) { 
			e.printStackTrace(); 
		}
	}
	
}
