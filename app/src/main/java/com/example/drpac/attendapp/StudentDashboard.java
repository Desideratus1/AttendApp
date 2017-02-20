package com.example.drpac.attendapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StudentDashboard extends AppCompatActivity {

    Button loginButton;
    EditText usernameField;
    ViewGroup rootContainer;
    TextView success;
    TextView fail;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard_layout);

        usernameField = (EditText) findViewById(R.id.username);
        success = (TextView) findViewById(R.id.attendance_success);
        success.setVisibility(View.INVISIBLE);
        fail = (TextView) findViewById(R.id.attendance_fail);
        fail.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        success.setVisibility(View.INVISIBLE);
                        fail.setVisibility(View.INVISIBLE);
                        String username = usernameField.getText().toString();
                        if(submitAttendance(username)) {
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
        if (username.equals("admin")) return true;
        else return false;
    }
}

