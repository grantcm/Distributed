package client;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.mainArgs.ClientArgsProcessor;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AReadingWritingConnectCommandFactory;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import main.BeauAndersonFinalProject;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.port.PerformanceExperimentEnded;
import util.trace.port.PerformanceExperimentStarted;
import util.trace.port.nio.NIOTraceUtility;

@Tags({ DistributedTags.CLIENT })

public class Assignment1Client implements Client {
	private static final int COMMAND_QUEUE_SIZE = 1000;
	private static final int EXPERIMENT_TRIALS = 1000;
	
	
	private HalloweenCommandProcessor commandProcessor;
	private CommandClientSender clientSender;
	private CommandClientReceiver clientReceiver;
	private ArrayBlockingQueue<ClientCommandObject> commandQueue;
	private ClientCommandRunnable commandRunnable;
	private Thread commandExecutor;
	private BroadcastMode mode;
	private SimulationConsoleListener consoleListener;
	String clientName;
	SocketChannel socketChannel;
	
	public static final String READ_THREAD_NAME = "Read Thread";

	public Assignment1Client(String name) {
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
		socketChannel = createSocketChannel();
		commandQueue = new ArrayBlockingQueue<ClientCommandObject>(COMMAND_QUEUE_SIZE);
		createCommunicationObjects();
		addListeners();
		connectToServer(aServerHost, aServerPort);
		commandExecutor.start();
		consoleListener = new SimulationConsoleListener();
		consoleListener.addSimulationParameterListener(new ParameterListener(this));
		consoleListener.processCommands();
	}

	/**
	 * Default to atomic simulation mode
	 **/
	protected void createSimulation() {
		commandProcessor = BeauAndersonFinalProject.createSimulation(clientName, 0, 0, 1200, 765, 100, 100);
		commandProcessor.setConnectedToSimulation(false);
		mode = BroadcastMode.atomic;
	}

	@Override
	public void connected(SocketChannel theSocketChannel) {
		System.out.println("Ready to send messages to server");
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
		clientSender = new ACommandClientSender(socketChannel, clientName, mode);
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
		connectToSocketChannel(aServerHost, aServerPort);
	}

	protected void connectToSocketChannel(String aServerHost, int aServerPort) {
		try {
			InetAddress aServerAddress = InetAddress.getByName(aServerHost);
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
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		Client aClient = new Assignment1Client(aClientName);
		aClient.initialize(aServerHost, aServerPort);
	}

	public static void main(String[] args) {
		launchClient(ClientArgsProcessor.getServerHost(args), ClientArgsProcessor.getServerPort(args),
				ClientArgsProcessor.getClientName(args));

	}

	@Override
	public void setLocal(boolean local) {
		this.mode = local == true ? BroadcastMode.local : BroadcastMode.atomic;
		clientSender.setBroadcastMode(mode);
		commandProcessor.setConnectedToSimulation(true);
	}

	@Override
	public void setAtomic(boolean atomic) {
		this.mode = atomic == true ? BroadcastMode.atomic : BroadcastMode.nonatomic;
		clientSender.setBroadcastMode(mode);
		if (atomic) {
			commandProcessor.setConnectedToSimulation(false);
		} else {
			commandProcessor.setConnectedToSimulation(true);
		}
	}
	
	@Override
	public void setInputString(String input) {
		commandProcessor.setInputString(input);
	}
	
	public void setMode(BroadcastMode mode) {
		this.mode = mode;
	}
	
	public BroadcastMode getMode() {
		return this.mode;
	}
	
	@Override
	public void runExperiment(){
		long end, duration;
		long start = System.nanoTime();
		String command = "move 1 1";

		PerformanceExperimentStarted.newCase(this, start, EXPERIMENT_TRIALS);
		
		for (int i = 0; i < EXPERIMENT_TRIALS; i++) {
			commandProcessor.setInputString(command);
		}
		
		end = System.nanoTime();
		duration = end-start;
		PerformanceExperimentEnded.newCase(this, start, end, duration, EXPERIMENT_TRIALS);
		System.out.println("Experiment took: " + (double)(duration/1000000000.0));
	}
}
