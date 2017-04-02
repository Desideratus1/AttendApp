package Database;

public class ServerRunner {
	static Server serv;
	
	public static void main(String[] args) {
		try {
			serv = new Server();
			while(true) serv.getNextRequest();
		} catch (Exception e) {
			System.out.println("Something caused the server to crash. Restarting...");
			e.printStackTrace();
		}
	}
}