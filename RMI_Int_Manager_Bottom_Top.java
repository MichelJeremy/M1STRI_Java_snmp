import java.rmi.RemoteException;

public interface RMI_Int_Manager_Bottom_Top extends java.rmi.Remote{
	public void sendTrap(String[] message) throws RemoteException;
}
