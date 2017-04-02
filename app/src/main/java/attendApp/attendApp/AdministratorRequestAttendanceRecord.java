package attendApp.attendApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by drpac on 2/20/2017.
 */

public class AdministratorRequestAttendanceRecord extends AppCompatActivity {
    Button requestRecord;
    TextView requestRecordText;
    EditText className;
    EditText email;
    RaspberryPiCommunication comm;
    String username;
    String response = "Unknown failure";
	String file = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: GET USERNAME/PASSWORD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_request_record_layout);

        requestRecord = (Button) findViewById(R.id.request_record);
        requestRecordText = (TextView) findViewById(R.id.request_record_text);
        className = (EditText) findViewById(R.id.class_name);
        username = getIntent().getStringExtra("USERNAME");

        requestRecord.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        requestRecord(view);
						if(!file.equals("")) {
							Intent n = new Intent(AdministratorRequestAttendanceRecord.this, AdministratorDisplayCSV.class);
                            n.putExtra("TEXT", file);
                            file = "";
                            startActivity(n);
						}
                    }
                });
    }

    private void requestRecord(View view) {

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    comm = new RaspberryPiCommunication();
                    Boolean b = comm.sendDataToRaspberryPi(
                            new String[] {"7", username, className.getText().toString() }
                    );
                    if(!b) {
                        response = "Data could not be sent";
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);

                    if(code != 3) {
						response = "The response we recieved from the server was not bad. Not your fault!";
						return;
					} else file = split[1];

					//See RaspberryPiCommunication for information regarding how an attendance record is split.
					/*ArrayList<String[]> disp = new ArrayList<String[]>();
					String[] d = split[1].split("|");//Split the file by new line representations.
					for(String str : d) {
						disp.add(str.split(","));//Split the lines by their commas
					}*/


					response = "Success!";
                } catch (Exception e) {
                    e.printStackTrace();
					response = "Networking errors; Unable to connect to server";
                }
            }
        });
        try {
            networkThread.start();
            networkThread.join(5*1000);
            if(networkThread.isAlive()) response = "Could not connect to the Server.";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requestRecordText.setText(response);
    }


}
