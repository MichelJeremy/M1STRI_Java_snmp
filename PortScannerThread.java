import java.net.Socket;
import java.util.ArrayList;

public class PortScannerThread extends Thread {
	
	private Gestion g;
	
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
			//First test if the size of the Arraylits are the same
			if(activePortsTest.size() != activePorts.size()){
				hasChanged = true;
			}
			else{
				while(!hasChanged && i < activePortsTest.size()){
					if(!activePorts.get(i).equals(activePortsTest.get(i))){
						System.out.println(activePorts.get(i) + "     " + activePortsTest.get(i));
						hasChanged = true;
					}
					i++;
				}
			}
			if(hasChanged){
				writeActivePorts();
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
		//The Gestion object will allow the modification of variables inside it.
		g = _g;
		ports = new ArrayList<Integer>(g.getPorts());
	}
}
