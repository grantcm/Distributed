package server;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.port.rpc.rmi.RMIRegistryCreated;

@Tags({DistributedTags.REGISTRY, DistributedTags.RMI})
public class RMIRegistry implements RMIValues{
	public static void main (String[] args) {
		try {
			LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);
			Scanner scanner = new Scanner(System.in);
			RMIRegistryCreated.newCase(scanner, REGISTRY_PORT_NUMBER);
			scanner.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
