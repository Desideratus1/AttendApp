package attendApp.attendApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by drpac on 2/20/2017.
 */

public class AdministratorDeleteClass extends AppCompatActivity {
    Button deleteClass;
    TextView deleteClassText;
    EditText className;
    RaspberryPiCommunication comm = new RaspberryPiCommunication();
    String username;
    String response = "Unknown failure";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: GET USERNAME/PASSWORD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_class_layout);

        deleteClass = (Button) findViewById(R.id.delete_class);
        deleteClassText = (TextView) findViewById(R.id.delete_class_text);
        className = (EditText) findViewById(R.id.class_name);
        username = getIntent().getStringExtra("USERNAME");

        deleteClassText.setVisibility(View.INVISIBLE);

        deleteClass.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        deleteClass(view);
                    }
                });
    }

    private void deleteClass(View view) {
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean b = comm.sendDataToRaspberryPi(
                            "6&" + username + "&" + className.getText().toString()
                    );
                    if(!b) {
                        deleteClassText.setText("Data could not be sent");
                        return;
                    }

                    String[] response = comm.getDataFromRaspberryPi();
                    deleteClassText.setText(response[1]);
                } catch (Exception e) {
                    e.printStackTrace();
					response = "Networking errors; Unable to connect to server";
                }
            }
        });
        networkThread.start();

        try {
            networkThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        deleteClassText.setText(response);
    }
}
