package client;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ClientArgsProcessor;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AReadingWritingConnectCommandFactory;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.BeauAndersonFinalProject;
import server.GIPCProposal;
import server.IAmInterface;
import server.RMICommandIntf;
import server.RMIValues;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.port.PerformanceExperimentEnded;
import util.trace.port.PerformanceExperimentStarted;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

@Tags({ DistributedTags.CLIENT, DistributedTags.RMI, DistributedTags.NIO, DistributedTags.GIPC })

public class AClient extends AnAbstractSimulationParametersBean implements Client, RMIValues {
	private static final int COMMAND_QUEUE_SIZE = 1000;
	private static final int EXPERIMENT_TRIALS = 1000;
	
	
	private HalloweenCommandProcessor commandProcessor;
	private CommandClientSender clientSender;
	private CommandClientReceiver clientReceiver;
	private ArrayBlockingQueue<ClientCommandObject> commandQueue;
	private ClientCommandRunnable commandRunnable;
	private Thread commandExecutor;
	private SimulationConsoleListener consoleListener;
	private ClientCallbackInf rmiCallback;
	private IAmInterface identity;
	private RMICommandIntf command;
	private IPCMechanism ipc = IPCMechanism.GIPC;
	private GIPCClientCallback gipcCallback;
	private GIPCProposal proposal;
	String clientName;
	SocketChannel socketChannel;
	
	public static final String READ_THREAD_NAME = "Read Thread";

	public AClient(String name) {
		clientName = name;
	}
	
