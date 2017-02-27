package com.example.drpac.attendapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameField;
    EditText passwordField;
    ViewGroup rootContainer;
    String username;
    String password;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout); //User views the content specified in login_layout.xml

        loginButton = (Button) findViewById(R.id.login);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener( //Whenever the login button is pressed
                new View.OnClickListener() {
                    public void onClick(View view) {
                        login(view);
                    }
                });
    }

    private boolean usernameAndPasswordMatch(View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        return (username.equals("admin") && password.equals("admin"));
        //TODO: REPLACE THIS CODE WITH DATA SENT TO AND FROM THE RASPBERRY PI
    }

    private void login(View view) {
        if (usernameAndPasswordMatch(view)) {
            startActivity(new Intent(LoginActivity.this, StudentDashboard.class));
            //startActivity(new Intent(LoginActivity.this, StudentDashboard.class));
            //TODO: We should be able to tell who is and who is not an administrator and send them to the appropriate screen
        }
    }
}

