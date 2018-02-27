package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMICommandIntf extends Remote {
	public void sendCommand(String name, String command, boolean atomic) throws RemoteException;
}
