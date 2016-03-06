import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TrapThread extends Thread{
	
	private RMI_Int_Manager souche;
	
	public void run(){
		
	}
	
	public TrapThread(){
		try {
			souche=(RMI_Int_Manager) Naming.lookup("rmi://localhost/Manager_connection");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
