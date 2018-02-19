package server;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import assignments.util.MiscAssignmentUtils;

public class ACommandServerReceiver implements CommandServerReceiver {

	private ArrayBlockingQueue<WriteCommandObject> aCommandQueue;
	private static final int COMMAND_QUEUE_SIZE = 10000;
	private ArrayList<SocketChannel> clients;

	public ACommandServerReceiver(ArrayBlockingQueue<WriteCommandObject> commandQueue,
			ArrayList<SocketChannel> clients) {
		this.aCommandQueue = commandQueue;
		this.clients = clients;
	}

	@Override
	public void socketChannelRead(SocketChannel aSocketChannel, ByteBuffer aMessage, int aLength) {
		if (aCommandQueue.size() == COMMAND_QUEUE_SIZE) {
			// TODO: Throw error
			System.out.println("Error - QUEUE FULL");
		} else {
			WriteCommandObject command = new WriteCommandObject(aSocketChannel,
					MiscAssignmentUtils.deepDuplicate(aMessage), clients);
			aCommandQueue.add(command);

			// TODO:only notify if the queue was empty before adding
			synchronized (aCommandQueue) {
				aCommandQueue.notify();
			}

		}
	}
}
