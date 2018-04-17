package client;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;

import inputport.nio.manager.NIOManagerFactory;
import server.GIPCProposal;
import server.RMICommandIntf;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.RemoteProposeRequestSent;

public class ACommandClientSender implements CommandClientSender {
	private final int atomic = 1;
	private final int nonatomic = 0;
	SocketChannel socketChannel;
	RMICommandIntf command;
	GIPCProposal proposal;
	private Client client;
	public ACommandClientSender(SocketChannel aSocketChannel, RMICommandIntf command, GIPCProposal proposal, Client client) {
		socketChannel = aSocketChannel;	
		this.command = command;
		this.client = client;
		this.proposal = proposal;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("InputString")) {
			if (!client.getLocal()) {
				if (client.getIPC() == IPCMechanism.GIPC) {
					//GIPC
					try {
						proposal.proposeCommand((String) evt.getNewValue());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (client.getIPC() == IPCMechanism.RMI) {
					//RMI
					try {
						String message = (String) evt.getNewValue();
						if(!client.getAtomic())
							client.executeCommand(message);
						command.sendCommand(client.getName(), message, client.getAtomic());
						RemoteProposeRequestSent.newCase(this, "Command", 1, message);

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
