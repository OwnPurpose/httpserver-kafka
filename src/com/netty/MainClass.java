package com.netty;

import java.util.Scanner;

/**
 * <p>
 *  This is Main class. It will start and stop the Server based on our input at runtime.
 * </p>
 */
public class MainClass {

	public static void main(String args[]) {

		Scanner in = new Scanner(System.in);
		String str;
		
		HttpServer server = new HttpServer(8080);
		
		System.out.println("Type Start / Stop for respective action...");
		
		while((str=in.nextLine()).equalsIgnoreCase("start")) {
			try {
				
				if(server.live)
					System.out.println("Server currently Running...");
				else{
					server.start();
					System.out.println("Server started...");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(str.equalsIgnoreCase("stop"))
			server.shutdown();
	}
}
