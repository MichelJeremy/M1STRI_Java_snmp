//interface listing methods that can be called by the manager
import java.rmi.RemoteException;

public interface RMI_Int_Manager extends java.rmi.Remote{
	public void trap(String[] message) throws RemoteException;
}
