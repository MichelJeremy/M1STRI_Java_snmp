import java.io.File;
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

public class Manager{

	protected Manager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	//*****************************************
	//-------- Agents Configuration -----------
	
	private static final String name = "Manager3";
	
	private static final String[] agentsArray = {"Agent3", "Agent4"};
	
	private static final int hierarchy = 2;
	private static final String[] ManagerArray = {"Manager1", "Manager2"};
	
	//*****************************************
	
	public static void main(String args[]) throws RemoteException, MalformedURLException{
		GestionManager gestion = new GestionManager(name, agentsArray, ManagerArray, hierarchy);
		ConnectionsScannerThread thread = new ConnectionsScannerThread(gestion);
		thread.start();
		
		if(hierarchy == 1){
			gestion.menuAgent();
		}
		else{
			gestion.menuManager();
		}
	}

}
