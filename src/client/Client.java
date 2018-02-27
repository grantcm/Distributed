package client;
import inputport.nio.manager.listeners.SocketChannelConnectListener;
import util.interactiveMethodInvocation.IPCMechanism;

public interface Client extends SocketChannelConnectListener {
	public void connectToServer(String aServerHost, int aServerPort);
	public void initialize(String aServerHost, int aServerPort);
	public void setLocal(boolean local);
	public void setAtomic(boolean atomic);
	public void setInputString(String input);
	public void setMode(BroadcastMode newValue);
	public boolean getAtomic();
	public void setIPC(IPCMechanism newValue);
	public IPCMechanism getIPC();
	public String getName();
	public void executeCommand(String command);
	public BroadcastMode getMode();
	public void runExperiment();
}
