import java.net.MalformedURLException;
import java.nio.channels.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;

//Represents an agent that is going to be connected to the manager
public class Agent extends UnicastRemoteObject implements RMI_Int_Agent{

	private static final long serialVersionUID = 1L;
	
	//*************************************************************
	//---------- Change the name for the different agents ---------
	private static String name = "Agent1";
	private static String user = "usertest1";
	private static String password = "passtest1";
	
	//*************************************************************
	
	private static ArrayList<Integer> ports = new ArrayList<Integer>();
	private static Hashtable<Integer, String> portsTranslation = new Hashtable<Integer, String>();
	private static Hashtable<Integer, Integer> portsPriority = new Hashtable<Integer, Integer>();
	
	//Thread to scan the ports
	private static PortScannerThread scannerThread;
	
	//Class to manage the Arraylists and Hashmaps
	private static GestionAgent gestion;
	
	public Agent() throws RemoteException {
		super();
	}
	
	//Called from the Manager, it is used to get the lists of active ports.
	public Hashtable<Integer, String> getActivesPorts(){
		return(gestion.getActivesPorts());
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String _name){
		name = _name;
	}
	
	//Search the mib for the corresponding information
	public String[] getMIBInformation(String OID){
		//Get the project path (helps if used on different machines)
		StringBuilder temp = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB_").append(name).append(".csv");
		System.out.println(temp);
		return gestion.csvLookup(temp.toString(), OID);
	}
	
	//Search the mib for the corresponding information and replace it by the new values
	public int setMIBInformation(String OID, String[] values, String agentName, String ID, String password){
		StringBuilder temp = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB_").append(name).append(".csv");
		return gestion.csvSetValue(temp.toString(), OID, values, name, ID, password);
	}
	
	public String[] getNext(String searchNextItem){
		StringBuilder temp = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB_").append(name).append(".csv");
		return gestion.csvGetNext(temp.toString(), searchNextItem);
	}
		
	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, java.rmi.AlreadyBoundException, NotBoundException  {
		
		// ============ CONFIGURATION ============
		//If modified, you need to modify the MIB.csv
		
		ports.add(20); // apache
		ports.add(80); // http
		ports.add(443); // https
		ports.add(135); // test
		ports.add(445); // test2
		ports.add(61005); //socket1
		ports.add(61006); //socket2
		ports.add(61007); //socket3
		
		portsTranslation.put(20, "Apache");
		portsTranslation.put(80, "http");
		portsTranslation.put(443, "https");
		portsTranslation.put(135, "test");
		portsTranslation.put(445, "test2");
		portsTranslation.put(61005, "socket1");
		portsTranslation.put(61006, "socket2");
		portsTranslation.put(61007, "socket3");
		
		portsPriority.put(20, 1);
		portsPriority.put(61005, 1);
		portsPriority.put(135, 1);
		
		portsPriority.put(80, 2);
		portsPriority.put(445, 2);
		portsPriority.put(61006, 2);
		
		portsPriority.put(443, 3);
		portsPriority.put(61007, 3);
		
		// =======================================
		
		
		// only one agent needs to do this
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("Registry initiated");
		} catch (Exception e) {
			
		}
		
		//Create different binding names with the agent name
		StringBuilder agentConnection = new StringBuilder().append(name).append("_connection");
		Naming.bind(agentConnection.toString(), new Agent());
		
		gestion = new GestionAgent(name, ports, portsTranslation, portsPriority);
		
		gestion.createMib(name, user, password);
		
		scannerThread = new PortScannerThread(gestion);
		scannerThread.start();
		
		try {
			scannerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
