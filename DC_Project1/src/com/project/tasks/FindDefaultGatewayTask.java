package com.project.tasks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import com.project.framework.Task;

public class FindDefaultGatewayTask extends SimpleAbstractTask {

	/*
	 * Generated ID for Serializable
	 */
	private static final long serialVersionUID = 7828459616841892908L;
	
	private String def_Gateway = "NULL";

	public FindDefaultGatewayTask() {
		super();

		this.setTaskId("FindDefaultGatewayTask");
	}

	public FindDefaultGatewayTask(ITaskCallback callback) {
		super(callback);
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
		System.out.println(this.getClass().getSimpleName() + " finished");
	}

	public String getDefaultGateway() {
		return def_Gateway;
	}

	private String lookUpDefaultGateway() throws IOException {
		Process result;

		String gateway = "NULL";
		String decidedDNS = "NULL";

		BufferedReader output;

		boolean hasRouteTable = false;

		try {
			System.out.println("Executing traceroute");
			result = Runtime.getRuntime()
					.exec("traceroute -m 1 www.google.com");
			output = new BufferedReader(new InputStreamReader(
					result.getInputStream()));
			while (output.readLine() != null) {
				String tmp = output.readLine();
				System.out.println("OUTPUT: " + tmp);
				String splitData[] = tmp.split("\\s+");
				if (splitData.length > 2) {
					decidedDNS = splitData[2];
					if (!decidedDNS.equalsIgnoreCase("On-link")) {
						System.out.println("Found DNS: " + decidedDNS);
						break;
					}
				}
			}
			this.stringData = decidedDNS;
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
						String splitData[] = tmp.split("\\s+");
						decidedDNS = "NULL";
						if (splitData.length > 3) {
							decidedDNS = splitData[3];
							if (!decidedDNS.equalsIgnoreCase("On-link")) {
								System.out.println("Found DNS: " + decidedDNS);
								break;
							}
						}
					}
				}
				this.stringData = decidedDNS;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return gateway;
	}

	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out;
		
		try {
			out = new ObjectOutputStream(os);
			out.writeUTF(getTaskId());
			out.writeUTF(getStringData());
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final byte[] res = os.toByteArray();

		return res;
	}

	@Override
	public Task fromBytes(final byte[] byteArray) {
		FindDefaultGatewayTask task = new FindDefaultGatewayTask();
		ByteArrayInputStream is;
		ObjectInputStream in;
		
		String tmpTaskId;
		String tmpStringData;
		
		try {
			is = new ByteArrayInputStream(byteArray);
			in = new ObjectInputStream(is);

			tmpTaskId = in.readUTF();
			tmpStringData = in.readUTF();
			
			task.setTaskId(tmpTaskId);
			task.setStringData(tmpStringData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return task;
	}
}
