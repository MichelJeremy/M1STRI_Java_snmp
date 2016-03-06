import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class Manager extends Object{

	private int hierarchy; // int to know which priority this manager can manage
	private Hashtable<String, String> hashAgents = new Hashtable<String, String>(); // hash managing agent list (contact list, name -> address)
	
	//Hashtable to store Agent's active ports // Enumeration to parse the HashTable
	private static Hashtable<Integer, String> activePorts = new Hashtable<Integer, String>();
	private static Enumeration<Integer> activePortsEnum;
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
		int currentPort;
		String[] MibDetail;
		int i =0;
		int retourSet;
		Scanner reader;
	
		
		RMI_Int_Agent souche=(RMI_Int_Agent) Naming.lookup("rmi://localhost/Agent_connection");
		
		//Not sure where to put it
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("Registry initiated");
		} catch (Exception e) {
			System.out.println("Registry already bound");
			System.exit(0);
		}
		
		//Problem with the extends of the class
		Naming.bind("Manager_connection", new Manager());
		
		
		//TEST of getting the Active Ports
		activePorts = new Hashtable<Integer, String>(souche.getActivesPorts());
		activePortsEnum = activePorts.keys();
		
		while(activePortsEnum.hasMoreElements()){
			//Enumeration has only the nextElement methods so we need to have a variable to store it for multiple use.
			currentPort = activePortsEnum.nextElement();
			
			System.out.println("Port " + currentPort + " : " + activePorts.get(currentPort));
		}
				
		
		// Interface.
		System.out.println("Welcome. Choose from the menu below (1-4):\n");
		System.out.println("1: snmpget");
		System.out.println("2: snmpget-next");
		System.out.println("3: snmpset");
		System.out.println("4: quit");
		
		reader = new Scanner(System.in);

		while (true) {
			
			// interface variables, initiated here to make sure they reset with each iterations
			String OID = null;
			String agentName = null;
			String ID = null;
			String password = null;
			String valuesWithCommas = null;
			String[] values = null;
			StringBuilder output = new StringBuilder();
			int choice;
			i=0;
			
			//choices
			System.out.println("Your choice: ");	
			choice = reader.nextInt();
			switch (choice) {
				// case 1: get
				case 1:	System.out.println("Enter the OID: ");
						OID = reader.next();
						MibDetail = souche.getMIBInformation(OID);
						
						while(i < MibDetail.length){
							if (i != MibDetail.length-1) {
								output.append(MibDetail[i]).append(", ");
							} else {
								output.append(MibDetail[i]);
							}
						i++;
						}
						System.out.println(output);
						System.out.println("Welcome back. Choose from the menu below (1-4):\n");
						System.out.println("1: snmpget");
						System.out.println("2: snmpget-next");
						System.out.println("3: snmpset");
						System.out.println("4: quit");
						break;
				// case 2: get-next
				case 2: System.out.println("Enter the OID: ");
						OID = reader.next();
						MibDetail = souche.getNext(OID);
						
						while(i < MibDetail.length){
							if (i != MibDetail.length-1) {
								output.append(MibDetail[i]).append(", ");
							} else {
								output.append(MibDetail[i]);
							}
						i++;
						}
						System.out.println(output);
						System.out.println("Welcome back. Choose from the menu below (1-4):\n");
						System.out.println("1: snmpget");
						System.out.println("2: snmpget-next");
						System.out.println("3: snmpset");
						System.out.println("4: quit");
						break;
				//case 3: set
				case 3: System.out.println("Enter the OID: ");
						OID = reader.next();
						
						System.out.println("Enter the new values you wish to add.\nIf you want to add multiple values, separate them using commas.");
						valuesWithCommas = reader.next();
						values = valuesWithCommas.split(",");
						while (i < values.length) {
							values[i].trim();
							i++;
						}
						
						System.out.println("Enter the name of the agent you wish to contact NOT IMPL YET: ");
						agentName = reader.next();
						
						System.out.println("Enter your ID: ");
						ID = reader.next();
						
						System.out.println("Enter your password: ");
						password = reader.next();
						
						retourSet = souche.setMIBInformation(OID, values, agentName, ID, password);
						switch(retourSet){
							case -1:	System.out.println("Failed to open/remove or edit file");
										break;
							case -2:	System.out.println("Agent does not exist");
										break;
							case -3:	System.out.println("Check your privileges, scrub");
										break;
							default:	System.out.println("MIB successfully modified");
							break;
						}
						
						System.out.println("Welcome back. Choose from the menu below (1-4):\n");
						System.out.println("1: snmpget");
						System.out.println("2: snmpget-next");
						System.out.println("3: snmpset");
						System.out.println("4: quit");
						break;
				// case 4: Exit		
				case 4: System.out.println("Exiting ... \n");
						System.exit(0);
				default: System.out.println("We did not understand your choice. Please try again and make sure your choice is correct.");
			}
			
		}
	}
	
	public void trap(String message){
		System.out.println("Trap: " + message);
	}
}
