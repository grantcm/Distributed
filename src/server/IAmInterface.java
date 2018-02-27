package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.ClientCallbackInf;

public interface IAmInterface extends Remote {
	public boolean IAm(String name, ClientCallbackInf callback) throws RemoteException;
}
