package client;

import java.rmi.Remote;

import util.interactiveMethodInvocation.IPCMechanism;

public interface GIPCClientCallback extends Remote {
	boolean proposeAtomicBroadcast(Boolean newValue);
	boolean proposeIPCMechanism(IPCMechanism newValue);
	void executeCommand(String command);
}
