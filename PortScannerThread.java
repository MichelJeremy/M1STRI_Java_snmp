import java.net.Socket;
import java.util.ArrayList;

public class PortScannerThread extends Thread {
	
	private Gestion g;
	
	private ArrayList<Integer> ports = new ArrayList<Integer>();
	private ArrayList<Integer> activePorts = new ArrayList<Integer>();
	
	@Override
	public void run() {
		//We are permanently updating the port state ArrayList
		while(true){
			for (int i = 0; i < ports.size(); i++) {
				int portAct = ports.get(i);
				try {
					Socket sock = new Socket("localhost", portAct);
					activePorts.add(portAct);
					sock.close();
				} catch(Exception e) {}
			}
			//Modify in the gestion object the active port ArrayList;
			g.setActivePorts(activePorts);
		}
	}
	
	public PortScannerThread(Gestion _g){
		//The Gestion object will allow to modify the variable isnide it.
		g = _g;
		ports = new ArrayList<Integer>(g.getPorts());
	}
	
	public ArrayList<Integer> getActivePorts(){
		return activePorts;
	}
	
}
