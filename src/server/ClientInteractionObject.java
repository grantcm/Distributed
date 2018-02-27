package server;

import stringProcessors.HalloweenCommandProcessor;
import main.BeauAndersonFinalProject;

public class ClientInteractionObject implements RMIClientInterface {
	
	HalloweenCommandProcessor commandProcessor;
	
	public ClientInteractionObject(String name) {
		commandProcessor = BeauAndersonFinalProject.createSimulation(name, 0, 0, 1200, 765, 100, 100);
	}
	
	public String executeCommand(String input) {
		return input;
	}
}
