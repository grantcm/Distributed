package client;

import java.util.concurrent.ArrayBlockingQueue;

public class ClientCommandRunnable implements Runnable {
	
	private ArrayBlockingQueue<ClientCommandObject> commandQueue;
	
	public ClientCommandRunnable(ArrayBlockingQueue<ClientCommandObject> commandQueue) {
		this.commandQueue = commandQueue;
	}
	
	@Override
	public void run() {
		ClientCommandObject command;
		while(true) {
			try {
				command = commandQueue.take();
				command.execute();
			} catch (InterruptedException e) {

			}
		}
	}

}
