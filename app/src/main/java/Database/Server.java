package Database;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    ServerSocket socketServer;
    Socket clientSocket;
    BufferedReader DIS;
    DataOutputStream DOS;
    LoginsCSV logins;

	String PATH = System.getProperty("user.dir") + "\\";
	String LOGINS_EXTENTION = ".txt";
	String ATTENDANCE_EXTENTION = ".txt";
    
    String pathToFolder;
    String currentActiveAttendancePeriodClassName;
    attendancePeriodCSV activePeriod;
    double lat = 0;
    double lon = 0;
    
    String command = "python " + PATH + "Database\\Database\\gpsCoord.py";
    /*
    public static void main(String[] args) throws IOException {
        ServerSocket socketServer = new ServerSocket(1420);
        Socket clientSocket = socketServer.accept();       //This is blocking. It will wait.
        DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream DOS = new DataOutputStream(clientSocket.getOutputStream());
        while(true){
            clientSocket = socketServer.accept();
            String input = DIS.readUTF();
            serveRequest(input);
        }
    }
    */

    public Server() throws IOException, InterruptedException {
        socketServer = new ServerSocket(1420);
        logins = new LoginsCSV(PATH + "//un");
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        Scanner scanner = new Scanner(new File("gps.txt"));
        lat = Double.parseDouble(scanner.nextLine());
        lon = Double.parseDouble(scanner.nextLine());
        scanner.close();
    }

    public void getNextRequest() throws IOException {
        Socket clientSocket = socketServer.accept(); //This is blocking. It will wait.
        DOS = new DataOutputStream(clientSocket.getOutputStream());
        DIS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String read = DIS.readLine();
        System.out.println(read + "--");
        serveRequest(read);
    }

    private void serveRequest(String in) throws IOException {
		checkAttendancePeriod();
        String[] split = in.split("&");
        int code;
        try {
            code = Integer.parseInt(split[0]);
        } catch(Exception e) {
            DOS.writeBytes("102&Bad request\n");
            return;
        }
        switch(code) {
            case 0: //Logins
                if (split.length != 3) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
                
                String username = split[1];
                String password = split[2];
                
                User user = logins.getUser(username);
                
                if(user == null) {
                	DOS.writeBytes("103&Username and password did not match\n");
                	return;
                }
                
                if(username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                	if(user.isTeacher()) DOS.writeBytes("1&Teacher\n");
                	else DOS.writeBytes("0&Is not teacher\n");
                } else {
                	DOS.writeBytes("103&Username and password did not match\n");
                }
                break;
            case 1: //Register
                if (split.length != 5) {
                    DOS.writeBytes("102&Bad request\n");
                	clientSocket.close();
                    return;
                }
                username = split[1];
                password = split[2];
                String name = split[3];
                String isTeacher = split[4];
                try {
                    Integer.parseInt(isTeacher);
                } catch(Exception e) {
                    DOS.writeBytes("102&Bad request\n");
                	clientSocket.close();
                    return;
                }
                logins.addUser(username, password, name, isTeacher);
                logins.write();
                
                DOS.writeBytes("0&Success!\n");
                break;
            case 2: //Student submit attendance
                if (split.length != 4) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
                username = split[1];
                String lat = split[2];
                String lon = split[3];
                
                user = logins.getUser(username);
                if(user == null) {
                	DOS.writeBytes("104&Error retrieving account\n");
                	return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
                	DOS.writeBytes("105& No active attendance period\n");
                	return;
                }
                
                if(tooFarAway(Double.parseDouble(lat), Double.parseDouble(lon))) {
                	DOS.writeBytes("110&You are too far away! Get closer!");
                	return;
                }
                boolean b = activePeriod.submitAttendance(user.getName());
                if(b) DOS.writeBytes("0&Attendance recieved!\n");
                else DOS.writeBytes("106&Student not found in class\n");
                
                break;
            case 3: //Begin attendance period
                if (split.length != 4) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
                username = split[1];
                String timeAsStr = split[2];
                String className = split[3];
                int realTimeInSeconds;

                try {
                    realTimeInSeconds = Integer.parseInt(timeAsStr);
                } catch(Exception e) {
                    DOS.writeBytes("102&Bad request\n");
					return;
                }

				user = logins.getUser(username);
				if(user == null) {
					DOS.writeBytes("104&Error retrieving account\n");
					return;
				}

				activePeriod = new attendancePeriodCSV(PATH + className + ATTENDANCE_EXTENTION, realTimeInSeconds);
				if(activePeriod == null || !activePeriod.exists()) {
					DOS.writeBytes("109&Class does not exist\n");
					return;
				}
				activePeriod.beginAttendancePeriod();

                    break;
            case 4: //Cancel attendance
                if (split.length != 2) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
                	DOS.writeBytes("105& No active attendance period\n");
                	return;
                }
                
                username = split[1];
                user = logins.getUser(username);
                
                if(user == null) {
                	DOS.writeBytes("104&Error retrieving account\n");
                	return;
                }

				if (user.hasAccessToClass(currentActiveAttendancePeriodClassName)) {
					activePeriod.cancelAttendancePeriod();
					DOS.writeBytes("0&Success!\n");
					return;
				}

                DOS.writeBytes("107&You don't have access to this class\n");
                
                break;
            case 5: //Create new class AHAHHHHHHHHHHHHHHHHHHHHHHHHH
                DOS.writeBytes("200&Create new class bad\n");
                break;
            case 6:
                if (split.length != 3) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
                username = split[1];
                className = split[2];

				if(activePeriod != null) {
					DOS.writeBytes("108&There is an active attendance period\n");
					return;
				}

				user = logins.getUser(username);
				if(user == null) {
					DOS.writeBytes("104&Failure to retrieve account\n");
					return;
				}

				if(user.hasAccessToClass(className)) {
					File file = new File(PATH + className + ATTENDANCE_EXTENTION);
					if(file.exists()) {
						file.delete();
						DOS.writeBytes("0&Success!\n");
						return;
					} else {
						DOS.writeBytes("109&The class doesn't exist!\n");
						return;
					}
				}
				DOS.writeBytes("107&You do not have access\n");
                break;
            case 7: //Get record
                if (split.length != 3) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
                username = split[1];
                className = split[2];
                
                String reponse = activePeriod.toString();
                DOS.writeBytes("202&Get record bad\n");
                break;

        }
    }

	private boolean tooFarAway(double lat2, double lon2) {
		double theta = lon - lon2;
		double dist = Math.sin(Math.toDegrees(lat)) * Math.sin(Math.toDegrees(lat2)) + Math.cos(Math.toDegrees(lat)) * Math.cos(Math.toDegrees(lat2)) * Math.cos(Math.toDegrees(theta));
		dist = Math.acos(dist);
		dist = Math.toRadians((dist));
		dist = dist * 60 * 1.1515;
		//Distance is now in terms of miles
		//25 feet is 0.00473485 miles
		
		System.out.println(dist);
		return ( dist > 0.00473485);
	}

	private void checkAttendancePeriod() {
		if(activePeriod == null) return;
		else try {
			activePeriod.endAttendancePeriod();
		} catch (IOException e) {
			return;
		}
	}
}
