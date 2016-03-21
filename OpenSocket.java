import java.io.IOException;
import java.net.ServerSocket;

public class OpenSocket {

	public static void main(String[] args) {
		try {
			ServerSocket s = new ServerSocket(61006);
			while(true){
			System.out.println(s.accept());	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
