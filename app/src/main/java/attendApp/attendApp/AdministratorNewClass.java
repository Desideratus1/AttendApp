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
	RaspberryPiCommunication comm = new RaspberryPiCommunication();
	String username;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_delete_class_layout);

		createClass = (Button) findViewById(R.id.createClass);
		createClassText = (TextView) findViewById(R.id.delete_class_text);
		className = (EditText) findViewById(R.id.class_name);
		username = getIntent().getStringExtra("USERNAME");

		createClassText.setVisibility(View.INVISIBLE);

		createClassText.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						createClass(view);
					}
				});
	}

	private void createClass(View view) {

		Boolean b = comm.sendDataToRaspberryPi(
				"5&" + username + "&" + className.getText().toString()
		);
		if(!b) {
			createClassText.setText("Data could not be sent");
			return;
		}

		String[] response = comm.getDataFromRaspberryPi();
		createClassText.setText(response[1]);
	}
}
