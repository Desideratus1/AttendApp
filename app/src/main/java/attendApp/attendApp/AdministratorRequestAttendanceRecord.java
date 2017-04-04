package attendApp.attendApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by drpac on 2/20/2017.
 */

public class AdministratorRequestAttendanceRecord extends AppCompatActivity {
    Button requestRecord;
    TextView requestRecordText;
    EditText className;
	ProgressBar bar;
    RaspberryPiCommunication comm;
    String username;
	String file = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: GET USERNAME/PASSWORD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_request_record_layout);

        requestRecord = (Button) findViewById(R.id.request_record);
        requestRecordText = (TextView) findViewById(R.id.request_record_text);
        className = (EditText) findViewById(R.id.class_name);
		bar = (ProgressBar) findViewById(R.id.bar);
		bar.setVisibility(View.INVISIBLE);
        username = getIntent().getStringExtra("USERNAME");

        requestRecord.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        requestRecord(view);
                    }
                });
    }

	/**
	 * Records a record with the specified class
	 * @param view
	 */
    private void requestRecord(View view) {

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.VISIBLE);
							requestRecordText.setText("");
						}
					});
                    comm = new RaspberryPiCommunication();
                    Boolean b = comm.sendDataToRaspberryPi(
                            new String[] {"7", username, className.getText().toString() }
                    );
                    if(!b) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								requestRecordText.setText(new String("Data could not be sent"));
							}
						});
                        return;
                    }

                    String[] split = comm.getDataFromRaspberryPi();
                    int code = Integer.parseInt(split[0]);

                    if(code != 3) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								requestRecordText.setText(new String("The response we recieved from the server was not bad. Not your fault!"));
							}
						});
						return;
					} else file = split[1];

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
						}
					});

					Intent n = new Intent(AdministratorRequestAttendanceRecord.this, AdministratorDisplayCSV.class);
					n.putExtra("TEXT", file);
					file = "";
					startActivity(n);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							requestRecordText.setText(new String("Success!"));
						}
					});
                } catch (Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							requestRecordText.setText(new String("Networking error; Unable to connect to server"));
						}
					});
                }
            }
        });
		networkThread.start();
    }


}
