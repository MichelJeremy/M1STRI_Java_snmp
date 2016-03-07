import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class PortScannerThread extends Thread{
	
	private Gestion g;
	private RMI_Int_Manager rmi;
	
	private ArrayList<Integer> ports = new ArrayList<Integer>();
	private ArrayList<Integer> activePorts = new ArrayList<Integer>();
	private ArrayList<Integer> inactivePorts = new ArrayList<Integer>();
	private ArrayList<Integer> activePortsTest = new ArrayList<Integer>();
	
	@Override
	public void run() {
		boolean hasChanged=false;
		int i;
		//We are permanently updating the port state ArrayList
		
		while(true){
			//Reset the Arraylist
			activePorts = new ArrayList<Integer>();
			for (i = 0; i < ports.size(); i++) {
				int portAct = ports.get(i);
				try {
					Socket sock = new Socket("localhost", portAct);
					activePorts.add(portAct);
					sock.close();
				} catch(Exception e) {}
			}
			//Modify the active port ArrayList if it has changed
			i=1;
			//First test if the size of the Arraylist are the same
			if(activePortsTest.size() != activePorts.size()){
				hasChanged = true;
			}
			else{
				while(i < activePortsTest.size()){
						if(!activePorts.get(i).equals(activePortsTest.get(i))){
							hasChanged = true;
						}
						i++;
				}
			}
			if(hasChanged){
				System.out.println("yo");
				writeActivePorts();
				manageChangeState();
				//Change the values of the arraylists so that next loop will work
				activePortsTest = new ArrayList<Integer>(activePorts);
				hasChanged = false;
			}
		}
	}
	
	private synchronized void writeActivePorts(){
		StringBuilder temp = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB.csv");
		String[] portStatus = null;
		//Set the active port to 1
		for(int i=0; i < activePorts.size(); i++){
			portStatus = "1".split("");
			g.csvSetValue(temp.toString(), activePorts.get(i).toString(), portStatus , "threadP", "threadP", "robot");
		}
		//Set the non active ports to 0
		for(int i=0; i < inactivePorts.size(); i++){
			portStatus = "0".split("");
			g.csvSetValue(temp.toString(), inactivePorts.get(i).toString(), portStatus , "threadP", "threadP", "robot");
		}
		
	}
	
	public PortScannerThread(Gestion _g){
		//The Gestion object will allow the modification of variables inside of it.
		g = _g;
		ports = new ArrayList<Integer>(g.getPorts());
		
		try {
			sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			rmi =(RMI_Int_Manager) Naming.lookup("rmi://localhost/Manager_connection");
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
	
	private void sendTrap(String trap){
		
		try {
			rmi.trap(trap);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void manageChangeState(){
		
		//Get the new active ports
		for(int i=0; i< activePorts.size(); i++){
			for(int j=0; j<activePortsTest.size(); j++){
				System.out.println(activePorts.get(i) + "    " + activePortsTest.get(j));
				if(activePorts.get(i).equals(activePortsTest.get(j))){
					break;
				}
				else if(j == (activePortsTest.size()-1)){
					StringBuilder trapBuilder = new StringBuilder().append(activePorts.get(i).toString()).append(" is now active");
					System.out.println(trapBuilder.toString());
					sendTrap(trapBuilder.toString());
				}
			}
		}
		
		//Get the old active ports
		for(int i=0; i< activePortsTest.size(); i++){
			for(int j=0; j<activePorts.size(); j++){
				System.out.println(activePorts.get(j) + "    " + activePortsTest.get(i));
				if(activePortsTest.get(i).equals(activePorts.get(j))){
					break;
				}
				else if(j == activePorts.size()-1){
					StringBuilder trapBuilder = new StringBuilder().append(activePortsTest.get(i).toString()).append(" is now inactive");
					System.out.println(trapBuilder.toString());
					sendTrap(trapBuilder.toString());
				}
			}
		}
	}
}
