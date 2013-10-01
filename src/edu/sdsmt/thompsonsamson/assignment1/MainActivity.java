package edu.sdsmt.thompsonsamson.assignment1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

	SharedPreferences _savedSearches;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_savedSearches = getSharedPreferences("tags", MODE_PRIVATE);
		
		// button click to save search information
		Button button_save = (Button) findViewById(R.id.button_save);
		button_save.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// add tag - inflate view
			}
		    
		});
		
		// button click to clear all tag information
		Button button_clear = (Button) findViewById(R.id.button_clear);
		button_clear.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// clear all tags - be sure to do alert
			}
			
		});
		
	}

}
