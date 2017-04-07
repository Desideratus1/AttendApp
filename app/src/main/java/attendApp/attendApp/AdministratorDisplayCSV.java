package attendApp.attendApp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
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

/**
 * A class that will display the attendance records of the given class, if it exists.
 * It is displayed in the regular table-style with the ability to scroll to view more of the screen.
 */
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
		int columns = split[0].length() - split[0].replaceAll(",","").length();
		t.setPadding(50,50,50,50);
		String table[][] = new String[columns][rows+1];

		for (int a = 0; a < rows; a++) {
			splitt.add(split[a].split(","));
			String l = "";
			int tot = 0;
			for (int b = 0; b < columns; b++) {
				table[b][a] = splitt.get(a)[b];
				l = l + " " + table[b][a];
			}
		}

		for(int q = 0; q < columns; q++) {
			int l = 0;
			for(int w = 0; w < rows; w++) {
				if(w != 0 && q != 0) l = l + Integer.parseInt(table[q][w]);
			}
			if(q == 0) table[q][rows] = "Totals";
			else table[q][rows] = Integer.toString(l);
		}

		for (int i = 0; i < columns; i++) {
			TableRow row= new TableRow(this);
			for(int j = 0; j < rows+1; j++) {
				TextView tv = new TextView(this);
				tv.setPadding(20,0,20,0);
				tv.setGravity(1);
				tv.setTextSize(18);
				if(j == 0 || i == 0) {
					tv.setTextColor(Color.argb(255, 50, 100, 100));
					tv.setTextSize(20);
				}
				tv.setText(table[i][j]);
				row.addView(tv);
			}
			t.addView(row,i);
		}
	}
}