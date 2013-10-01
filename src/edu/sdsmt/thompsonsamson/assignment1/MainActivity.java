package edu.sdsmt.thompsonsamson.assignment1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

public class MainActivity extends Activity {

	SharedPreferences _savedSearches;
	EditText _objQuery;
	EditText _objTag;
	TableLayout _tagListTableLayout;
	String _editTag;
	
	/* Method:	onCreate
	 * Purpose:	Main entry point for activity. Called when application is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// setup shared preferences object
		_savedSearches = getSharedPreferences("tags", MODE_PRIVATE);
		
		// define the text fields
		_objQuery = (EditText) findViewById(R.id.text_query);
		_objTag = (EditText) findViewById(R.id.text_tag);
		_tagListTableLayout = (TableLayout) findViewById(R.id.TableLayout2);
		_editTag = null;
		
		// button click to save search information
		Button button_save = (Button) findViewById(R.id.button_save);
		button_save.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveTag();
			}
		    
		});
		
		// button click to clear all tag information
		Button button_clear = (Button) findViewById(R.id.button_clear);
		button_clear.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendClearTagsAlert();
			}
			
		});

		// when the app starts, list any current searches
		getTaggedSearches();
	}

	/* Method:	saveTag
	 * Purpose:	Adds the query/tag information to the saved preferences and inflates a view
	 */
	private void saveTag()
	{
		// get text fields values
		String query = _objQuery.getText().toString();
		String tag = _objTag.getText().toString();
		
		// check for empty query or tag
		if( query.length() == 0 || tag.length() == 0)
		{
			sendCheckInputAlert();
			return;
		}
		
		// add the tag to saved preferences
		SharedPreferences.Editor prefEditor = _savedSearches.edit();
		if (_editTag != null && _editTag != tag)
		{
			prefEditor.remove(_editTag);
			_editTag = null;
		}
		prefEditor.putString( tag, query );
		prefEditor.commit();
		
		
		// refresh the search tags
		getTaggedSearches();
		
		// clear the text fields
		_objQuery.setText("");
		_objTag.setText("");
				
		return;
	}

	private void sendClearTagsAlert() {
		// build the alert dialog
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(R.string.alert_clear_title);
		alertBuilder.setMessage(R.string.alert_clear_message);
		alertBuilder.setCancelable(true);
		
		// listener for confirmation
		DialogInterface.OnClickListener deleteAllTagsConfirmButtonListener = new DialogInterface.OnClickListener() {						
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteAllTags();
			}
		};
		
		alertBuilder.setPositiveButton(R.string.alert_yes, deleteAllTagsConfirmButtonListener);
		alertBuilder.setNegativeButton(R.string.alert_cancel, null);

		// show the alert
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}
	
	private void sendDeleteTagAlert(final String tag) {
		// build the alert dialog
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(R.string.alert_delete_title);
		alertBuilder.setMessage(R.string.alert_delete_message);
		alertBuilder.setCancelable(true);

		// listener for confirmation
		DialogInterface.OnClickListener deleteTagConfirmButtonListener = new DialogInterface.OnClickListener() {						
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteTag(tag);
			}
		};		

		alertBuilder.setPositiveButton(R.string.alert_yes, deleteTagConfirmButtonListener);
		alertBuilder.setNegativeButton(R.string.alert_cancel, null);
		
		// show the alert
		AlertDialog alert = alertBuilder.create();
		alert.show();		
	}
	
	private void sendCheckInputAlert() {
		// build the alert dialog
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(R.string.alert_save_title);
		alertBuilder.setMessage(R.string.alert_save_message);
		alertBuilder.setCancelable(true);
		alertBuilder.setNegativeButton(R.string.alert_ok, null);
		
		// show the alert
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}
	
	
	/*
	 * 
	 */
	private void getTaggedSearches ()
	{	
		// clear any existing view
		_tagListTableLayout.removeAllViews();
		
		// get array of keys from saved preferences and sort them
		String[] savedTags = _savedSearches.getAll().keySet().toArray(new String[0]);
		Arrays.sort(savedTags, String.CASE_INSENSITIVE_ORDER);
		
		// set the inflater
		LayoutInflater inflater = getLayoutInflater();
		
		// loop through tags in the array and build views from them
		for( String tag : savedTags )
		{
			// inflate the tagged search view
			View tagRowView = inflater.inflate(R.layout.tagged_search, null);

			// update the button text to tag name
			final Button tagButton = (Button) tagRowView.findViewById(R.id.button_tag);			
			tagButton.setText(tag);
						
			tagButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					displaySearch(tagButton.getText().toString());
				}
			});
			
			Button editButton = (Button) tagRowView.findViewById(R.id.button_edit);
			editButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					editTag(tagButton.getText().toString());
				}
			});	
			
			Button deleteButton = (Button) tagRowView.findViewById(R.id.button_delete);
			deleteButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendDeleteTagAlert(tagButton.getText().toString());
				}
			});	
			
			// add the view to the layout
			_tagListTableLayout.addView(tagRowView);
		}
		
	}

	public void displaySearch(String tag) {

		try {

			String query = _savedSearches.getString(tag, null);
			String url = getString(R.string.search_url) + URLEncoder.encode(query, "UTF-8");
			Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity (webIntent);
			Log.v("search",query);
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	
	
	public void editTag(String tag) {
		// get query from tag pairing in shared preferences
		String query = _savedSearches.getString(tag, null);

		// update text fields to display stored information
		_objTag.setText(tag);
		_objQuery.setText(query);
		
		_editTag = tag;
	}
	
	private void deleteTag(String tag) {
		
		SharedPreferences.Editor prefEditor = _savedSearches.edit();
		prefEditor.remove(tag);
		prefEditor.commit();
		
		getTaggedSearches();
		
	}

	// clear all tags - be sure to do alert
	private void deleteAllTags()
	{
		SharedPreferences.Editor prefEditor = _savedSearches.edit();
		prefEditor.clear();
		prefEditor.commit();
		
		_tagListTableLayout.removeAllViews();	
	}


}
