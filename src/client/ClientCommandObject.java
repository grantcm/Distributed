package client;

import java.nio.ByteBuffer;

import stringProcessors.HalloweenCommandProcessor;

public class ClientCommandObject {

	private HalloweenCommandProcessor commandProcessor;
	private BroadcastMode executionMode;
	private ByteBuffer buffer;

	public ClientCommandObject(HalloweenCommandProcessor aCommandProcessor, ByteBuffer buffer,
			BroadcastMode executionMode) {
		commandProcessor = aCommandProcessor;
		this.buffer = buffer;
		this.executionMode = executionMode;
	}

	public void execute() {

		if (executionMode == BroadcastMode.atomic) {
			commandProcessor.setConnectedToSimulation(true);
		}

		String command = new String(buffer.array(), buffer.position(), buffer.remaining());
		commandProcessor.processCommand(command);

		if (executionMode == BroadcastMode.atomic) {
			commandProcessor.setConnectedToSimulation(false);
		}
	}

}
