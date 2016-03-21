import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class PortScannerThread extends Thread{
	
	private GestionAgent g;
	private RMI_Int_Manager rmi= null;
	
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
			//Reset the Arraylists
			activePorts = new ArrayList<Integer>();
			inactivePorts = new ArrayList<Integer>();
			
			for (i = 0; i < ports.size(); i++) {
				int portAct = ports.get(i);
				try {
					Socket sock = new Socket("localhost", portAct);
					activePorts.add(portAct);
					sock.close();
				} catch(Exception e) {
					inactivePorts.add(portAct);
				}
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
				writeActivePorts();
				manageChangeState();
				//Change the values of the arraylists so that next loop will work
				activePortsTest = new ArrayList<Integer>(activePorts);
				
				hasChanged = false;
			}
		}
	}
	
	private synchronized void writeActivePorts(){
		StringBuilder temp = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB_").append(g.getName()).append(".csv");
		String[] portStatus = null;
		//Set the active port to 1
		for(int i=0; i < activePorts.size(); i++){
			portStatus = "1".split("");
			GestionAgent.csvSetValue(temp.toString(), activePorts.get(i).toString(), portStatus , "threadP", "threadP", "robot");
		}
		//Set the non active ports to 0
		for(int i=0; i < inactivePorts.size(); i++){
			portStatus = "0".split("");
			GestionAgent.csvSetValue(temp.toString(), inactivePorts.get(i).toString(), portStatus , "threadP", "threadP", "robot");
		}
		
	}
	
	public PortScannerThread(GestionAgent _g){
		//The Gestion object will allow the modification of variables inside of it.
		g = _g;
		ports = new ArrayList<Integer>(g.getPorts());
		StringBuilder lookup = new StringBuilder().append("rmi://localhost/Manager_connection_").append(g.getName());
		
		//Try until it can bound with the Manager (wait until it is created)
		while(rmi == null){
			//Wait .5 sec until retry to connect to the Manager
			
			try {
				sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			try {
				
				rmi =(RMI_Int_Manager) Naming.lookup(lookup.toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				//No errors displayed because it is normal to have some because the Agent is started before the manager
				//(Hence, the lookup will not work at the begining
			}
		
		}
	}
	
	private void sendTrap(String[] trap){
		
		try {
			rmi.trap(trap);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void manageChangeState(){
		
		//Send a trap for the new active ports
		for(int i=0; i< activePorts.size(); i++){
			for(int j=0; j<activePortsTest.size(); j++){
				if(activePorts.get(i).equals(activePortsTest.get(j))){
					break;
				}
				else if(j == (activePortsTest.size()-1)){
					String[] temp;
					String[] returnString;
					StringBuilder location = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB_").append(g.getName()).append(".csv");
					temp = GestionAgent.csvLookup(location.toString(), activePorts.get(i).toString());
					
					//TempS2 will create the return string
					returnString = new String[temp.length+1];
					returnString[0] = GestionAgent.getName();
					for(int k=0; k<temp.length; k++){
						returnString[k+1] = temp[k];
					}
					sendTrap(returnString);
				}
			}
		}
		
		//Send a trap for the new inactive ports (Not well designed as a single array would have been better
		for(int i=0; i< activePortsTest.size(); i++){
			for(int j=0; j<activePorts.size(); j++){
				if(activePortsTest.get(i).equals(activePorts.get(j))){
					break;
				}
				else if(j == activePorts.size()-1){
					String[] temp;
					String[] returnString;
					StringBuilder location = new StringBuilder().append(System.getProperty("user.dir")).append("/src/MIB_").append(g.getName()).append(".csv");
					temp = g.csvLookup(location.toString(), activePortsTest.get(i).toString());
					
					//TempS2 will create the return string
					returnString = new String[temp.length+1];
					returnString[0] = g.getName();
					for(int k=0; k<temp.length; k++){
						returnString[k+1] = temp[k];
					}
					sendTrap(returnString);
				}
			}
		}
	}
}
