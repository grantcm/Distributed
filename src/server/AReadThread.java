package server;
import java.util.concurrent.ArrayBlockingQueue;

import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.port.nio.NIOTraceUtility;

public class AReadThread implements Runnable {

	private ArrayBlockingQueue<WriteCommandObject> aCommandQueue;
	private ReadThreadWriteListener writeListener;

	public AReadThread(ArrayBlockingQueue<WriteCommandObject> commandQueue) {
		aCommandQueue = commandQueue;
		writeListener = new ReadThreadWriteListener();
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
	}

	@Override
	public void run() {
		while (true) {
			synchronized (aCommandQueue) {
				while (aCommandQueue.isEmpty()) {
					try {
						aCommandQueue.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			
			WriteCommandObject command;
			try {
				synchronized (writeListener) {
					command = aCommandQueue.take();
					command.execute();
				}
			} catch (InterruptedException e) {

			}
		}
	}
}
