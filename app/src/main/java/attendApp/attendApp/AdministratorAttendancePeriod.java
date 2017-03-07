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
        int timeInSeconds;
        try {
            timeInSeconds = Integer.parseInt(timeField.getText().toString())*60;
        } catch(Exception e) {
            attendanceText.setText("The minutes you supplied is unusable.");
            return;
        }
        String name = className.getText().toString();

        Boolean b = comm.sendDataToRaspberryPi(
                "3&" + username + "&" + timeField.getText().toString() + "&" + className.getText().toString()
        );
        if(!b) {
            attendanceText.setText("Data could not be sent");
            return;
        }

        String[] split = comm.getDataFromRaspberryPi();
        int code = Integer.parseInt(split[0]);
        if(code > 99) { //100+ is an error
            attendanceText.setText(split[1]);
            return;
        }

        attendanceText.setText("Success!");
    }

    /**
     * Function to cancel the current running attendance period
     * Remember only 1 attendance period can be active
     * @param view
     */
    private void cancelPeriod(View view) {
        String name = className.getText().toString();

        Boolean b = comm.sendDataToRaspberryPi(
                "4&" + username + "&cancel"
        );
        if(!b) {
            attendanceText.setText("Data could not be sent");
            return;
        }

        String[] split = comm.getDataFromRaspberryPi();
        int code = Integer.parseInt(split[0]);
        if(code > 99) { //100+ is an error
            attendanceText.setText(split[1]);
            return;
        }
        attendanceText.setText("Success!");
        /*
        TODO: ATTENDANCE PERIOD NEEDS ERRORS (ATTENDANCE PERIOD ALREADY BEGAN ETC.)
        */
    }


}

