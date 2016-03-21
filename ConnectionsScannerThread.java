import java.rmi.Naming;

public class ConnectionsScannerThread extends Thread{

	private GestionManager g;
	private String[] agentsArray;
	
	public ConnectionsScannerThread(GestionManager gestion) {
		g = gestion;
		agentsArray = g.getAgentsArray();
		
	}
	
	public void run() {
		//We need to know the number of agents
		int numberOfAgents = countAgent(agentsArray);
		
		int[] isAgentConnected = new int[numberOfAgents];
		boolean everyConnectionsOk = false;
		
		//Try to connect to the two agents until it is done
		while (!everyConnectionsOk){
			everyConnectionsOk = true;
			
			for(int i = 0; i < numberOfAgents; i++){
				//We use 0 as default because we do not know the length of the Array and because 0 is automatically set when the initialisation is done, it is easier to do so.
				if(isAgentConnected[i] == 0){
					isAgentConnected[i] = g.setSoucheAgent(agentsArray[i]);
					everyConnectionsOk = false;
				}
			}
		}
			
	}
	
	//Because we use an integer array in the run() that correspond to the string array, we ned to go through it using a counter. 
	public int countAgent(String[] _agentsArray){
		int number = 0;
		for(String agent: _agentsArray) {
		    if (!agent.trim().isEmpty()) {
		    	number ++;
		    }
		}
		return number;
	}

}
