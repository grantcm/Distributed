package server;

import client.ClientCallbackInf;

/**
 * 
 * @author Grant
 * Allows clients to communicate to an RMI server who they are when they connect
 */

public class IAmCommand implements IAmInterface {
	Server server;
	
	public IAmCommand(Server server) {
		this.server = server;
	}
	
	public boolean IAm(String name, ClientCallbackInf callback) {
		return server.addRMIClient(name, callback);
	}
	
}
