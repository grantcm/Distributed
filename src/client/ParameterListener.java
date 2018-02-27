package client;

import assignments.util.inputParameters.SimulationParametersListener;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;

public class ParameterListener implements SimulationParametersListener {

	private Client client;
	
	public ParameterListener (Client client) {
		this.client = client;
	}
	
	@Override
	public void atomicBroadcast(boolean newValue) {
		System.out.println("atomicBroadcast " + newValue);
		client.setAtomic(newValue);
	}

	@Override
	public void ipcMechanism(IPCMechanism newValue) {
		System.out.println("ipcMechanism " + newValue);
		client.setIPC(newValue);
	}

	@Override
	public void experimentInput() {
		System.out.println("experimentInput");
		client.runExperiment();
	}

	@Override
	public void localProcessingOnly(boolean newValue) {
		System.out.println("localProcessingOnly " + newValue);
		client.setLocal(newValue);
	}

	@Override
	public void waitForBroadcastConsensus(boolean newValue) {
		System.out.println("waitForBroadcastConsensus " + newValue);

	}

	@Override
	public void waitForIPCMechanismConsensus(boolean newValue) {
		System.out.println("waitForIPCMechanismConsensus " + newValue);
	}

	@Override
	public void consensusAlgorithm(ConsensusAlgorithm newValue) {
		System.out.println("consensusAlgorithm " + newValue);
	}

	@Override
	public void quit(int aCode) {
		System.out.println("Quitting with code " + aCode);
		System.exit(aCode);
	}

	@Override
	public void simulationCommand(String aCommand) {
		System.out.println("Simulation command: " + aCommand);
		client.setInputString(aCommand);
	}

	@Override
	public void broadcastMetaState(boolean newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delaySends(int aMillisecondDelay) {
		// TODO Auto-generated method stub		
	}

}
