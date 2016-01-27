import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Hashtable;

public class Manager extends Object{

	private int hierarchy; // int to know which priority this manager can manage
	private Hashtable<String, String> hash_agents = new Hashtable<String, String>(); // hash managing agent list (contact list, name -> address)
	
	
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
		Gestion g = new Gestion();
		RMI_Int_Agent souche=(RMI_Int_Agent) Naming.lookup("rmi://localhost/Agent_connection");
		String ptest = g.doThings();
		String test = souche.getSelfName();
		System.out.println("test : "+test);
		System.out.println("ptest : "+ptest);
	}
}
