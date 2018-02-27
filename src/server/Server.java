package server;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Map;

import assignments.util.mainArgs.ServerPort;
import client.ClientCallbackInf;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;

public interface Server extends ServerPort, SocketChannelAcceptListener{
	public void initialize(int serverPort);
	public ArrayList<SocketChannel> getClientList();
	public boolean addRMIClient(String name, ClientCallbackInf callback);
	public Map<String, ClientCallbackInf> getRMIClients();
}
