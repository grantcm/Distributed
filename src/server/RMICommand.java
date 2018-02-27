package server;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import client.ClientCallbackInf;

public class RMICommand implements RMICommandIntf {

	Server server;
	
	public RMICommand(Server server) {
		this.server = server;
	}
	
	@Override
	/**
	 * Propagates command to other clients by using their callback objects to set the input string
	 * If the mode is atomic, then it ignores the invoking client
	 */
	public synchronized void sendCommand(String name, String command, boolean atomic) throws RemoteException {
		Map<String, ClientCallbackInf> clients = server.getRMIClients();	
		for (Entry<String, ClientCallbackInf> entry: clients.entrySet()) {
			String client = entry.getKey();
			if (command.equals("mode")) {
				entry.getValue().changeBroadcastMode(atomic);
			} else if (!client.equals(name) || atomic == true) {
				entry.getValue().executeCommand(command);
			}
		}
	}

}
