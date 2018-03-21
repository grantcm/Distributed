package client;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

import stringProcessors.HalloweenCommandProcessor;

public class ACommandClientReceiver implements CommandClientReceiver {
	private ArrayBlockingQueue<ClientCommandObject> commandQueue;
	private HalloweenCommandProcessor commandProcessor;
	private Client parent;

	public ACommandClientReceiver(ArrayBlockingQueue<ClientCommandObject> commandQueue,
			HalloweenCommandProcessor commandProcessor, Client parent) {
		this.commandQueue = commandQueue;
		this.commandProcessor = commandProcessor;
		this.parent = parent;
	}
	
	@Override
	public void socketChannelRead(SocketChannel aSocketChannel, ByteBuffer aMessage, int aLength) {
		ClientCommandObject command;
		if (aMessage.get() == '0') {
			parent.setAtomic(false);
			command = new ClientCommandObject(commandProcessor, aMessage, parent.getAtomic());
		} else {
			parent.setAtomic(true);
			command = new ClientCommandObject(commandProcessor, aMessage, parent.getAtomic());
		}
		commandQueue.add(command);
	}
}
