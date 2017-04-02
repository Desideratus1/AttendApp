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

public class AdministratorNewClass extends AppCompatActivity {
	Button createClass;
	TextView createClassText;
	EditText className;
	String username;
	String response = "Unknown failure";
	RaspberryPiCommunication comm = new RaspberryPiCommunication();

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_new_class_layout);

		createClass = (Button) findViewById(R.id.create_class);
		createClassText = (TextView) findViewById(R.id.new_class_text);
		className = (EditText) findViewById(R.id.new_class);
		username = getIntent().getStringExtra("USERNAME");

		createClass.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						createClass(view);
					}
				});
	}

	private void createClass(View view) {

		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					comm = new RaspberryPiCommunication();
					Boolean b = comm.sendDataToRaspberryPi(
							new String[] {"5", username, className.getText().toString() }
					);
					if(!b) {
						response = "Data could not be sent";
						return;
					}

					String[] split = comm.getDataFromRaspberryPi();
					response = split[1];
				} catch (Exception e) {
					e.printStackTrace();
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

		createClassText.setText(response);
	}
}
