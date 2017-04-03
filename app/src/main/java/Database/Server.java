package Database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;

public class Server {

    private ServerSocket socketServer;
	private LoginsCSV logins;

	private String currentActiveAttendancePeriodClassName = null;
	private attendancePeriodCSV activePeriod = null; //THe current active attendance period
	private static double lat = 0;
	private static double lon = 0;

	Encryption en = new Encryption();

    public Server() throws IOException, InterruptedException {
        socketServer = new ServerSocket(1420);
	File file = new File("un");
	if(!file.exists()) file.createNewFile();
        logins = new LoginsCSV("un");
		getGPS();
    }

    public void getNextRequest() throws IOException {
        final Socket clientSocket = socketServer.accept(); //This is blocking. It will wait for a new request
        Thread thr = new Thread(new Runnable() {
			
			@Override
			public void run() { //Once a new request is recieved we make a new thread and run it
				DataOutputStream DOS;
				DataInputStream DIS;
				try {
					DOS = new DataOutputStream(clientSocket.getOutputStream()); //If this gives an error ignore it
					DIS = new DataInputStream(clientSocket.getInputStream());
					int val = DIS.readInt();
					byte[] h = new byte[val];
					int count = 0;
					while(count != val) {
						h[count] = DIS.readByte();
						count++;
					}
					String read = en.decrypt(h);
					System.out.println(read);
					serveRequest(read, DOS, DIS);
				} catch (IOException e) {
					System.out.println("Failure within Thread. Shutting it down...");
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
    private void serveRequest(String in, DataOutputStream DOS, DataInputStream DIS) throws IOException {
    	if(in == null) return;
		checkAttendancePeriod(); //An active check to determine if the attendance period has expired
        String[] split = in.split("&");
        int code;
        try {
            code = Integer.parseInt(split[0]);
        } catch(Exception e) {
			sendData(DOS, 102, "Bad request");
            return;
        }
        switch(code) {
            case 0: //Logins ----------------------
            	System.out.println("Serving login request...");
                if (split.length != 3) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                
                String username = split[1];
                String password = split[2];
                
                User user = logins.getUser(username);
                
                if(user == null) {
					sendData(DOS, 103, "Username and password do not match");
                	return;
                }
                
                if(username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                	if(user.isTeacher()) sendData(DOS, 1, "This user is a teacher");
                	else sendData(DOS, 0, "This user is a student");
                } else {
                	sendData(DOS, 103, "Username and password do not match");
                }
                break;
            case 1: //Register ------------------
            	System.out.println("Serving register request...");
                if (split.length != 5) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                username = split[1];
                password = split[2];
                String name = split[3];
                String isTeacher = split[4];
                try {
                    Integer.parseInt(isTeacher);
                } catch(Exception e) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                logins.addUser(username, password, name, isTeacher);
                logins.write();

				sendData(DOS, 0, "Success!");
                break;
            case 2: //Student submit attendance --------------------
            	System.out.println("Servicing attendance submission...");
                if (split.length != 4) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                username = split[1];
                String lat = split[2];
                String lon = split[3];
                
                user = logins.getUser(username);
                if(user == null) {
					sendData(DOS, 104, "Error retrieving account");
                	return;
                }
                
                if(tooFarAway(Double.parseDouble(lat), Double.parseDouble(lon))) {
					sendData(DOS, 110, "You're too far away!");
                	return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
					sendData(DOS, 105, "No active attendance period");
                	return;
                }
                
                boolean b = activePeriod.submitAttendance(user.getName());
                if(b) sendData(DOS, 0, "Attendance recieved!");
                else sendData(DOS, 106, "Student not found in class");
                
                break;
            case 3: //Begin attendance period ----------------------
            	System.out.println("Servicing attendance period request...");
            	if(activePeriod != null) {
					sendData(DOS, 108, "Active attendance period");
            		return;
            	}
            	
                if (split.length != 4) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                username = split[1];
                String timeAsStr = split[2];
                String className = split[3];
                int realTimeInSeconds;
                
                try {
                    realTimeInSeconds = Integer.parseInt(timeAsStr);
                } catch(Exception e) {
					sendData(DOS, 102, "Bad request");
					return;
                }

				user = logins.getUser(username);
				if(user == null) {
					sendData(DOS, 104, "Error retrieving account");
					return;
				}

				activePeriod = new attendancePeriodCSV("records_for_" + className + ".txt", realTimeInSeconds);
				if(!activePeriod.exists()) {
					activePeriod = null;
					sendData(DOS, 109, "Class does not exist");
					return;
				}
				currentActiveAttendancePeriodClassName = className;
				activePeriod.beginAttendancePeriod();
				sendData(DOS, 0, "Success!");
				
				System.out.println("Attendance period for class \" " + className + "\" has started. It");
				System.out.println("It will end in " + realTimeInSeconds + " seconds.");

                break;
            case 4: //Cancel attendance --------------------
            	System.out.println("Serving attendance period cancellation request...");
                if (split.length != 2) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                
                if(currentActiveAttendancePeriodClassName == null) {
					sendData(DOS, 105, "No active attendance period");
                	return;
                }
                
                username = split[1];
                user = logins.getUser(username);
                
                if(user == null) {
					sendData(DOS, 104, "Error retrieving account");
                	return;
                }

				if (user.hasAccessToClass(currentActiveAttendancePeriodClassName)) {
					activePeriod.cancelAttendancePeriod();
					sendData(DOS, 0, "Success!");
					return;
				}

				sendData(DOS, 107, "You don't have access to this class");

				break;
            case 5: //Create new class -------------------------
            	System.out.println("Serving create new class request...");
            	if (split.length != 3) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
            	username = split[1];
            	className = split[2];
            	
            	user = logins.getUser(username);
                
                if(user == null) {
					sendData(DOS, 104, "Error retrieving account");
                	return;
                }
                
                File newClass = new File("records_for_" + className + ".txt");
                if(newClass.exists()) {
					sendData(DOS, 111, "This file exists, we can not make a new class out of it. Delete this class first");
             	   return;
                }
                
               ArrayList<String> f = extractDataForNewClass(className + ".txt");
               if(f == null) {
				   sendData(DOS, 112, "File does not exist");
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
				sendData(DOS, 0, "Success");
                break;
            case 6: //Delete class request ---------------------
            	System.out.println("Serving delete class request...");
                if (split.length != 3) {
					sendData(DOS, 102, "Bad request");
                    return;
                }
                username = split[1];
                className = split[2];

				if(activePeriod != null) {
					sendData(DOS, 108, "There is an active attendance period");
					return;
				}

				user = logins.getUser(username);
				if(user == null) {
					sendData(DOS, 104, "Failure to retrieve account");
					return;
				}

				if(user.hasAccessToClass(className)) {
					File file = new File(className);
					if(file.exists()) {
						file.delete();
						sendData(DOS, 0, "Success!");
						return;
					} else {
						sendData(DOS, 109, "The class doesn't exist!");
						return;
					}
				}
				sendData(DOS, 107, "You do not have access!");
                break;
            case 7: //Get record ---------------------------------
            	System.out.println("Serving get record request...");
                if (split.length != 3) {
					sendData(DOS, 102, "Bad request");
                    return;
                }

                username = split[1];
                className = split[2];


				if(currentActiveAttendancePeriodClassName != null && className.equals(currentActiveAttendancePeriodClassName)) {
					sendData(DOS, 108, "The class you want is currently active!");
					return;
				}

				user = logins.getUser(username);
				if(user == null) {
					sendData(DOS, 104, "Failure to retrieve account");
					return;
				}

				if(!user.hasAccessToClass(className)) {
					sendData(DOS, 107, "You don't have access to this class!");
					return;
				}

				attendancePeriodCSV classDat = new attendancePeriodCSV("records_for_" + className + ".txt",100);
				if(!classDat.exists()) {
					sendData(DOS, 109, "The class doesn't exist!");
				}
				
                String response = classDat.toString();
                sendData(DOS, 3, response); //This has to be this kind of call
                break;

        }
    }

	public static boolean tooFarAway(double lat2, double lon2) {
		if(lat2 == lat && lon2 == lon) return false;
	    double earthRadius = 6371000; //meters
	    double dLat = Math.toRadians(lat2-lat);
	    double dLng = Math.toRadians(lon2-lon);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    float dist = (float) (earthRadius * c);
		return (dist > 100);//0.00473485);
	    }

	private void checkAttendancePeriod() {
		if(activePeriod == null) return;
		else try {
			if(activePeriod.endAttendancePeriod()) {
				currentActiveAttendancePeriodClassName = null;
				activePeriod = null;
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

	void sendData(DataOutputStream DOS, int code, String resp) {
		try {
			byte[] r = en.encrypt(code + "&" + resp);
			DOS.writeInt(r.length);
			for(byte a : r) {
				DOS.writeByte(a);
			}
			DOS.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
