import java.util.ArrayList;
import java.util.Hashtable;

// going to process things for Agent
public class Gestion {
	
	// We use ArrayLists for its memory access rapidity when periodically looking at the actives ports
	private static ArrayList<Integer> ports = new ArrayList<Integer>();
	private static Hashtable<Integer, String> portsTranslation = new Hashtable<Integer, String>();

	//Same as the ArrayList case, translation Hashtable will only be updated when access methods used.
	private static ArrayList<Integer> activePorts = new ArrayList<Integer>();
	private static Hashtable<Integer, String> activePortsTranslation = new Hashtable<Integer, String>();
	
	//Getter and Setter useful for the Thread so it can modify the ArrayList
	public static ArrayList<Integer> getPorts() {
		return ports;
	}
	public static void setActivePorts(ArrayList<Integer> _activePorts) {
		activePorts = new ArrayList<Integer>(_activePorts);
	}
	
	
	public Gestion(ArrayList<Integer> _ports, Hashtable<Integer, String> _portsTranslation) {
		super();
		ports = _ports;
		portsTranslation = _portsTranslation;
	}
	
	//Called from the Manager then the Agent, it is used to get the lists of active ports.
		public Hashtable<Integer, String> getActivesPorts(){
			int i;
			
			//Clear the HashMap every time the methods is called
			activePortsTranslation.clear();
			
			for(i=0; i<activePorts.size(); i++){
				activePortsTranslation.put(activePorts.get(i), portsTranslation.get(activePorts.get(i)));
			}
			return activePortsTranslation;
		}
}
