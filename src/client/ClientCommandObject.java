package client;

import java.nio.ByteBuffer;

import stringProcessors.HalloweenCommandProcessor;

public class ClientCommandObject {

	private HalloweenCommandProcessor commandProcessor;
	private boolean atomic;
	private ByteBuffer buffer;

	public ClientCommandObject(HalloweenCommandProcessor aCommandProcessor, ByteBuffer buffer,
			boolean atomic) {
		commandProcessor = aCommandProcessor;
		this.buffer = buffer;
		this.atomic = atomic;
	}

	public void execute() {

		if (atomic) {
			commandProcessor.setConnectedToSimulation(true);
		}

		String command = new String(buffer.array(), buffer.position(), buffer.remaining());
		commandProcessor.processCommand(command);

		if (atomic) {
			commandProcessor.setConnectedToSimulation(false);
		}
	}

}
