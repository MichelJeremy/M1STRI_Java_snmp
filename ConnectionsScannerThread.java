import java.rmi.Naming;

public class ConnectionsScannerThread extends Thread{

	private GestionManager g;
	private String[] agentsArray;
	private String[] managerArray;
	
	public ConnectionsScannerThread(GestionManager gestion) {
		g = gestion;
	}
	
	public void run() {
		
		int hierarchy = g.getHierarchy();
		
		//If its a regular Manager
		if(hierarchy == 1){
			
			agentsArray = g.getAgentsArray().clone();
			//Fill the Agent Array
			/*
			for(int i = 0; i < g.getAgentsArray().length; i++){
				agentsArray[i] = g.getAgentsArray()[i];
			}
			*/
			
			//We need to know the number of agents
			int numberOfAgents = countArray(agentsArray);
			
			int[] isAgentConnected = new int[numberOfAgents];
			int isManagerConnected = 0;
			boolean everyConnectionsAgentOk = false;
			boolean connectionsManagerOk = false;
			
			//Try to connect to the two agents until it is done
			//V2: Try to connect to the Hierarchical Manager
			while (!everyConnectionsAgentOk && !connectionsManagerOk){
				everyConnectionsAgentOk = true;
				connectionsManagerOk = true;
				
				if(!everyConnectionsAgentOk){
					for(int i = 0; i < numberOfAgents; i++){
						//We use 0 as default because we do not know the length of the Array and because 0 is automatically set when the initialisation is done, it is easier to do so.
						if(isAgentConnected[i] != 1){
							isAgentConnected[i] = g.setSoucheAgent(i, agentsArray[i]);
							everyConnectionsAgentOk = false;
						}
					}
				}
				
				if(!connectionsManagerOk){
					//Try to connect to its supervisor
					if(isManagerConnected != 1){
						isManagerConnected = GestionManager.setSoucheTopManager(g.getName());
					}
				}
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//If it is a Manager of Managers
		else{
			managerArray = g.getManagersArray().clone();
			/*
			for(int i = 0; i < g.getManagersArray().length; i++){
				managerArray[i] = g.getManagersArray()[i];
			}
			*/
			//We need to know the number of managers
			int numberOfManagers = countArray(managerArray);
			
			int[] isManagerConnected = new int[numberOfManagers];
			boolean everyConnectionsOk = false;
			
			//Try to connect to the two managers until it is done
			while (!everyConnectionsOk){
				everyConnectionsOk = true;
				
				for(int i = 0; i < numberOfManagers; i++){
					//We use 0 as default because we do not know the length of the Array and because 0 is automatically set when the initialisation is done, it is easier to do so.
					if(isManagerConnected[i] != 1){
						isManagerConnected[i] = g.setSoucheDownManager(i, managerArray[i]);
						System.out.println(managerArray[i] + ": " + isManagerConnected[i]);
						everyConnectionsOk = false;
					}
				}
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
	}
	
	//Because we use an integer array in the run() that correspond to the string array, we need to go through it using a counter. 
	public int countArray(String[] _agentsArray){
		int number = 0;
		for(String agent: _agentsArray) {
		    if (!agent.trim().isEmpty()) {
		    	number ++;
		    }
		}
		return number;
	}
}
