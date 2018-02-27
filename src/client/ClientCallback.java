package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
		client.executeCommand(command);
	}

	@Override
	public void changeBroadcastMode(boolean mode) throws RemoteException {
		client.setAtomic(mode);
	}

}
