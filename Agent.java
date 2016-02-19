import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.channels.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//Represents an agent that is going to be connected to the manager
public class Agent extends UnicastRemoteObject implements RMI_Int_Agent{

	private static final long serialVersionUID = 1L;
	private static  String agentID = "agent1"; // agent's own ID, should become depreciated quickly
	private String name = "Jean";
	
	//vars for file reader
	private static BufferedReader br = null;
	private static String line = "";
	
	private static ArrayList<Integer> ports = new ArrayList<Integer>();
	private static Hashtable<Integer, String> portsTranslation = new Hashtable<Integer, String>();

	private String[] getCsv(String csvFilePath, String searchItem) {
		String result[] = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
			while ((line = br.readLine()) != null) {
				result[] = line.split(",");
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	return result;
	}
	//Read CSV file to get list of users
	
	//Thread to scan the ports
	private static PortScannerThread scannerThread;
	
	//Class to manage the Arraylists and Hashmaps
	private static Gestion gestion;
	
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
		
	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, java.rmi.AlreadyBoundException, NotBoundException  {
		
		// ============ CONFIGURATION ============
		ports.add(20); // apache
		ports.add(80); // http
		ports.add(443); // https
		ports.add(135); // test
		ports.add(445); // test2
		
		portsTranslation.put(20, "Apache");
		portsTranslation.put(80, "http");
		portsTranslation.put(443, "https");
		portsTranslation.put(135, "test");
		portsTranslation.put(445, "test2");
		// =======================================
		
		// only one agent needs to do this
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("Registry initiated");
		} catch (Exception e) {
			System.out.println("Registry already bound");
		}
		
		Naming.bind("Agent_connection", new Agent());
		LoadAccounts("/home/etu/M1STRI_Java_snmp/example.csv");
		
		gestion = new Gestion(ports, portsTranslation);
		
		scannerThread = new PortScannerThread(gestion);
		scannerThread.start();
		
			//Get the actives ports
			//activePorts = new ArrayList<Integer>(Scanner(ports));
			try {
				scannerThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		/*Naming.unbind("Agent_connection");
		System.out.println("Unbind done");*/
		
	}

}
