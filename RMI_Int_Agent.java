//interface listing methods that can be called by the manager
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface RMI_Int_Agent extends java.rmi.Remote{
	public Hashtable<Integer, String> getActivesPorts() throws RemoteException;
	public String getName() throws RemoteException;
	public void setName(String _name) throws RemoteException;
}
