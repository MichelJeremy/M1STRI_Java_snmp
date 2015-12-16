import java.net.MalformedURLException;
import java.nio.channels.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

//Represents an agent that is going to be connected to the manager
public class Agent extends UnicastRemoteObject implements RMI_Int_Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Agent() throws RemoteException {
		super();
	}
	
	public String getSelfName() {
		String name = "nom";
		return name;
	}


	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, java.rmi.AlreadyBoundException  {
		LocateRegistry.createRegistry(1099);
		Naming.bind("Agent_connection", new Agent());
	}

}