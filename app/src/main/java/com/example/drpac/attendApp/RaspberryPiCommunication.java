package com.example.drpac.attendApp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by drpac on 3/3/2017.
 */

/*RESPONSE CODE LISTING 0-99
1: The user is a Student
2: The user is a Teacher
/*ERROR CODE LISTING: 100-???
100: Failure to get a connection with the Raspberry Pi
101: Failure to read from Raspberry Pi
 */

public class RaspberryPiCommunication {
    String RASPBERRY_PI_IP = "";
    int RASPBERRY_PI_PORT = 0;
    Socket socket;
    DataOutputStream DOS;
    DataInputStream DIS;


    public RaspberryPiCommunication() {
        try {
            socket = new Socket(RASPBERRY_PI_IP, RASPBERRY_PI_PORT);
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
        } catch(Exception e) {
            socket = null;
            DOS = null;
            DIS = null;
        }
    }

    /**
     * Function to send a string to the Raspberry Pi.
     * @param toSend, the String to be sent to the Raspberry Pi
     * @return if sucessful, true. Else, false.
     */
    boolean sendDataToRaspberryPi(String toSend) {
        int tries = 0;
        while(DOS == null && !reinitialize()) tries++;
        if(tries == 20) return false;

        try {
            DOS.writeUTF(toSend);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Function to recieve data sent from the Raspberry Pi
     * @return An array of Strings with 2 pieces. The first piece is the "code" (0-255), and a string attached to say what the code means
     */
    String[] getDataFromRaspberryPi() {
        int tries = 0;
        while(DIS == null && !reinitialize()) tries++;
        if(tries == 20) return new String[] {"100","Failure to get Raspberry Pi"};

        String toReturn;
        try {
            toReturn = DIS.readUTF();
        } catch (Exception e) {
            return new String[] {"101", "Failure to read from Raspberry Pi"};
        }

        String[] split = toReturn.split("&");
        try {
            int code = Integer.parseInt(split[0]);
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
            DIS = new DataInputStream(socket.getInputStream());
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
