package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.GIPCClientCallback;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;

public interface GIPCProposal extends Remote {
	void proposeAtomicBroadcast(Boolean newValue) throws RemoteException;
	void proposeIPCMechanism(IPCMechanism newValue) throws RemoteException;
	void proposeConsensusMechanism(ConsensusAlgorithm newValue) throws RemoteException;
	void proposeCommand(String newCommand) throws RemoteException;
	boolean introduceClient(String clientName, GIPCClientCallback callback) throws RemoteException;
}
