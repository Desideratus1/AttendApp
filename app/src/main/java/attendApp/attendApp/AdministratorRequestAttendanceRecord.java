package attendApp.attendApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by drpac on 2/20/2017.
 */

public class AdministratorRequestAttendanceRecord extends AppCompatActivity {
    Button requestRecord;
    TextView requestRecordText;
    EditText className;
    EditText email;
    RaspberryPiCommunication comm = new RaspberryPiCommunication();
    String username;
    String response = "Unknown failure";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: GET USERNAME/PASSWORD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_class_layout);

        requestRecord = (Button) findViewById(R.id.request_record);
        requestRecordText = (TextView) findViewById(R.id.request_record_text);
        className = (EditText) findViewById(R.id.class_name);
        email = (EditText) findViewById(R.id.request_record_email);
        username = getIntent().getStringExtra("USERNAME");

        requestRecord.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        requestRecord(view);
                    }
                });
    }

    private void requestRecord(View view) {

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String em = email.getText().toString();
                    Boolean b = comm.sendDataToRaspberryPi(
                            "7&" + username + "&" + className.getText().toString()
                    );
                    if(!b) {
                        response = "Data could not be sent";
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);
                    if(code > 99) { //100+ is an error
                        response = split[1];
                        return;
                    }
                    response = "Success!";
                } catch (Exception e) {
                    e.printStackTrace();
					response = "Networking errors; Unable to connect to server";
                }
            }
        });
        networkThread.start();

        try {
            networkThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requestRecordText.setText(response);
    }
}
