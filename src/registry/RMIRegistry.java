package registry;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import server.RMIValues;
import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.REGISTRY})

public class RMIRegistry implements RMIValues{
	public static void main (String[] args) {
		try {
			LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
