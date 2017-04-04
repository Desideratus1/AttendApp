package attendApp.attendApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by drpac on 2/20/2017.
 */

public class AdministratorNewClass extends AppCompatActivity {
	Button createClass;
	TextView createClassText;
	EditText className;
	ProgressBar bar;
	String username;
	RaspberryPiCommunication comm;

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
		bar = (ProgressBar) findViewById(R.id.bar);
		bar.setVisibility(View.INVISIBLE);
		username = getIntent().getStringExtra("USERNAME");

		createClass.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						createClass(view);
					}
				});
	}

	/**
	 * Attempts to create a new class with the current method.
	 * @param view
	 */
	private void createClass(View view) {

		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bar.setVisibility(View.VISIBLE);
						createClassText.setText("");
					}
				});
				try {
					comm = new RaspberryPiCommunication();
					Boolean b = comm.sendDataToRaspberryPi(
							new String[] {"5", username, className.getText().toString() }
					);
					if(!b) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bar.setVisibility(View.INVISIBLE);
								createClassText.setText(new String("Data could not be sent"));
							}
						});
						return;
					}

					final String[] split = comm.getDataFromRaspberryPi();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							createClassText.setText(split[1]);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bar.setVisibility(View.INVISIBLE);
							createClassText.setText(new String("Networking error, unable to contact server"));
						}
					});
				}
			}
		});
		networkThread.start();
	}
}
