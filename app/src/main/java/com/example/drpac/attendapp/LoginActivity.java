package com.example.drpac.attendapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameField;
    EditText passwordField;
    ViewGroup rootContainer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
    }

    private boolean usernameAndPasswordMatch(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) return true;
        else return false;
    }

    private void login(String username, String password) {

    }

    public void changeSceneToStudentDashboard(View view) {

    }
}

