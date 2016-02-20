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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//Represents an agent that is going to be connected to the manager
public class Agent extends UnicastRemoteObject implements RMI_Int_Agent{

	private static final long serialVersionUID = 1L;
	private static  String agentID = "agent1"; // agent's own ID, should become depreciated quickly
	private String name = "Jean";
	
	private static ArrayList<Integer> ports = new ArrayList<Integer>();
	private static Hashtable<Integer, String> portsTranslation = new Hashtable<Integer, String>();

	
	// First we read the initial line and make sure it exists
	// Then, we buffer the whole file
	// We modify the line with the new value (set)
	// Finally, we rewrite the file
	// returns -1 in case of error, 1 in case of success
	private static int csvSetValue(String csvFilePath, String OID, String newValue) {
		// vars
		BufferedReader br = null;
		BufferedWriter bw = null;
		String[] existenceCheck = null;
		boolean existence = false;
		String line = "";
		String newline = "";
		String result[] = null;
		
		existenceCheck = csvLookup(csvFilePath, OID);
		if (existenceCheck[0].equals(OID)) {
			existence = true;
		} else {
			return -1;
		}
		
		try {
			File f = new File("/home/jeremy/M1STRI_Java_snmp/exampleOID.csv.bkp");
			f.createNewFile();
			
			br = new BufferedReader(new FileReader(csvFilePath));
			bw = new BufferedWriter(new FileWriter("/home/jeremy/M1STRI_Java_snmp/exampleOID.csv.bkp"));
			while ((line = br.readLine()) != null) {
				result = line.split(",");
				if (result[0].equals(OID)) {
					//this is the line that we want to modify
					// Recreate it and then write it
					newline = new StringBuilder().append(OID).append(",").append(newValue).toString();
					bw.write(newline);
					bw.newLine();
				} else {
					//this is the case where the targeted line of the set action isn't this line
					bw.write(line);
					bw.newLine();
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// now swap the files
		//TODO
		return 1;
	}
	
	
	
	
	// searches the item "searchItem" and returns the line's fields
	// Create a BufferedReader to read the file line by line
	// If 'SearchItem' is found, return the split line
	// else, the String "Unknown Item" will be returned in the first field of the array result[] (result[0])
	private static String[] csvLookup(String csvFilePath, String searchItem) {
		//vars
		BufferedReader br = null;
		String line = "";
		String result[] = null;
		boolean match = false;
		
		try {
			br = new BufferedReader(new FileReader(csvFilePath));
			while ((line = br.readLine()) != null) {
				result = line.split(",");
				if (result[0].equals(searchItem)) {
					match = true;
					return result;
				}
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

	// if no matches were found, the query is incorrect and thus we :
	// 1 : empty the fields
	// 2 : insert "Unknown item in the first field
	if (match == false) {
		for (int i=0;i<result.length;i++) {
			result[i] = "";
		}
		result[0] = "Unknown item";
	}
	return result;
	}

	
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
		
		String blbl[] = csvLookup("/home/jeremy/M1STRI_Java_snmp/example.csv", "agent2");
		System.out.println("Field 1 : "+blbl[0]+"\nField 2 : "+blbl[1]+"\nField 3 : "+blbl[2]+"\nField 4 : "+blbl[3]);
			
		int vartest = csvSetValue("/home/jeremy/M1STRI_Java_snmp/exampleOID.csv", "1.2.3.4.1","wtfifthatworks");
		System.out.println(vartest);
		
		
		
		// only one agent needs to do this
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("Registry initiated");
		} catch (Exception e) {
			System.out.println("Registry already bound");
		}
		
		Naming.bind("Agent_connection", new Agent());
		
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
