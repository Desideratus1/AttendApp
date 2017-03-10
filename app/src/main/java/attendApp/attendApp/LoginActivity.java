package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    RaspberryPiCommunication comm = new RaspberryPiCommunication();


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
                        //socket.close();
                        startActivity(n);
                    }
                });
    }

    /**
     * Function to check as to whether the username and password are valid
     * @param view
     * @return byte that tells us if they are a teacher, student, or the username and password pair is not valid
     */
    private byte usernameAndPasswordMatch(View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String[] split;

        Boolean b = comm.sendDataToRaspberryPi("0&" + username + "&" + password);
        if(!b) {
            loginFailed.setText("Data could not be sent");
            return 0;
        }

        split = comm.getDataFromRaspberryPi();
        int code = Integer.parseInt(split[0]);
        loginFailed.setText(split[1]);
        return (byte) code;
        /*
        TODO: REPLACE THIS CODE WITH DATA SENT TO AND FROM THE RASPBERRY PI
        //WHAT IS BEING SENT:
            Username, Password
          WHAT IS BEING RECIEVED:
            A byte. 1 means they are a student, 2 means they are a teacher. ANY OTHER NUMBER IS AN ERROR HERE.
         */
    }

    private void login(View view) {
        switch (usernameAndPasswordMatch(view)) {
            case 1:
                Intent n = new Intent(LoginActivity.this, StudentDashboard.class);
                n.putExtra("USERNAME", usernameField.getText().toString());
                n.putExtra("PASSWORD", passwordField.getText().toString());
                comm.end(); //Kill the socket connecting to the RASPBI
                startActivity(n);
                break;
            case 2:
                Intent k = new Intent(LoginActivity.this, AdministratorDashboard.class);
                k.putExtra("USERNAME", usernameField.getText().toString());
                k.putExtra("PASSWORD", passwordField.getText().toString());
                comm.end();
                startActivity(k);
                break;
        }
    }
}