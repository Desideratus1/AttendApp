package Database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;

public class Server {

    private ServerSocket socketServer;
	private Socket clientSocket;
	private BufferedReader DIS;
	private DataOutputStream DOS;
	private LoginsCSV logins;

	private String PATH = System.getProperty("user.dir") + "\\"; //Path to the current directory
	private String PATH_FOR_ATTENDANCE_RECORDS = PATH + "records_for_"; //Path to directory with beginning of attendnace record file names
	private String ATTENDANCE_EXTENTION = ".txt"; //Extention for attendance records

	private String currentActiveAttendancePeriodClassName = null;
	private attendancePeriodCSV activePeriod = null; //THe current active attendance period
	private static double lat = 0;
	private static double lon = 0;

    public Server() throws IOException, InterruptedException {
    	System.out.println(PATH);
        socketServer = new ServerSocket(1420);
        logins = new LoginsCSV(PATH + "//un");
		getGPS();
    }

    public void getNextRequest() throws IOException {
        Socket clientSocket = socketServer.accept(); //This is blocking. It will wait for a new request
        Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() { //Once a new request is recieved we make a new thread and run it
				try {
					DOS = new DataOutputStream(clientSocket.getOutputStream()); //If this gives an error ignore it
					DIS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			        String read = DIS.readLine();
			        System.out.println(read + "--");
			        serveRequest(read);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        });
        thr.start();
    }

