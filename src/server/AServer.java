package server;

/**
 * Broadcast server 
 * Uses a separate thread for processing read messages and selecting messages
 * Read listener, executed by selecting thread, enques information using add() since it is non-blocking
 * Reading thread 'takes' information from queue
 * 
 * Using a bounded queue for storing messages
 * Make sure to store a copy of the read queue using deepDuplicate() 
 * from MiscAssignmentUtils in assignments.util in GIPC
 * 
 * Create command processor using state createSimultion() in COMP401 Final project
 * Use processCommand() method to change simulation state
 * Use setConntectedToSimulation() to connect or disconnect to command processor
 * 
 * Also supports a User Command that sends a sequence of 1000 commands using setInputString()
 * and outputs the time taken to execute commands
 * @author Grant
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AReadingAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import port.ATracingConnectionListener;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import assignments.util.mainArgs.ServerArgsProcessor;
import client.ClientCallbackInf;
import client.GIPCClientCallback;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.rmi.RMITraceUtility;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.annotations.Tags;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;

@Tags({ DistributedTags.SERVER, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO })

public class AServer implements Server, RMIValues {

	private static final int COMMAND_QUEUE_SIZE = 10000;
	private ArrayBlockingQueue<WriteCommandObject> commandQueue;
	private ServerSocketChannel serverSocketChannel;
	private CommandServerReceiver commandServerReceiver;
	private ArrayList<SocketChannel> clients;
	private Runnable readCommand;
	private Thread readThread;
	private GIPCRegistry gipcRegistry;

	private Map<String, ClientCallbackInf> RMIClients;
	private Map<String, GIPCClientCallback> GIPCClients;
	
	public static final String READ_THREAD_NAME = "Read Thread";

	public AServer() {

	}

	// Set factories for accepting messages
	protected void setFactories() {
		AcceptCommandFactorySelector.setFactory(new AReadingAcceptCommandFactory());
	}

	/**
	 * Assigns the receiver for reading commands
	 */
	protected void createReceiver() {
		commandServerReceiver = new ACommandServerReceiver(commandQueue, clients);
	}

	
	protected void createCommunicationObjects() {
		commandQueue = new ArrayBlockingQueue<WriteCommandObject>(COMMAND_QUEUE_SIZE);
		clients = new ArrayList<>();
		createReceiver();
		RMIClients = new HashMap<>();
		readCommand = new AReadThread(commandQueue);
		readThread = new Thread(readCommand);
		readThread.setName(READ_THREAD_NAME);
	}

	protected void makeServerConnectable() {
		NIOManagerFactory.getSingleton().enableListenableAccepts(serverSocketChannel, this);
	}

	/**
	 * Adds a listener to the socket to act when commands are received
	 * 
	 * @param aSocketChannel
	 */
	protected void addReadListener(SocketChannel aSocketChannel) {
		NIOManagerFactory.getSingleton().addReadListener(aSocketChannel, commandServerReceiver);
	}

	/**
	 * Method to be run by reading thread that will write the command out to every client
	 * Must also listen for the write to finish so that we know the byte buffer can be used for the next command  
	 */
//	protected void writeToClients() {
//	}

	/**
	 * Adds read listener to the socket channel
	 * 
	 * @param aSocketChannel
	 */
	protected void addListeners(SocketChannel aSocketChannel) {
		addReadListener(aSocketChannel);
	}

	@Override
	public void socketChannelAccepted(ServerSocketChannel aServerSocketChannel, SocketChannel aSocketChannel) {
		addListeners(aSocketChannel);
		clients.add(aSocketChannel);
	}

	/**
	 * Initializes the structures for the server
	 * 
	 * @param serverPort
	 */
	public void initialize(int serverPort) {
		this.setFactories();
		serverSocketChannel = createSocketChannel(serverPort);
		createCommunicationObjects();
		makeServerConnectable();
		readThread.start();
		setupRMIRegistry();
		setupGIPCRegistry();
	}
	
	private void setupRMIRegistry() {
		try {
			Registry rmiRegistry = LocateRegistry.getRegistry(REGISTRY_PORT_NUMBER);
			IAmInterface identity = new IAmCommand(this);
			RMICommandIntf command = new RMICommand(this);
			UnicastRemoteObject.exportObject(identity, 0);
			UnicastRemoteObject.exportObject(command, 0);
			rmiRegistry.rebind(IAM, identity);
			rmiRegistry.rebind(COMMAND, command);
		} catch (Exception e) {
		}
	}
	
	/**
	 * GIPC Section
	 */
	
	private void setupGIPCRegistry() {
		GIPCClients = new HashMap<>();
		gipcRegistry = GIPCLocateRegistry.createRegistry(GIPC_SERVER_PORT);
		GIPCProposal proposal = new AGIPCProposal(this);
		gipcRegistry.rebind(PROPOSAL, proposal);	
		gipcRegistry.getInputPort()
		.addConnectionListener(new ATracingConnectionListener(gipcRegistry.getInputPort()));
	}
	
	@Override
	public void proposeAtomicBroadcast(Boolean newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void proposeIPCMechanism(IPCMechanism newValue) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void proposeConsensusAlgorithm(ConsensusAlgorithm newValue) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void proposeCommand(String command) {
		
	}

	/**
	 * Create socket channel for communication to clients
	 * 
	 * @param serverPort
	 * @return
	 */
	protected ServerSocketChannel createSocketChannel(int serverPort) {
		try {
			ServerSocketChannel retVal = ServerSocketChannel.open();
			InetSocketAddress isa = new InetSocketAddress(serverPort);
			retVal.socket().bind(isa);
			SocketChannelBound.newCase(this, retVal, isa);
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();
		AServer aServer = new AServer();
		args = ServerArgsProcessor.removeEmpty(args);
		aServer.initialize(ServerArgsProcessor.getServerPort(args));
	}

	@Override
	public ArrayList<SocketChannel> getClientList() {
		return clients;
	}

	@Override
	public boolean addRMIClient(String name, ClientCallbackInf callback) {
		if (RMIClients.containsKey(name)) {
			return false;
		} else {
			RMIClients.put(name, callback);
			return true;
		}
	}
	
	@Override
	public boolean addGIPCClient(String name, GIPCClientCallback callback) {
		if (GIPCClients.containsKey(name)) {
			return false;
		} else {
			GIPCClients.put(name, callback);
			return true;
		}
	}

	@Override
	public Map<String, ClientCallbackInf> getRMIClients() {
		return RMIClients;
	}

}
