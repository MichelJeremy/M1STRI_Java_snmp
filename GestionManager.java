import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class GestionManager extends UnicastRemoteObject implements RMI_Int_Manager, RMI_Int_Manager_Top_Bottom, RMI_Int_Manager_Bottom_Top{
	
	private static final long serialVersionUID = 1L;

	private static String name;
	private static String[] agentsArray;
	private static String[] managersArray;
	private static int hierarchy;
	
	//*********************************************
	//------------ Simple manager -----------------
	private static RMI_Int_Agent[] soucheAgents;
	private static RMI_Int_Manager_Bottom_Top soucheBottomTop;
	
	//Priority of the trap that should be seen
	private static int[] priorityAgent;
	
	//Hashtable to store Agent's active ports // Enumeration to parse the HashTable
		private static Hashtable<Integer, String> activePorts = new Hashtable<Integer, String>();
		private static Enumeration<Integer> activePortsEnum;
	
	//*********************************************
	//----------- Manager of manager --------------
	private static RMI_Int_Manager_Top_Bottom[] soucheTopBottom;
	
	//Priority of the trap that the hierarchic Manager should see
	private static int[] priorityAgentForManager;
	
	public static String[] getAgentsArray() {
		return agentsArray;
	}

	public static int getHierarchy() {
		return hierarchy;
	}

	public static String getName() {
		return name;
	}

	public static String[] getManagersArray() {
		return managersArray;
	}

	public static int setSoucheAgent(int number, String agent) {
		StringBuilder lookup = new StringBuilder().append("rmi://localhost/").append(agent).append("_connection");
		//Create the Thread to manage the RMI connections.
		try {
			soucheAgents[number] = (RMI_Int_Agent) Naming.lookup(lookup.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			// May happen at the beginning of the execution of the program
			return 0;
		}
		return 1;
	}
	
	public static int setSoucheTopManager(String manager) {
		StringBuilder lookup = new StringBuilder().append("rmi://localhost/Manager_hierarchy_top_connection_").append(manager);
		//Create the Thread to manage the RMI connections.
		try {
			soucheBottomTop = (RMI_Int_Manager_Bottom_Top) Naming.lookup(lookup.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			// May happen at the beginning of the execution of the program
			return 0;
		}
		return 1;
	}
	
	public static int setSoucheDownManager(int number, String manager) {
		StringBuilder lookup = new StringBuilder().append("rmi://localhost/Manager_hierarchy_bottom_connection_").append(manager);
		//Create the Thread to manage the RMI connections.
		
		try {
			soucheTopBottom[number] = (RMI_Int_Manager_Top_Bottom) Naming.lookup(lookup.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			// May happen at the beginning of the execution of the program
			return 0;
		}
		return 1;
	}
	
	public GestionManager() throws RemoteException{}

	public GestionManager(String _name, String[] _agentsArray, String[] _managersArray, int _hierarchy) throws RemoteException, MalformedURLException {
		
		int i = 0;
		hierarchy = _hierarchy;
		name = _name;
		
		//If hierarchy higher than 1, then this is a Manager of Managers.
		if(hierarchy > 1){
			managersArray = new String[_managersArray.length];
			for(i = 0; i < managersArray.length; i++){
				managersArray[i] = _managersArray[i];
			}
			
			//RMI connection to the Thread of the Managers with specific names
			for(i = 0; i < managersArray.length; i++){
				StringBuilder bind = new StringBuilder().append("Manager_hierarchy_top_connection_").append(managersArray[i]);
				try {
					Naming.bind(bind.toString(), new GestionManager());
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
				}
			}
		}
		//Else it manage Agents
		else{
			soucheAgents = new RMI_Int_Agent[_agentsArray.length];
			
			//Initialize with the right length
			priorityAgent = new int[_agentsArray.length];
			priorityAgentForManager = new int[_agentsArray.length];
			
			agentsArray = new String[_agentsArray.length];
			for(i = 0; i < agentsArray.length; i++){
				agentsArray[i] = _agentsArray[i];
			}
			
			//RMI connection to the Thread of the agents with specific names
			for(i = 0; i < agentsArray.length; i++){
				StringBuilder bind = new StringBuilder().append("Manager_connection_").append(agentsArray[i]);
				try {
					Naming.bind(bind.toString(), new GestionManager());
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
				}
			}
			
			//RMI to connect to the Manager that supervise it
			StringBuilder bind = new StringBuilder().append("Manager_hierarchy_bottom_connection_").append(name);
			try {
				Naming.bind(bind.toString(), new GestionManager());
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void menuAgent() throws RemoteException{
		
		String[] MibDetail;
		int i;
		int retourSet;
		Scanner reader = new Scanner(System.in);
		RMI_Int_Agent souche = null;
		boolean quitMenu = false;
		boolean quitSecondMenu = false;
		
		while(!quitMenu){
			int choice = 0;
			
			System.out.println("Welcome! Please choose an agent to manage");
			
			//Allows an infinite number of agents
			for(i = 0; i < agentsArray.length; i++){
					System.out.println((i+1) + ": " + agentsArray[i]);
			}
			
			System.out.println("0 : Exit");
			
			choice = reader.nextInt();
			
			if(choice == 0){
				quitMenu = true;
			}
			//Choice will have the value + 1
			else if(choice > 0 && choice <= agentsArray.length){
				souche = soucheAgents[choice-1];
				
				quitSecondMenu = false;
				
				while (!quitSecondMenu) {
					
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
					//The hierarchy system start at 1, the lowest to 3, the critical.
					//For exemple, when subscribe to level 2, you will receive the trap of level 2 and 3.
						case 1: System.out.println("Enter the priority of traps you want to subscribe (1-3): ");
								priorityAgent[choice-1] = reader.nextInt();
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
								quitSecondMenu = true;
								break;
						default: System.out.println("We did not understand your choice. Please try again and make sure your choice is correct.");
					}
				}
				
			}
		}
	}
	
	public void trap(String[] message) throws RemoteException{
		int i=0;
		String agent;
		boolean found = false;
		
		agent = message[0];
		while(i < agentsArray.length && !found){
			if(agentsArray[i].equals(agent))
				found = true;
				
			i++;
		}
		
		//If the manager has subscribe to the trap of the agent then print it
		//The hierarchy system start at 1, the lowest to 3, the critical.
		//For exemple, when subscribe to level 2, you will receive the trap of level 2 and 3.
		if(priorityAgent[i-1] <= Integer.parseInt(message[message.length-1])){
			System.out.print("Trap: " + message[0] + " : " + message[1] + " est maintenant ");
			if(Integer.parseInt(message[message.length-2]) == 0){
				System.out.println("inactif");
			}
			else{
				System.out.println("actif");
			}
			
			if(priorityAgentForManager[i-1] <= Integer.parseInt(message[message.length-1])){
				try{
					soucheBottomTop.sendTrap(message);
				}
				catch(Exception e){
					System.out.println(e);
				}
			}
		}
	}
	
	public static void menuManager() throws RemoteException{
		
		boolean exitMenu = false;
		boolean exitSubMenu = false;
		boolean exitSubSubMenu = false;
		Scanner reader = new Scanner(System.in);
		int choice, choice2, choice3;
		String[] agents;
		int[] agentPriority, actualPriority;
		StringBuilder output = new StringBuilder();
		
		while(!exitMenu){
			exitSubMenu = false;
			System.out.println("Welcome. Choose from the menu below (1-3):\n");
			System.out.println("1 : Subscribe");
			System.out.println("2 : Manage Managers");
			System.out.println("3 : Exit");
			
			choice = reader.nextInt();
			System.out.println(output);
			
			switch(choice){
				case 1:
					while(!exitSubMenu){
						exitSubSubMenu = false;
						System.out.println("Please select the Manager you wish to manage subscription upon");
						for(int i = 0; i < managersArray.length; i++){
							System.out.println((i+1) + " : " + managersArray[i]);
						}
						System.out.println("0 : Exit");
						
						choice2 = reader.nextInt();
						System.out.println(output);
						
						if(choice2 == 0){
							exitSubMenu = true;
						}
						else if(choice2 > 0 && choice2 <= managersArray.length){
							agents = new String[soucheTopBottom[choice2-1].getAgents().length];
							agentPriority = new int[agents.length];
							
							while(!exitSubSubMenu){
								//Update the informations about the current status
								actualPriority = new int[agentPriority.length];
							
								for(int i = 0; i < actualPriority.length; i++){
									agents[i] = soucheTopBottom[choice2-1].getAgents()[i];
									agentPriority[i] = soucheTopBottom[choice2-1].getPriority()[i];
									actualPriority[i] = soucheTopBottom[choice2-1].getManagerPriority()[i];
								}
								
								System.out.println("Please select the Agent you wish to set level on");
								System.out.println("   --Agent-- --Manager level-- --Your current level");
								for(int i = 0; i < actualPriority.length; i++){
									System.out.println(i + " : " + agents[i] + "  |  " + agentPriority[i] + "       |             " + actualPriority[i] + "     |" );
								}
								System.out.println("0 : Exit");
								
								choice3 = reader.nextInt();
								System.out.println(output);
								
								if(choice3 == 0){
									exitSubSubMenu = true;
								}
								else if (choice3 > 0 && choice3 <= actualPriority.length){
									do{
										System.out.println("Enter the priority of traps you want to subscribe (1-3): ");
										choice2 = reader.nextInt();
										System.out.println(output);
									}while(choice2 < actualPriority[choice3] || choice3 > 3);	
								}
							}
						}
					}
				break;
				case 2:
					while(!exitSubMenu){
						System.out.println("Please select the Manager you wish to manage subscription upon");
						for(int i = 0; i < managersArray.length; i++){
							System.out.println((i+1) + " : " + managersArray[i]);
						}
						System.out.println("0 : Exit");
						
						choice2 = reader.nextInt();
						System.out.println(output);
						
						if(choice2 == 0){
							exitSubMenu = true;
						}
						else if(choice2 > 0 && choice2 <= managersArray.length){
							soucheTopBottom[choice2-1].menuAgent();
						}
					}
				break;
				case 3:
					exitMenu = true;
				break;
				default:System.out.println("We did not understand your choice. Please try again and make sure your choice is correct.");
			}		
		}
	}

	@Override
	public String[] getAgents() throws RemoteException {
		return agentsArray;
	}

	@Override
	public int[] getPriority() throws RemoteException {
		return priorityAgent;
	}

	@Override
	public void setManagerPriority(int[] managerPriority) throws RemoteException {
		for(int i = 0; i < priorityAgentForManager.length; i++){
			priorityAgentForManager[i] = managerPriority[i];
		}
	}
	
	@Override
	public int[] getManagerPriority() throws RemoteException {
		return priorityAgentForManager;
	}

	@Override
	public void sendTrap(String[] message) throws RemoteException {
		System.out.print("Trap: " + message[0] + " : " + message[1] + " est maintenant ");
		if(Integer.parseInt(message[message.length-2]) == 0){
			System.out.println("inactif");
		}
		else{
			System.out.println("actif");
		}
	}
	
}
