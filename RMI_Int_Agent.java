//interface listing methods that can be called by the manager
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface RMI_Int_Agent extends java.rmi.Remote{
	public Hashtable<Integer, String> getActivesPorts() throws RemoteException;
}
