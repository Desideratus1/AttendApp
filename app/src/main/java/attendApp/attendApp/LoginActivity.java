package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    EditText usernameField;
    EditText passwordField;
    TextView loginFailed;
    RaspberryPiCommunication comm;
    String[] split = {"0","Critical failure"};
    byte c = 2;
    String response;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout); //User views the content specified in login_layout.xml

        loginButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        loginFailed = (TextView) findViewById(R.id.loginFailed);
        loginFailed.setVisibility(View.VISIBLE);
        loginFailed.setText("");

        loginButton.setOnClickListener( //Whenever the login button is pressed
                new View.OnClickListener() {
                    public void onClick(View view) {
                        login(view);
                    }
                });
        registerButton.setOnClickListener( //Whenever the register button is pressed
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent n = new Intent(LoginActivity.this, RegisterActivity.class);
                        n.putExtra("USERNAME", usernameField.getText().toString());
                        n.putExtra("PASSWORD", passwordField.getText().toString());
                        startActivity(n);
                    }
                });
    }

    /**
     * Function to check as to whether the username and password are valid
     * @param view
     * @return byte that tells us if they are a teacher, student, or the username and password pair is not valid
     */
    private void usernameAndPasswordMatch(View view) {
		loginButton.setEnabled(false);
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    comm = new RaspberryPiCommunication();
                    String username = usernameField.getText().toString();
                    String password = passwordField.getText().toString();

                    Boolean b = comm.sendDataToRaspberryPi(
                            new String[] {"0", username, password });
                    if(!b) {
                        response = "Data could not be sent";
                        return;
                    }
                    split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);
                    response = split[1];
                    c = (byte) code;
                } catch (Exception e) {
                    response = "Networking errors; Unable to connect to server";
                }
            }
        });
		try {
			networkThread.start();
			networkThread.join(5*1000);
			if(networkThread.isAlive()) response = "Could not connect to the Server.";
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		loginButton.setEnabled(true);
	}

    private void login(View view) {
        usernameAndPasswordMatch(view);
        loginFailed.setText(response);
        switch (c) {
            case 0:
                Intent n = new Intent(LoginActivity.this, StudentDashboard.class);
                n.putExtra("USERNAME", usernameField.getText().toString());
                n.putExtra("PASSWORD", passwordField.getText().toString());
                comm.end(); //Kill the socket connecting to the RASPBI
                startActivity(n);
                break;
            case 1:
                Intent k = new Intent(LoginActivity.this, AdministratorDashboard.class);
                k.putExtra("USERNAME", usernameField.getText().toString());
                k.putExtra("PASSWORD", passwordField.getText().toString());
                comm.end();
                startActivity(k);
                break;
        }
    }
}