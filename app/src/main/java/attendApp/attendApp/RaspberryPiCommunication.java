package attendApp.attendApp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by drpac on 3/3/2017.
 */

/*General Protocol
1. The App sends data in the format "bytes&request_number&data&data...\n"
    The \n is a new line indicator and MUST BE AT THE END OF THE LINE. THIS CLASS HANDLES THIS ON THE APP'S SIDE
    & - which is ampersand if you can't read it - separates different datum
    So an example of this is: "0&username&password" - so it's a login request with 'password' and 'username' as data
2. The Server will process the request and send back a response in the format "bytes&code&responseCode"
	code is an integer that is listed below in RESPONSE CODE LISTING (and ERRORS)
	Response is just a string response we display on the App
	NOTE: This is different for Message 7, getting an attendance period record
		The specifics are at the bottom of this comment chain.


MESSAGE LISTING
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
1,1,1,1,1,1,1| <-- Note here that '|' replaces '\n' for the reason that '\n' tells the reciever that this is the end of the message.
2,1,1,1,1,1,1|     This can be replaced with another symbol but it is not advised.
3,1,1,1,1,1,1|
4,1,1,1,1,1,1

NOTE: There are no trailing ',' marks after the last entry on a line. There are no new lines after the last entry in the file
 */

public class RaspberryPiCommunication {
    private String RASPBERRY_PI_IP = "10.0.0.4";
    private int RASPBERRY_PI_PORT = 1420;
    private Socket socket;
    private DataOutputStream DOS;
    private DataInputStream DIS;
	private Encryption en = new Encryption();


    public RaspberryPiCommunication() {
        try {
            socket = new Socket();
			socket.connect(new InetSocketAddress(RASPBERRY_PI_IP,RASPBERRY_PI_PORT),3*1000);
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
        } catch(Exception e) {
        }
    }

    /**
     * Function to send a string to the Raspberry Pi.
     * @param toSend, the String to be sent to the Raspberry Pi
     * @return if sucessful, true. Else, false.
     */
    boolean sendDataToRaspberryPi(String[] toSend) {
        if(DOS == null) return false;

        try {
            StringBuilder ts = new StringBuilder();
            for(String str : toSend) {
                ts.append(str).append("&");
            }
            ts.deleteCharAt(ts.length()-1);
			byte[] b = en.encrypt(ts.toString());
			int length = b.length;
			DOS.writeInt(length);
			for(byte h : b) {
				DOS.writeByte(h);
			}
			DOS.flush();
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
			int size = DIS.readInt();
			Log.d("size of response", Integer.toString(size));
            byte[] h = new byte[size];
			int count = 0;
			while(count != size) {
				h[count++] = DIS.readByte();
			}
			toReturn = en.decrypt(h);
			Log.d("Returned", toReturn);
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
