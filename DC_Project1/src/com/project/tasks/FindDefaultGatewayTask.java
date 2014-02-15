package com.project.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class FindDefaultGatewayTask extends SimpleTask {

	private String def_Gateway = "NULL";

	public FindDefaultGatewayTask() {
		super();

		this.setTaskId("FindDefaultGatewayTask");
	}

	@Override
	public void execute() {

		try {
			System.out.println("ATTEMPTING TO FIND DEFAULT GATEWAY");
			def_Gateway = lookUpDefaultGateway();
		} catch (IOException e) {
			System.out.println("ERROR!");
			e.printStackTrace();
		}

		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub

	}

	public String getDefaultGateway() {
		return def_Gateway;
	}

	private String lookUpDefaultGateway() throws IOException {
		Process result;
		String gateway = "NULL";
		StringTokenizer st;
		String thisLine = "";
		BufferedReader output;

		boolean hasRouteTable = false;

		try {
			
			System.out.println("Executing traceroute");
			result = Runtime.getRuntime()
					.exec("traceroute -m 1 www.google.com");

			output = new BufferedReader(new InputStreamReader(
					result.getInputStream()));
//			thisLine = output.readLine();
			
//			System.out.println("OUTPUT: " + output.readLine());
			
			while (output.readLine() != null) {

				String tmp = output.readLine();
				
				System.out.println("OUTPUT: " + tmp);
				
//				if (tmp.equalsIgnoreCase("IPv4 Route Table")) {
//					hasRouteTable = true;
//				} else if (tmp.contains("========")) {
//					hasRouteTable = false;
//				}
//
//				if (hasRouteTable) {

//					System.out.println(tmp);

					String splitData[] = tmp.split("\\s+");
					st = new StringTokenizer(tmp);
					String decidedDNS = "NULL";
					if (splitData.length > 2) {
						decidedDNS = splitData[2];

						if (!decidedDNS.equalsIgnoreCase("On-link")) {
							System.out.println("Found DNS: " + decidedDNS);
						}
					}
				}
//				thisLine += tmp;
//			}
			
			
			
//			st = new StringTokenizer(thisLine);
//			st.nextToken();
//			gateway = st.nextToken();
//			System.out.printf("The gateway is %s\n", gateway);
		} catch (IOException e) {
			try {
				result = Runtime.getRuntime().exec("netstat -rn");

				output = new BufferedReader(new InputStreamReader(
						result.getInputStream()));
				while (output.readLine() != null) {

					String tmp = output.readLine();
					if (tmp.equalsIgnoreCase("IPv4 Route Table")) {
						hasRouteTable = true;
					} else if (tmp.contains("========")) {
						hasRouteTable = false;
					}

					if (hasRouteTable) {

//						System.out.println(tmp);

						String splitData[] = tmp.split("\\s+");
						st = new StringTokenizer(tmp);
						String decidedDNS = "NULL";
						if (splitData.length > 3) {
							decidedDNS = splitData[3];

							if (!decidedDNS.equalsIgnoreCase("On-link")) {
								System.out.println("Found DNS: " + decidedDNS);
							}
						}
					}
//					thisLine += tmp;
				}

//				System.out.println("LINE FOUND: " + thisLine);

//				st = new StringTokenizer(thisLine);
//				st.nextToken();
//				gateway = st.nextToken();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("CAUGHT IO EXCEPTION");
			}
		}

		return gateway;
	}

}
