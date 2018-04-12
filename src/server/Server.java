package server;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Map;

import assignments.util.mainArgs.ServerPort;
import client.ClientCallbackInf;
import client.GIPCClientCallback;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;

public interface Server extends ServerPort, SocketChannelAcceptListener{
	public void initialize(int serverPort);
	public ArrayList<SocketChannel> getClientList();
	public boolean addRMIClient(String name, ClientCallbackInf callback);
	public Map<String, ClientCallbackInf> getRMIClients();
	void proposeAtomicBroadcast(Boolean newValue);
	void proposeIPCMechanism(IPCMechanism newValue);
	void proposeConsensusAlgorithm(ConsensusAlgorithm newValue);
	void proposeCommand(String newCommand);
	boolean addGIPCClient(String name, GIPCClientCallback callback);
}