	/**
	 * Function to serve a request that was recieved from the App
	 * @param in A string that is the data recieved from the App
	 * @throws IOException //If something goes wrong.
	 */
    private void serveRequest(String in) throws IOException {
    	if(in == null) return;
		checkAttendancePeriod(); //An active check to determine if the attendance period has expired
        String[] split = in.split("&");
        int code;
        try {
            code = Integer.parseInt(split[0]);
        } catch(Exception e) {
            DOS.writeBytes("102&Bad request\n");
            return;
        }
        switch(code) {
            case 0: //Logins ----------------------
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
            case 1: //Register ------------------
                if (split.length != 5) {
                    DOS.writeBytes("102&Bad request\n");
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
                    return;
                }
                logins.addUser(username, password, name, isTeacher);
                logins.write();
                
                DOS.writeBytes("0&Success!\n");
                break;
            case 2: //Student submit attendance --------------------
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
                
                if(tooFarAway(Double.parseDouble(lat), Double.parseDouble(lon))) {
                	DOS.writeBytes("110&You are too far away! Get closer!\n");
                	return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
                	DOS.writeBytes("105& No active attendance period\n");
                	return;
                }
                
                boolean b = activePeriod.submitAttendance(user.getName());
                if(b) DOS.writeBytes("0&Attendance recieved!\n");
                else DOS.writeBytes("106&Student not found in class\n");
                
                break;
            case 3: //Begin attendance period ----------------------
            	if(activePeriod != null) {
            		DOS.writeBytes("108&Active attendance period!\n");
            		return;
            	}
            	
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

				System.out.println("1");
				activePeriod = new attendancePeriodCSV(PATH_FOR_ATTENDANCE_RECORDS + className + ATTENDANCE_EXTENTION, realTimeInSeconds);
				if(!activePeriod.exists()) {
					activePeriod = null;
					DOS.writeBytes("109&Class does not exist\n");
					return;
				}
				currentActiveAttendancePeriodClassName = className;
				activePeriod.beginAttendancePeriod();
				DOS.writeBytes("0&Success!\n");

                break;
            case 4: //Cancel attendance --------------------
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
            case 5: //Create new class -------------------------
            	if (split.length != 3) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }
            	username = split[1];
            	className = split[2];
            	
            	user = logins.getUser(username);
                
                if(user == null) {
                	DOS.writeBytes("104&Error retrieving account\n");
                	return;
                }
                
                File newClass = new File("records_for_" + className + ATTENDANCE_EXTENTION);
                if(newClass.exists()) {
             	   DOS.writeBytes("111&This file exists, we can not make a new class out of it. Delete this class first\n");
             	   return;
                }
                
               ArrayList<String> f = extractDataForNewClass(PATH + className + ".txt");
               if(f == null) {
            	   DOS.writeBytes("112&File does not exist\n");
            	   return;
               }
               newClass.createNewFile();

               FileWriter writer = new FileWriter(newClass);
               StringBuilder toWrite = new StringBuilder("Date,");
               for(String str : f) {
            	   toWrite.append(str).append(",");
   			   }
               toWrite.deleteCharAt(toWrite.length()-1);
               writer.write(toWrite.toString());
               writer.close();
               
               user.addClass(className);
               logins.write();
               DOS.writeUTF("0&Success!\n");
                break;
            case 6: //Delete class request ---------------------
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
					File file = new File(PATH_FOR_ATTENDANCE_RECORDS + className + ATTENDANCE_EXTENTION);
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
            case 7: //Get record ---------------------------------
                if (split.length != 3) {
                    DOS.writeBytes("102&Bad request\n");
                    return;
                }

                username = split[1];
                className = split[2];


				if(currentActiveAttendancePeriodClassName != null && className.equals(currentActiveAttendancePeriodClassName)) {
					DOS.writeBytes("108&The class you want is currently active!\n");
					return;
				}

				user = logins.getUser(username);
				if(user == null) {
					DOS.writeBytes("104&Failure to retrieve account\n");
					return;
				}

				if(!user.hasAccessToClass(className)) {
					DOS.writeBytes("107&You don't have access to this class!\n");
					return;
				}

				attendancePeriodCSV classDat = new attendancePeriodCSV(PATH_FOR_ATTENDANCE_RECORDS + className,100);
				if(!classDat.exists()) {
					DOS.writeBytes("109&The class doesn't exist!\n");
				}

                String response = classDat.toString();
                DOS.writeBytes("3&" + response.replaceAll("\n","|")+ "\n");
                break;

        }
    }

	public static boolean tooFarAway(double lat2, double lon2) {
		if(lat2 == lat && lon2 == lon) return true;
	    double earthRadius = 6371000; //meters
	    double dLat = Math.toRadians(lat2-lat);
	    double dLng = Math.toRadians(lon2-lon);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    float dist = (float) (earthRadius * c);
		return (dist > 0.00473485);
	    }

	private void checkAttendancePeriod() {
		if(activePeriod == null) return;
		else try {
			if(activePeriod.endAttendancePeriod()) {
				currentActiveAttendancePeriodClassName = null;
				return;
			}
		} catch (IOException e) {
			return;
		}
	}
	
	private ArrayList<String> extractDataForNewClass(String fileName) {
		
		 File fil = new File(fileName);
         ArrayList<String> names = new ArrayList<String>();
         Scanner scan;
		try {
			scan = new Scanner(fil);
		} catch (FileNotFoundException e) {
			return null;
		}
         while(scan.hasNextLine()) names.add(scan.nextLine());
         scan.close();
         return names;
	}

	private void getGPS() {
		URL url;
		try {
			url = new URL("http://freegeoip.net/json");
		} catch (MalformedURLException e) {
			System.out.println("We can not run because we can not connect to the website required to pull GPS.");
			System.exit(0);
			return;
		}
		Scanner scanner = null;
		try {
			scanner = new Scanner(url.openStream());
		} catch (IOException e) {
			System.out.println("We can not run because we can not connect to the website required to pull GPS.");
			System.exit(0);
			return;
		}
		String str = "";
		while(scanner.hasNextLine()) {
			str = str + scanner.nextLine();
		}
		scanner.close();
		String[] split = str.split(",");
		for(String string : split) {
			if(string.contains("latitude")) {
				String[] moresplit = string.split(":");
				lat = Double.parseDouble(moresplit[1]);
			}
			if(string.contains("longitude")) {
				String[] moresplit = string.split(":");
				lon = Double.parseDouble(moresplit[1]);
			}
		}
	}
}
