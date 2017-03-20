package attendApp.attendApp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by drpac on 3/3/2017.
 */

/*MESSAGE LISTING
0: Login request                                    username password
1: Register Request                                 username password name is_teacher
2: Student Submit Attendance Request                username latitude longitude
3: Administrator Begin Attendance Period            username time classname
4: Administrator Cancel Attendance Period           username
5: Administrator Create New Class Request           username classname
6: Administrator Delete Class Request               username classname
7: Administrator Get Attendance Record Request      username classname
 */

/*RESPONSE CODE LISTING 0-99
0: General Success
1: The user is a Student
2: The user is a Teacher
3: Response to an administrator requesting an attendance record
/*ERROR CODE LISTING: 100-???
100: Failure to get a connection with the Raspberry Pi
101: Failure to read from Raspberry Pi
102: Bad request
103: Username and password did not match
104: Error retrieving usernames, probably fatal
105: No current active attendance period
106: Student not found in class
107: Teacher does not have access
108: There is an active attendance period
109: The class does not exist
110: Error?
111: Class DOES exist

200: Not working (new class)
201: Not working (delete class)
202: Not working (Get record)

Attendance records are read as, for example:
Date,Name1,Name2,Name3,Name4,Name5,Name6
1,1,1,1,1,1,1| <-- Note here that '|' replaces '\n' for the reason that the function to write to in our protocol means we are done writing.
2,1,1,1,1,1,1|     This can be replaced with another symbol but it is not advised.
3,1,1,1,1,1,1|
4,1,1,1,1,1,1
--
NOTE: There is no '--' in attendance records to be clear. There are no trailing ',' marks after the last entry on a line.
There are no new lines after the last entry in the file
 */

public class RaspberryPiCommunication {
    private String RASPBERRY_PI_IP = "10.0.0.12";
    private int RASPBERRY_PI_PORT = 1420;
    private Socket socket;
    private DataOutputStream DOS;
    private BufferedReader DIS;


    public RaspberryPiCommunication() {
        try {
            socket = new Socket(RASPBERRY_PI_IP, RASPBERRY_PI_PORT);
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to send a string to the Raspberry Pi.
     * @param toSend, the String to be sent to the Raspberry Pi
     * @return if sucessful, true. Else, false.
     */
    boolean sendDataToRaspberryPi(String toSend) {
        int tries = 0;
        while(DOS == null && !reinitialize()) {
            tries++;
            if (tries == 20) return false;
        }

        try {
            DOS.writeBytes(toSend + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Function to recieve data sent from the Raspberry Pi
     * @return An array of Strings with 2 pieces. The first piece is the "code" (0-255), and a string attached to say what the code means
     */
    String[] getDataFromRaspberryPi() {
		if(DIS == null) {
			return new String[] {"101","Failure to read from Raspberry Pi"};
		}

        String toReturn;
        try {
            toReturn = DIS.readLine();
        } catch (Exception e) {
            return new String[] {"101", "Failure to read from Raspberry Pi"};
        }

        String[] split = toReturn.split("&");
        try {
            Integer.parseInt(split[0]);
        } catch(Exception e) {
            return new String[] {"102","Bad response"};
        }
        return split;
    }

    /**
     * If a problem with the connection to the Raspberry Pi occurs then we run this function
     * @return If reinitialization is successful, true. Else, false.
     */
    private boolean reinitialize() {
        try {
            socket = new Socket(RASPBERRY_PI_IP, RASPBERRY_PI_PORT);
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(Exception e) {
            socket = null;
            DOS = null;
            DIS = null;
            return false;
        }
        return true;
    }

    /**
     * Function to get rid of the connection between this and the Raspberry Pi.
     */
    public void end() {
        if(socket==null) return;
        try {
            socket.close();
        } catch (IOException e) {
            return;
        }
        return;
    }
}
