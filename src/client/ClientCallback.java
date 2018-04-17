package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import util.trace.port.consensus.ProposalLearnedNotificationReceived;

public class ClientCallback  extends UnicastRemoteObject implements ClientCallbackInf {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Client client;
	
	public ClientCallback (Client client) throws RemoteException {
		this.client = client;
	}
	
	@Override
	public void executeCommand(String command) throws RemoteException {
		ProposalLearnedNotificationReceived.newCase(this, "Command", 1, command);
		client.executeCommand(command);
	}

	@Override
	public void changeBroadcastMode(boolean mode) throws RemoteException {
		ProposalLearnedNotificationReceived.newCase(this, "Atomic", 1, mode);
		client.updateAtomic(mode);
	}

}
