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
        setContentView(R.layout.login_layout);

        loginButton = (Button) findViewById(R.id.login);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(
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
    }

    private void login(View view) {
        if (usernameAndPasswordMatch(view)) {
            startActivity(new Intent(LoginActivity.this, StudentDashboard.class));
        }
    }
}

