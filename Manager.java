import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

public class Manager extends Object{

	private int hierarchy; // int to know which priority this manager can manage
	private Hashtable<String, String> hashAgents = new Hashtable<String, String>(); // hash managing agent list (contact list, name -> address)
	
	//Hashtable to store Agent's active ports // Enumeration to parse the HashTable
	private static Hashtable<Integer, String> activePorts = new Hashtable<Integer, String>();
	private static Enumeration<Integer> activePortsEnum;
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
		int currentPort;
		
		RMI_Int_Agent souche=(RMI_Int_Agent) Naming.lookup("rmi://localhost/Agent_connection");
		
		activePorts = new Hashtable<Integer, String>(souche.getActivesPorts());
		activePortsEnum = activePorts.keys();
		
		while(activePortsEnum.hasMoreElements()){
			//Enumeration has only the nextElement methods so we need to have a variable to store it for multiple use.
			currentPort = activePortsEnum.nextElement();
			
			System.out.println("Port " + currentPort + " : " + activePorts.get(currentPort));
		}		
	}
}
