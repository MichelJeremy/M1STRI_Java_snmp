import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

// going to process things for Agent
public class Gestion {
	
	private static  String agentID = "agent1"; // agent's own ID, should become depreciated quickly
	
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
		

		// First we read the initial line and make sure it exists
		// Then, we write the content of the original file to the temp file
		// We modify the line with the new value (set) and write it where the original line was
		// Finally, remove the original file
		// The temp file is renamed to the original file's name
		// returns -1 in case of error, 1 in case of success
		public static int csvSetValue(String csvFilePath, String OID, String[] newValues, String agent, String user, String pass) {
			// vars
			BufferedReader br = null;
			BufferedWriter bw = null;
			String[] existenceCheck = null;
			boolean existence = false;
			String line = "";
			String newline = "";
			String result[] = null;
			int i=0;
			StringBuilder tempNewLine;
			StringBuilder pathOID = new StringBuilder().append(System.getProperty("user.dir")).append("\\src\\exampleOID.csv.bkp");
			StringBuilder pathUser = new StringBuilder().append(System.getProperty("user.dir")).append("\\src\\secret.csv");
			boolean isAuthorizedToWrite = false;
			boolean isconnectionOk = false;
			
			//file declarations
			File temp = new File(pathOID.toString());
			File orig = new File(csvFilePath);
			
			//Test if accreditations are ok to write
			try{
				br = new BufferedReader(new FileReader(pathUser.toString()));
				while ((line = br.readLine()) != null) {
					result = line.split(",");
					if (result[0].equals(agent) && result[1].equals(user) && result[2].equals(pass)) {
						isconnectionOk = true;
						if(result[3].equals("rw")){
							isAuthorizedToWrite = true;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(isAuthorizedToWrite == false){
				return -3;
			}
			
			if(isconnectionOk == false){
				return -2;
			}
			
			existenceCheck = csvLookup(csvFilePath, OID);
			if (existenceCheck[0].equals(OID)) {
				existence = true;
			} else {
				return -1;
			}
			
			try {
				temp.createNewFile();
				br = new BufferedReader(new FileReader(csvFilePath));
				bw = new BufferedWriter(new FileWriter(pathOID.toString()));
				while ((line = br.readLine()) != null) {
					result = line.split(",");
					if (result[0].equals(OID)) {
						//this is the line that we want to modify
						// Recreate it and then write it
						// Parse the String[] of new values
						tempNewLine = new StringBuilder().append(OID).append(",");
						
						while(i < newValues.length){
							tempNewLine.append(newValues[i]).append(",");
							i++;
						}

						tempNewLine.deleteCharAt(tempNewLine.length()-1);
						newline = tempNewLine.toString();
						
						bw.write(newline);
						bw.newLine();
					} else {
						//this is the case where the targeted line of the set action isn't this line
						bw.write(line);
						bw.newLine();
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// now swap the files : remove the original and replace by the new
			try {
				orig.delete();
				temp.renameTo(orig);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			return 1;
		}
		
		
		
		
		// searches the item "searchItem" and returns the line's fields
		// Create a BufferedReader to read the file line by line
		// If 'SearchItem' is found, returns the split line
		// else, the String "Unknown Item" will be returned in the first field of the array result[] (result[0])
		public static String[] csvLookup(String csvFilePath, String searchItem) {
			//vars
			BufferedReader br = null;
			String line = "";
			String result[] = null;
			boolean match = false;
			
			try {
				br = new BufferedReader(new FileReader(csvFilePath));
				while ((line = br.readLine()) != null) {
					result = line.split(",");
					if (result[0].equals(searchItem)) {
						match = true;
						return result;
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		// if no matches were found, the query is incorrect and thus we :
		// 1 : empty the fields
		// 2 : insert "Unknown item in the first field
		if (match == false) {
			for (int i=0;i<result.length;i++) {
				result[i] = "";
			}
			result[0] = "Unknown item";
		}
		return result;
	}
	
		
		// searches the item "searchItem" and returns the line's fields
		// Create a BufferedReader to read the file line by line
		// If 'SearchItem' is found, returns the split line
		// else, the String "Unknown Item" will be returned in the first field of the array result[] (result[0])
		public static String[] csvGetNext(String csvFilePath, String searchNextItem) {
			//vars
			BufferedReader br = null;
			String line = "";
			String result[] = null;
			boolean match = false;
			
			try {
				br = new BufferedReader(new FileReader(csvFilePath));
				while ((line = br.readLine()) != null) {
					result = line.split(",");
					if (result[0].equals(searchNextItem)) {
						match = true;
						//Get the next line.
						line = br.readLine();
						result = line.split(",");
						return result;
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		// if no matches were found, the query is incorrect and thus we :
		// 1 : empty the fields
		// 2 : insert "Unknown item in the first field
		if (match == false) {
			for (int i=0;i<result.length;i++) {
				result[i] = "";
			}
			result[0] = "Unknown item";
		}
		return result;
	}	
	
}
