package client;
import inputport.nio.manager.listeners.SocketChannelConnectListener;

public interface Client extends SocketChannelConnectListener {
	public void connectToServer(String aServerHost, int aServerPort);
	public void initialize(String aServerHost, int aServerPort);
	public void setLocal(boolean local);
	public void setAtomic(boolean atomic);
	public void setInputString(String input);
	public void setMode(BroadcastMode mode);
	public BroadcastMode getMode();
	public void runExperiment();
}
