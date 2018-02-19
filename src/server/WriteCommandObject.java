package server;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import assignments.util.MiscAssignmentUtils;
import inputport.nio.manager.NIOManagerFactory;

public class WriteCommandObject {
	
	private final int ATOMIC = 49;
	
	SocketChannel source;
	List<SocketChannel> clients;
	ByteBuffer buffer;
	boolean broadcastMode;
	
	public WriteCommandObject(SocketChannel source, ByteBuffer buffer, List<SocketChannel> clients) {
		this.source = source;
		//Copy reference so that when new clients are added we don't broadcast an old command to them
		this.clients = new ArrayList<SocketChannel>(clients);
		this.buffer = buffer;
		//Get broadcastMode from client		
		this.broadcastMode = buffer.get(0) == ATOMIC ? true : false;
	}
	
	public void execute(){
		//
		for (SocketChannel channel: clients) {
			//DEBUG
			System.out.println("Writing "+buffer.toString()+" to " + channel.toString());

			if (!broadcastMode && channel.equals(source)) {
				//Skip the sender if we are in nonatomic broadcast
				continue;
			}
			
			NIOManagerFactory.getSingleton().write(channel, MiscAssignmentUtils.deepDuplicate(buffer));//, writeListener);
			//Getting error on the notification of my listener
			//writeListener.wait();
		}
	}
	
}
