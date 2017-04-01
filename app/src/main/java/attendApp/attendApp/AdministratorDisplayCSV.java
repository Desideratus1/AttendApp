package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AdministratorDisplayCSV extends AppCompatActivity {

	/**
	 * Called when the activity is first created.
	 */

	TextView t;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_csv_layout); //User views the content specified in display_csv_layout.xml
		t = (TextView) findViewById(R.id.table_displayer);
		String text = getIntent().getStringExtra("TEXT").replaceAll(",", "\t");
		t.setText(text);
	}
}