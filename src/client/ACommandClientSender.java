package client;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;

import inputport.nio.manager.NIOManagerFactory;
import server.RMICommandIntf;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.bean.NotifiedPropertyChangeEvent;
import util.trace.port.consensus.RemoteProposeRequestSent;

public class ACommandClientSender implements CommandClientSender {
	private final int atomic = 1;
	private final int nonatomic = 0;
	SocketChannel socketChannel;
	RMICommandIntf command;
	private Client client;
	public ACommandClientSender(SocketChannel aSocketChannel, RMICommandIntf command, Client client) {
		socketChannel = aSocketChannel;	
		this.command = command;
		this.client = client;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("InputString")) {
			//NotifiedPropertyChangeEvent.newCase(this, evt, this);
			if (!client.getLocal()) {
				if (client.getIPC() == IPCMechanism.RMI) {
					//RMI
					try {
						String message = (String) evt.getNewValue();
						if(!client.getAtomic())
							client.executeCommand(message);
						RemoteProposeRequestSent.newCase(client.getName(), message, (float) 1, command);
						command.sendCommand(client.getName(), message, client.getAtomic());

					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else {
					//NIO
					if (client.getAtomic()) {
						ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap((Integer.toString(atomic) + evt.getNewValue()).getBytes());
						NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
					} else {
						ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap((Integer.toString(nonatomic) + evt.getNewValue()).getBytes());
						NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
					}
				}	
			}	
		}		
	}
}