	public void quit() {
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void setFactories() {
		ConnectCommandFactorySelector.setFactory(new AReadingWritingConnectCommandFactory());
	}

	public void initialize(String aServerHost, int aServerPort) {
		createSimulation();
		setFactories();
		setupRMI();
		setupGIPC();
		socketChannel = createSocketChannel();
		commandQueue = new ArrayBlockingQueue<ClientCommandObject>(COMMAND_QUEUE_SIZE);
		createCommunicationObjects();
		addListeners();
		connectToServer(RMIValues.HOSTNAME, RMIValues.NIO_PORT_NUMBER);
		commandExecutor.start();
		consoleListener = new SimulationConsoleListener();
		consoleListener.addSimulationParameterListener(new ParameterListener(this));
		//consoleListener.processCommands();
		runTests();
	}
	
	private void runTests() {
		this.localProcessingOnly = true;
		System.out.println("Local only took: "+ runExperiment());
		
		this.ipc = IPCMechanism.NIO;
		runTestsBroadcastMode();
		this.ipc = IPCMechanism.RMI;
		runTestsBroadcastMode();
		this.ipc = IPCMechanism.GIPC;
		runTestsBroadcastMode();
		quit(0);
	}
	
	private void runTestsBroadcastMode() {
		try {
			this.localProcessingOnly = false;
			this.atomicBroadcast = true;
			System.out.println("Atomic true took: " + runExperiment());
			Thread.sleep(5000);
			this.atomicBroadcast = false;
			System.out.println("Atomic false took: " + runExperiment());
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			
		}
	}
	
	protected void setupRMI() {
		try {
			rmiCallback = new ClientCallback(this);
			Registry rmiRegistry = LocateRegistry.getRegistry(RMIValues.HOSTNAME, REGISTRY_PORT_NUMBER);
			identity = (IAmInterface) rmiRegistry.lookup(IAM);
			command = (RMICommandIntf) rmiRegistry.lookup(COMMAND);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setupGIPC() {
		try {
			gipcCallback = new AGIPCClientCallback(this);
			GIPCRegistry gipcRegistry = GIPCLocateRegistry.getRegistry(RMIValues.HOSTNAME, 
															GIPC_SERVER_PORT, this.getName());
			GIPCRegistryLocated.newCase(gipcRegistry, "Server", GIPC_SERVER_PORT, this.getName());
			proposal = (GIPCProposal) gipcRegistry.lookup(GIPCProposal.class, PROPOSAL);
			GIPCObjectLookedUp.newCase(gipcRegistry, proposal, proposal.getClass(), PROPOSAL, gipcRegistry);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Default to atomic simulation mode
	 **/
	protected void createSimulation() {
		commandProcessor = BeauAndersonFinalProject.createSimulation(clientName, 0, 0, 400, 200, 100, 100);
		commandProcessor.setConnectedToSimulation(false);
		this.atomicBroadcast = true;
		this.consensusAlgorithm = ConsensusAlgorithm.CENTRALIZED_SYNCHRONOUS;
	}

	@Override
	public void connected(SocketChannel theSocketChannel) {
		//System.out.println("Ready to send messages to server");
	}

	@Override
	public void notConnected(SocketChannel aSocketChannel, Exception e) {
		System.err.println("Could not connect:" + aSocketChannel);
		if (e != null)
			e.printStackTrace();
	}

	protected void createCommunicationObjects() {
		createSender();
		createReceiver();
		createCommandExecutor();
	}

	/**
	 * Receiver for receiving and executing commands from the server
	 */
	protected void createReceiver() {
		clientReceiver = new ACommandClientReceiver(commandQueue, commandProcessor, this);
	}
	
	protected void createCommandExecutor(){
		commandRunnable = new ClientCommandRunnable(commandQueue);
		commandExecutor = new Thread(commandRunnable);
		commandExecutor.setName(READ_THREAD_NAME);
	}

	/**
	 * Class for sending messages
	 */
	protected void createSender() {
		clientSender = new ACommandClientSender(socketChannel, command, proposal, this);
	}

	/**
	 * Add listeners to simulation model
	 */
	protected void addListeners() {
		addModelListener();
		addReadListener();
	}

	/**
	 * Adds a read listener to the NIO Manager to receive messages from the
	 * server
	 */
	protected void addReadListener() {
		NIOManagerFactory.getSingleton().addReadListener(socketChannel, clientReceiver);
	}

	/**
	 * Listens to controller for changes command inputs
	 */
	protected void addModelListener() {
		commandProcessor.addPropertyChangeListener(clientSender);
	}

	@Override
	public void connectToServer(String aServerHost, int aServerPort) {
		connectToSocketChannel(RMIValues.HOSTNAME, aServerPort);
		if (this.getIPC() == IPCMechanism.RMI) {
			try {
				identity.IAm(clientName, rmiCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		try {
			proposal.introduceClient(this.getName(), gipcCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void connectToSocketChannel(String aServerHost, int aServerPort) {
		try {
			InetAddress aServerAddress = InetAddress.getByName(RMIValues.HOSTNAME);
			NIOManagerFactory.getSingleton().connect(socketChannel, aServerAddress, aServerPort, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected SocketChannel createSocketChannel() {
		try {
			SocketChannel retVal = SocketChannel.open();
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Connect the client with the specified name to the specified server.
	 */
	public static void launchClient(String aServerHost, int aServerPort, String aClientName) {
//		FactoryTraceUtility.setTracing();
//		BeanTraceUtility.setTracing();
//		NIOTraceUtility.setTracing();
//		RMITraceUtility.setTracing();
//		GIPCRPCTraceUtility.setTracing();
		Client aClient = new AClient(aClientName);
		aClient.initialize(aServerHost, aServerPort);
	}

	public static void main(String[] args) {
		args = ClientArgsProcessor.removeEmpty(args);
		launchClient(ClientArgsProcessor.getServerHost(args), ClientArgsProcessor.getServerPort(args),
				ClientArgsProcessor.getClientName(args));

	}
	
	@Override
	public void executeCommand(String command) {
		commandProcessor.processCommand(command);
	}

	@Override
	public void setLocal(boolean local) {
		this.localProcessingOnly(local);
		commandProcessor.setConnectedToSimulation(true);
	}

	@Override
	public void setAtomic(boolean atomic) {
		if (ipc == IPCMechanism.NIO || ipc == IPCMechanism.RMI) {
			this.atomicBroadcast(atomic);
			if (atomic) {
				commandProcessor.setConnectedToSimulation(false);
			} else {
				commandProcessor.setConnectedToSimulation(true);
			}
		} else if (ipc == IPCMechanism.GIPC) {
			try {
				proposal.proposeAtomicBroadcast(atomic);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (ipc == IPCMechanism.RMI) {
			try {
				command.sendCommand("mode","", atomic);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getName() {
		return clientName;
	}
	
	@Override
	public void setInputString(String input) {
		commandProcessor.setInputString(input);
	}
	
	@Override
	public double runExperiment(){
		long end, duration;
		long start = System.nanoTime();
		String command = "move 1 0";

		PerformanceExperimentStarted.newCase(this, start, EXPERIMENT_TRIALS);
		
		for (int i = 0; i < EXPERIMENT_TRIALS; i++) {
			commandProcessor.setInputString(command);
		}
		
		end = System.nanoTime();
		duration = end-start;
		PerformanceExperimentEnded.newCase(this, start, end, duration, EXPERIMENT_TRIALS);
		return (double) (duration/1000000000.0);
	}

	@Override
	public IPCMechanism getIPC() {
		return this.ipc;
	}

	@Override
	public void setIPC(IPCMechanism newValue) {
		this.ipc = newValue;
	}

	@Override
	public boolean getAtomic() {
		return this.isAtomicBroadcast();
	}
	
	@Override
	public boolean isWaitForAtomic() {
		return this.atomicBroadcast == null ? true : false;
	}

	@Override
	public boolean getLocal() {
		return this.isLocalProcessingOnly();
	}

	@Override
	public boolean isWaitForConsensusAlgorithm() {
		return this.consensusAlgorithm == null ? true : false;
	}
}
