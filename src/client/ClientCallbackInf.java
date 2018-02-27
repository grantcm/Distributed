package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInf extends Remote {
	public void executeCommand(String command) throws RemoteException;
	public void changeBroadcastMode(boolean mode) throws RemoteException;
}
