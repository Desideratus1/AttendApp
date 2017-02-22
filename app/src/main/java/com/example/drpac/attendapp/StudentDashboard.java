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
                        //Set both the success text and fail text to invisible
                        success.setVisibility(View.INVISIBLE);
                        fail.setVisibility(View.INVISIBLE);
                        if(submitAttendance("admin")) {
                            //TODO: replace "admin" with the full name of their account.
                            //If their full name exists, mark them as here and give the "SUCCESS" text
                            success.setVisibility(View.VISIBLE);
                            fail.setVisibility(View.INVISIBLE);
                        }
                        else {
                            //Otherwise they failed but...
                            //TODO: Maybe tell them why it failed? Too far away?
                            fail.setVisibility(View.VISIBLE);
                            success.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    //Return true if successfully submitted
    private boolean submitAttendance(String username) {
        //TODO: Replace this with data sent to and from the Raspberry Pi
        return (username.equals("admin"));
    }
}

