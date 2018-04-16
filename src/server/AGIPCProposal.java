package server;

import client.GIPCClientCallback;
import util.interactiveMethodInvocation.IPCMechanism;

public class AGIPCProposal implements GIPCProposal{

	Server server;
	
	public AGIPCProposal(Server server) {
		this.server = server;
	}
	
	public synchronized boolean introduceClient(String clientName, GIPCClientCallback callback){
		if(server.addGIPCClient(clientName, callback))
				return true;
		return false;
	}
	
	public synchronized void proposeCommand(String newValue) {
		server.proposeCommand(newValue);
	}
	
	@Override
	public synchronized void proposeAtomicBroadcast(Boolean newValue) {
		server.proposeAtomicBroadcast(newValue);
	}

	@Override
	public synchronized void proposeIPCMechanism(IPCMechanism newValue) {
		server.proposeIPCMechanism(newValue);
	}
}
