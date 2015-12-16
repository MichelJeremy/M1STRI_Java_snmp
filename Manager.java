import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Manager extends Object{

	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
		Gestion g = new Gestion();
		RMI_Int_Agent souche=(RMI_Int_Agent) Naming.lookup("rmi://localhost/Agent_connection");
		String ptest = g.doThings();
		String test = souche.getSelfName();
		System.out.println("test : "+test);
		System.out.println("ptest : "+ptest);
	}
}
