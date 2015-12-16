//interface listing methods that can be called by the manager
import java.rmi.RemoteException;

public interface RMI_Int_Agent extends java.rmi.Remote{
	public String getSelfName() throws RemoteException;
}
