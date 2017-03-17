package Database;

import java.io.IOException;

public class ServerRunner {
	static Server serv;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		serv = new Server();
		while(true) serv.getNextRequest();
	}
}