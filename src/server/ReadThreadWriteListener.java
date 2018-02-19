package server;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import inputport.nio.manager.listeners.SocketChannelWriteListener;

public class ReadThreadWriteListener implements SocketChannelWriteListener {

	@Override
	public void written(SocketChannel socketChannel, ByteBuffer theWriteBuffer, int sendId) {
		notify();
	}

}
