package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;

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
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.COMMAND, 1, command);
		client.executeCommand(command);
	}

	@Override
	public void changeBroadcastMode(boolean mode) throws RemoteException {
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, 1, mode);
		client.updateAtomic(mode);
	}
}
