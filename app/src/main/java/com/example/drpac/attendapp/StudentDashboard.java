package com.example.drpac.attendapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentDashboard extends AppCompatActivity {

    Button submitAttendance;
    TextView success;
    TextView fail;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard_layout);

        submitAttendance = (Button) findViewById((R.id.submit_attendance));
        success = (TextView) findViewById(R.id.attendance_success);
        success.setVisibility(View.INVISIBLE);
        fail = (TextView) findViewById(R.id.attendance_fail);
        fail.setVisibility(View.INVISIBLE);

        submitAttendance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        success.setVisibility(View.INVISIBLE);
                        fail.setVisibility(View.INVISIBLE);
                        if(submitAttendance("admin")) {
                            success.setVisibility(View.VISIBLE);
                            fail.setVisibility(View.INVISIBLE);
                        }
                        else {
                            fail.setVisibility(View.VISIBLE);
                            success.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    //Return true if successfully submitted
    private boolean submitAttendance(String username) {
        //REPLACE THIS WITH SENDING IT TO RSPBPI
        //YOU WILL SEND JUST THE NAME
        return (username.equals("admin"));
    }
}

