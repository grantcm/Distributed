package client;
import assignments.util.inputParameters.SimulationParametersBean;
import inputport.nio.manager.listeners.SocketChannelConnectListener;
import util.interactiveMethodInvocation.IPCMechanism;

public interface Client extends SocketChannelConnectListener, SimulationParametersBean {
	public void connectToServer(String aServerHost, int aServerPort);
	public void initialize(String aServerHost, int aServerPort);
	public void setInputString(String input);
	public void setIPC(IPCMechanism newValue);
	public void setLocal(boolean local);
	public void setAtomic(boolean atomic);
	public boolean getAtomic();
	public boolean getLocal();
	public IPCMechanism getIPC();
	public String getName();
	public void executeCommand(String command);
	public void runExperiment();
	public boolean isWaitForConsensusAlgorithm();
}
