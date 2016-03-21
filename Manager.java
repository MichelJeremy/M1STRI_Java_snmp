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
	
	private static final String[] agentsArray = {"Agent1", "Agent2"};
	
	//*****************************************
	
	public static void main(String args[]) throws RemoteException, MalformedURLException{
		GestionManager gestion = new GestionManager(agentsArray);
		ConnectionsScannerThread thread = new ConnectionsScannerThread(gestion);
		gestion.menu();
	}

}
