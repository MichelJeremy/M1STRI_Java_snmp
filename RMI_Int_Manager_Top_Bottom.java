import java.rmi.RemoteException;

public interface RMI_Int_Manager_Top_Bottom extends java.rmi.Remote{
	public String[] getAgents() throws RemoteException;
	public int[] getPriority() throws RemoteException;
	public void setManagerPriority(int[] managerPriority) throws RemoteException;
	public int[] getManagerPriority() throws RemoteException;
	public void menuAgent() throws RemoteException;
}
