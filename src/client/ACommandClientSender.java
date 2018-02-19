package client;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import inputport.nio.manager.NIOManagerFactory;

public class ACommandClientSender implements CommandClientSender {
	private final int atomic = 1;
	private final int nonatomic = 0;
	SocketChannel socketChannel;
	String clientName;
	BroadcastMode broadcastMode;
	public ACommandClientSender(SocketChannel aSocketChannel, String aClientName, BroadcastMode mode) {
		socketChannel = aSocketChannel;	
		clientName = aClientName;
		broadcastMode = mode;
	}
	
	public void setBroadcastMode(BroadcastMode mode) {
		this.broadcastMode = mode;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("InputString")) {
			if (broadcastMode == BroadcastMode.atomic) {
				ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap((Integer.toString(atomic) + evt.getNewValue()).getBytes());
				NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
			} else if (broadcastMode == BroadcastMode.nonatomic) {
				ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap((Integer.toString(nonatomic) + evt.getNewValue()).getBytes());
				NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
			}
		}		
	}

}
