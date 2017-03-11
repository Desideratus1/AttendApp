package raspPiSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    ServerSocket socketServer;
    Socket clientSocket;
    DataInputStream DIS;
    DataOutputStream DOS;
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

    public Server() throws IOException {
        socketServer = new ServerSocket(1420);
    }

    public void GetNextRequest() throws IOException {
        Socket clientSocket = socketServer.accept(); //This is blocking. It will wait.
        DIS = new DataInputStream(clientSocket.getInputStream());
        DOS = new DataOutputStream(clientSocket.getOutputStream());

        serveRequest(DIS.readUTF());
    }

    private void serveRequest(String in) throws IOException {
        String[] split = in.split("&");
        int code;
        try {
            code = Integer.parseInt(split[0]);
        } catch(Exception e) {
            DOS.writeUTF("102&Bad request");
            return;
        }
        switch(code) {
            case 0: //Logins
                if (split.length != 3) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                String username = split[1];
                String password = split[2];
                break;
            case 1: //Register
                if (split.length != 5) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                password = split[2];
                String name = split[3];
                String isTeacher = split[4];
                int isT;
                try {
                    isT = Integer.parseInt(isTeacher);
                } catch(Exception e) {
                    DOS.writeUTF("102&Bad request");
                }
                break;
            case 2: //Student submit attendance
                if (split.length != 2) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                break;
            case 3: //Begin attendance
                if (split.length != 4) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                String timeAsStr = split[2];
                String className = split[3];
                int realTimeInSeconds;

                try {
                    realTimeInSeconds = Integer.parseInt(timeAsStr);
                } catch(Exception e) {
                    DOS.writeUTF("102&Bad request");
                }

                    break;
            case 4: //Cancel attendance
                if (split.length != 2) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                break;
            case 5: //Create new class
                DOS.writeUTF("200&Create new class bad");
                break;
            case 6: //Delete class
                if (split.length != 3) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                className = split[2];
                break;
            case 7: //Get record
                if (split.length != 3) {
                    DOS.writeUTF("102&Bad request");
                    return;
                }
                username = split[1];
                className = split[2];
                break;

        }
    }
}