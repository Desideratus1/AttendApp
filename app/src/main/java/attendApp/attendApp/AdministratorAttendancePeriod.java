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
        attendanceText.setText("Success!");
        /*
        TODO: SEND THESE TO THE RASPBERRY PI
            WHAT WE SEND:
                INTEGER: TIME IN SECONDS
                STRING: NAME
            WHAT WE GET:
                BOOLEAN: successful?
        TODO: MAKE TEXT IF IT SUCCEEDS IN STARTING
         */
    }

    /**
     * Function to cancel the current running attendance period
     * Remember only 1 attendance period can be active
     * @param view
     */
    private void cancelPeriod(View view) {
        /*
        TODO: CANCEL THE PERIOD THROUGH RASPBERRY PI
            WHAT WE SEND: NOTHING
            WHAT WE GET: BOOLEAN
        TODO: MAKE TEXT IF IT SUCCEEDS IN ENDING
         */
    }


}

