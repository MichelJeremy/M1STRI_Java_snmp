import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class GestionManager extends UnicastRemoteObject implements RMI_Int_Manager{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String[] agentsArray;

	private static RMI_Int_Agent[] soucheAgents;
	
	//Hashtable to store Agent's active ports // Enumeration to parse the HashTable
	private static Hashtable<Integer, String> activePorts = new Hashtable<Integer, String>();
	private static Enumeration<Integer> activePortsEnum;

	//Priority of the trap that should be seen
	private static Hashtable<Integer, Integer> priorityAgent;
	
	public static String[] getAgentsArray() {
		return agentsArray;
	}

	public static int setSoucheAgent(String agent) {
		StringBuilder lookup = new StringBuilder().append("rmi://localhost/").append(agent).append("_connection");
		//Create the Thread to manage the RMI connections.
		try {
			Naming.lookup(lookup.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// May happen at the beginning of the execution of the program
			return 0;
		}
		return 1;
	}
	
	public GestionManager() throws RemoteException{}

	public GestionManager(String[] _agentsArray) throws RemoteException, MalformedURLException {
		
		int i = 0;
		
		soucheAgents = new RMI_Int_Agent[_agentsArray.length];
		priorityAgent = new Hashtable<Integer, Integer>();
		
		
		agentsArray = new String[_agentsArray.length];
		for(i = 0; i < agentsArray.length; i++){
			agentsArray[i] = _agentsArray[i];
			priorityAgent.put(i, 0);
		}
		
		
		//RMI connection to the Thread of the agents with specific names
		for(i = 0; i < agentsArray.length; i++){
			StringBuilder bind = new StringBuilder().append("Manager_connection_").append(agentsArray[i]);
			try {
				Naming.bind(bind.toString(), new GestionManager());
			} catch (AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public static void menu() throws RemoteException{
		
		String[] MibDetail;
		int i;
		int retourSet;
		Scanner reader = new Scanner(System.in);
		RMI_Int_Agent souche = null;
		boolean quitmenu = false;
		
		while(true){
			int choice = 0;
			
			System.out.println("Welcome! Please choose an agent to manage");
			
			//Allows an infinite number of agents
			for(i = 0; i < agentsArray.length; i++){
					System.out.println((i+1) + ": " + agentsArray[i]);
			}
			choice = reader.nextInt();
			
			//Choice will have the value + 1
			if(choice > 0 && choice <= agentsArray.length){
				souche = soucheAgents[choice-1];
				
				quitmenu = false;
				
				while (!quitmenu) {
					
					// interface variables, initiated here to make sure they reset with each iterations
					String OID = null;
					String agentName = null;
					String ID = null;
					String password = null;
					String valuesWithCommas = null;
					String[] values = null;
					StringBuilder output = new StringBuilder();
					int choice2;
					i=0;
					
					// Interface.
					System.out.println("Welcome. Choose from the menu below (1-5):\n");
					System.out.println("1: subscribe");
					System.out.println("2: snmpget");
					System.out.println("3: snmpget-next");
					System.out.println("4: snmpset");
					System.out.println("5: quit");
					
					//choices
					System.out.println("Your choice: ");	
					choice2 = reader.nextInt();
					switch (choice2) {
					// case 1: Subscription	
						case 1: System.out.println("Enter the priority of traps you want to subscribe (1-3): ");
								priorityAgent.replace(choice-1, reader.nextInt());
								System.out.println(output);
								break;
						
						// case 2: get
						case 2:	System.out.println("Enter the OID: ");
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
								break;
						// case 3: get-next
						case 3: System.out.println("Enter the OID: ");
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
								break;
						//case 4: set
						case 4: System.out.println("Enter the OID: ");
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
								break;
						// case 5: Exit		
						case 5: System.out.println("Back to the Main Menu \n");
								quitmenu = true;
								break;
						default: System.out.println("We did not understand your choice. Please try again and make sure your choice is correct.");
					}
				}
				
			}
		}
	}
	
	public void trap(String[] message){
		int i=0;
		StringBuilder output = new StringBuilder();
		String agent;
		boolean found = false;
		
		agent = message[0];
		while(i < agentsArray.length && !found){
			if(agentsArray[i].equals(agent))
				found = true;
				
			i++;
		}
		
		//If the manager has subscribe to the trap of the agent then print it
		if(priorityAgent.get(i-1) == Integer.parseInt(message[message.length-1])){
			System.out.print("Trap: " + message[0] + " : " + message[1] + " est maintenant ");
			if(Integer.parseInt(message[message.length-2]) == 0){
				System.out.println("inactif");
			}
			else{
				System.out.println("actif");
			}
		}
	}
}
