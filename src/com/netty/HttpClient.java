package com.netty;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class HttpClient {
  
	public static void main(String arga[]) throws Exception {
		
		HttpUtils httpUtils= new HttpUtils();
		Scanner in = new Scanner(System.in);
		String str,acctNumber,acctName,acctHolderName;
		
		Map<String,String> map = new HashMap<String, String>();
		
		
		do {
			
			System.out.println("Enter Account Number:");
			acctNumber = in.nextLine();
			map.put("acctNumber", acctNumber);
			
			System.out.println("Enter Account Name:");
			acctName = in.nextLine();
			map.put("acctName", acctName);
			
			System.out.println("Enter Account Holder Name:");
			acctHolderName = in.nextLine();
			map.put("acctHolderName", acctHolderName);
			
			map.put("name", acctNumber);
			map.put("version", acctName);
			map.put("app", acctHolderName);
			
			try {
				String  responce = httpUtils.doJsonGet("http://localhost:8080", map );
				map.clear();
				
				System.out.println(responce);
			}
			catch(Exception e) { 
				e.printStackTrace();
			}
			
			System.out.println("Type yes if you want to continue to send data...");

		}while((str=in.nextLine()).equalsIgnoreCase("yes"));
		
		
	}
}