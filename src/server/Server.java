package server;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import assignments.util.mainArgs.ServerPort;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;

public interface Server extends ServerPort, SocketChannelAcceptListener{
	public void initialize(int serverPort);
	public ArrayList<SocketChannel> getClientList();
}
