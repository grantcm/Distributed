package client;

import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;

public class GIPCClientCallback {

	Client client;
	private Boolean oldAtomicValue;
	private IPCMechanism oldIPCValue;
	private ConsensusAlgorithm oldAlgValue;
	
	public GIPCClientCallback (Client client){
		this.client = client;
	}
	
	public boolean proposeAtomicBroadcast(Boolean newValue) {
		if (client.isAtomicBroadcast() == null && !client.isWaitForBroadcastConsensus()) {
			return false;
		} else if (!client.isWaitForBroadcastConsensus()) {
			client.setWaitForBroadcastConsensus(true);
			oldAtomicValue = client.isAtomicBroadcast();
			client.setAtomicBroadcast(null);
			return true;
		} else {
			if (newValue != null) {
				client.setAtomicBroadcast(newValue);
			} else {
				client.setAtomicBroadcast(oldAtomicValue);
			}
			client.setWaitForBroadcastConsensus(false);
			return true;
		}
	}

	public boolean proposeIPCMechanism(IPCMechanism newValue) {
		if (client.getIPC() == null && !client.isWaitForBroadcastConsensus()) {
			return false;
		} else if (!client.isWaitForIPCMechanismConsensus()) {
			client.setWaitForIPCMechanismConsensus(true);
			oldIPCValue = client.getIPC();
			client.setIPC(null);
		} else {
			if (newValue != null) {
				client.setIPC(newValue);
			} else {
				client.setIPC(oldIPCValue);
			}
			client.setWaitForIPCMechanismConsensus(false);
		}
		return true;
	}
	
	public boolean proposeConsensusAlgorithm(ConsensusAlgorithm newValue) {
		if (client.getConsensusAlgorithm() == null && !client.isWaitForConsensusAlgorithm()) {
			return false;
		} else if (!client.isWaitForConsensusAlgorithm()) {
			oldAlgValue = client.getConsensusAlgorithm();
			client.setConsensusAlgorithm(null);
		} else {
			if (newValue != null) {
				client.setConsensusAlgorithm(newValue);
			} else {
				client.setConsensusAlgorithm(oldAlgValue);
			}
		}
		return true;
		
	}
	
	
//	public boolean proposeBroadcastBroadcastMode(boolean broadcastBroadcastMode) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//

//
//	public boolean proposeBroadcastMetaState(boolean broadcastMetaState) {
//		// TODO Auto-generated method stub
//		return true;
//		
//	}
//
//	public boolean proposeDelay(int delay) {
//		// TODO Auto-generated method stub
//		return true;
//		
//	}
}
