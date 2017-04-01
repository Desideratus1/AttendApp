package attendApp.attendApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdministratorDisplayCSV extends AppCompatActivity {

	/**
	 * Called when the activity is first created.
	 */

	TableLayout t;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_csv_layout); //User views the content specified in display_csv_layout.xml
		t = (TableLayout) findViewById(R.id.table_displayer);
		String text = getIntent().getStringExtra("TEXT");

		String[] split = text.split("\n");
		ArrayList<String[]> splitt = new ArrayList<>();
		int rows = split.length;
		int columns = split[1].length() - split[1].replaceAll(",","").length();
		t.setPadding(50,50,50,50);

		for (int i = 0; i < rows; i++) {
			splitt.add(split[i].split(","));
			Log.d("text", split[i]);
			TableRow row= new TableRow(this);
			for(int j = 0; j < columns; j++) {
				TextView tv = new TextView(this);
				tv.setPadding(20,0,20,0);
				tv.setGravity(1);
				tv.setTextSize(18);
				tv.setText(splitt.get(i)[j]);
				row.addView(tv);
			}
			t.addView(row,i);
		}
	}
}