import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class Manager extends Object{

	private int hierarchy; // int to know which priority this manager can manage
	private Hashtable<String, String> hashAgents = new Hashtable<String, String>(); // hash managing agent list (contact list, name -> address)
	
	//Hashtable to store Agent's active ports // Enumeration to parse the HashTable
	private static Hashtable<Integer, String> activePorts = new Hashtable<Integer, String>();
	private static Enumeration<Integer> activePortsEnum;
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
		int currentPort;
		Scanner reader;
		
		RMI_Int_Agent souche=(RMI_Int_Agent) Naming.lookup("rmi://localhost/Agent_connection");
		
		//TEST of getting Agent's name
		System.out.println(souche.getName());
		
		//TEST of setting Agent's name
		souche.setName("Julie");
		System.out.println(souche.getName());
		
		//TEST of getting the Active Ports
		activePorts = new Hashtable<Integer, String>(souche.getActivesPorts());
		activePortsEnum = activePorts.keys();
		
		while(activePortsEnum.hasMoreElements()){
			//Enumeration has only the nextElement methods so we need to have a variable to store it for multiple use.
			currentPort = activePortsEnum.nextElement();
			
			System.out.println("Port " + currentPort + " : " + activePorts.get(currentPort));
		}
	
		reader = new Scanner(System.in);
		System.out.println("Welcome. Choose from the menu below (1-4):\n");
		System.out.println("1: snmpget");
		System.out.println("2: snmpget-next");
		System.out.println("3: snmpset");
		System.out.println("4: quit");
		
		int boucle = 1;
		while (boucle == 1) {
			System.out.println("Your choice: ");
			int choice = reader.nextInt();
			switch (choice) {
				case 1: //call snmpget
						System.out.println("1");
						break;
				case 2: //call snmpget-next
						System.out.println("2");	
						break;
				case 3: //call snmpset
						System.out.println("3");
						break;
				case 4: //quit
						System.out.println("4");
						boucle = 0;
				default: System.out.println("We did not understand your choice. Please try again and make sure your choice is correct.");
			}
		}
	}
}
