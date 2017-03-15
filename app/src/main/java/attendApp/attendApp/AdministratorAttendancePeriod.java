package attendApp.attendApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AdministratorAttendancePeriod extends AppCompatActivity {

    //Put buttons, strings, others here
    EditText timeField;
    EditText className;
    Button beginAttendancePeriod;
    Button cancelAttendancePeriod;
    TextView attendanceText;
    RaspberryPiCommunication comm = new RaspberryPiCommunication();
    String username;
    String response = "Unknown error";
    boolean wait = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_attend_period_layout);

        timeField = (EditText) findViewById(R.id.timeField);
        className = (EditText) findViewById(R.id.classList);
        beginAttendancePeriod = (Button) findViewById(R.id.beginAttendancePeriod);
        cancelAttendancePeriod = (Button) findViewById(R.id.cancelAttendancePeriod);
        attendanceText = (TextView) findViewById(R.id.attendancePeriodTexts);
        username = getIntent().getStringExtra("USERNAME");

        beginAttendancePeriod.setOnClickListener( //Whenever the login button is pressed
                new View.OnClickListener() {
                    public void onClick(View view) {
                        beginPeriod(view);
                    }
                });

        cancelAttendancePeriod.setOnClickListener( //Whenever the login button is pressed
                new View.OnClickListener() {
                    public void onClick(View view) {
                        cancelPeriod(view);
                    }
                });

    }

    /**
     * Function to begin an attendance period
     * Remember only 1 attendance period can be active at a time
     * @param view
     */
    private void beginPeriod(View view) {
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(wait);
                    wait = true;
                    int timeInSeconds;
                    try {
                        timeInSeconds = Integer.parseInt(timeField.getText().toString())*60;
                    } catch(Exception e) {
                        response = "The minutes you supplied is unusable.";
                        wait = false;
                        return;
                    }

                    Boolean b = comm.sendDataToRaspberryPi(
                            "3&" + username + "&" + timeInSeconds + "&" + className.getText().toString()
                    );
                    if(!b) {
                        response = "Data could not be sent";
                        wait = false;
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);
                    if(code > 99) { //100+ is an error
                        response = split[1];
                        wait = false;
                        return;
                    }

                    attendanceText.setText("Success!");
                    wait = false;
                } catch (Exception e) {
                    e.printStackTrace();
					response = "Networking errors; Unable to connect to server";
                }
            }
        });
        while(wait);
        networkThread.start();
        attendanceText.setText(response);
    }

    /**
     * Function to cancel the current running attendance period
     * Remember only 1 attendance period can be active
     * @param view
     */
    private void cancelPeriod(View view) {
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(wait);
                    wait = true;
                    Boolean b = comm.sendDataToRaspberryPi(
                            "4&" + username
                    );
                    if(!b) {
                        response = "Data could not be sent";
                        wait = false;
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);
                    if(code > 99) { //100+ is an error
                        response = split[1];
                        wait = false;
                        return;
                    }
                    response = "Success!";
                    wait = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        while(wait);
        networkThread.start();
        attendanceText.setText(response);
    }


}

