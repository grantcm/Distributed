package main;

import assignments.util.mainArgs.ClientArgsProcessor;
import client.Assignment1Client;
import util.trace.port.nio.NIOTraceUtility;

public class Client2Launcher {
	private static final String clientName = "client";
	public static void main(String[] args) {
		NIOTraceUtility.setTracing();
		Assignment1Client.launchClient(ClientArgsProcessor.getServerHost(args)
				, ClientArgsProcessor.getServerPort(args),
				clientName + String.valueOf(2));

	}
}
