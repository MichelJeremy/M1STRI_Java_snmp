import java.net.MalformedURLException;
import java.nio.channels.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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


	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, java.rmi.AlreadyBoundException, NotBoundException  {
		// only one agent needs to do this, thus make a try/catch so that one dude does it and after it will output an error but we are going to ignore it
		LocateRegistry.createRegistry(1099); 
		
		// enables this dude to listen through RMI, can throw an exception if agent is already registered in the registry : adds Agent_connection in RMI registry. Look up unbind maybe
		// does not block the code
		Naming.bind("Agent_connection", new Agent());
		System.out.println("Bind done");
		/*Naming.unbind("Agent_connection");
		System.out.println("Unbind done");*/
		
	}

}