/******************************************************************************
 *	Project:	Assignment 1 - Twitter Tag Search
 *	Authors:	Andrew Thompson & Scott Samson
 *	Date:		10/1/2013
 *	Purpose:	Android application to save twitter tags and call searches.
 *				Items used: SharedPreferences, Intents, Anonymous Inner Classes,
 *				Inflating views, and layout design.
 *	Repository:	https://github.com/amthompson/CSC421-Assignment1
 *	Timeline:	9/28/2013:	Initial layout design (AT)
 *				10/1/2013:	Program functionality (AT&SS)
 *				10/2/2013:	Edit layout colors and icon (AT)
 *				10/3/2013:	Fix layout bugs (AT)
 *				10/4/2013:	Finalize comments
 *	Bugs:		none found yet 
 ******************************************************************************/
package edu.sdsmt.thompsonsamson.assignment1;

// imported libraries
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;


/******************************************************************************
 * Class:	MainActivity
 * Purpose:	Main entry to the application. Create twitter search tags and
 * 			store them in shared preferences. Allow user to edit and delete
 * 			tags and to clear all tags.
 *****************************************************************************/
public class MainActivity extends Activity {

	SharedPreferences _savedSearches;		// saved search tags
	EditText _objQuery;						// query text field
	EditText _objTag;						// tag text field
	TableLayout _tagListTableLayout;		// tag search table
	String _editTag;						// current tag being edited
	
	/**************************************************************************
	 * Method:	onCreate
	 * Purpose:	Main entry point for activity. Called when application is
	 * 			created. Defines activity-wide members and click listeners for 
	 * 			save and clear buttons.
	 * Params:	savedInstanceState - Bundle to record lifecycle data
	 **************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// setup shared preferences object
		_savedSearches = getSharedPreferences("tags", MODE_PRIVATE);
		
		// define variables
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

		// when the application starts, list any current searches
		getTaggedSearches();
	}

	/**************************************************************************
	 * Method:	saveTag
	 * Purpose:	Add or updates the query andtag information to the saved preferences
	 * Params:	none
	 **************************************************************************/
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
		
		// add the tag to saved preferences. if the edit tag member
		// has a value, then remove before adding the tag
		SharedPreferences.Editor prefEditor = _savedSearches.edit();
		if (_editTag != null && _editTag != tag)
		{
			prefEditor.remove(_editTag);
			_editTag = null;
		}
		prefEditor.putString( tag, query );
		prefEditor.commit();
				
		// clear the text fields and remove focus
		_objQuery.setText("");
		_objTag.setText("");
		_objQuery.clearFocus();
		_objTag.clearFocus();
		
		// refresh the search tags
		getTaggedSearches();
	}

	/**************************************************************************
	 * Method: 	getTaggedSearches
	 * Purpose:	Display the tagged searches from the shared preferences.
	 * Params:	none
	 *************************************************************************/
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

			// update the button text to tag name and fit width
			final Button tagButton = (Button) tagRowView.findViewById(R.id.button_tag);			
			tagButton.setText(tag);
						
			// create a listener when the tag button is clicked
			tagButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendQueryIntent(tagButton.getText().toString());
				}
			});
			
			// instantiate edit button and create click listener
			final Button editButton = (Button) tagRowView.findViewById(R.id.button_edit);
			editButton.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					editTag(tagButton.getText().toString());
				}
			});	
			
			// instantiate delete button and click listener
			final Button deleteButton = (Button) tagRowView.findViewById(R.id.button_delete);
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

	/**************************************************************************
	 * Method:	sendQueryIntent
	 * Purpose:	Sends intent when a tag is clicked on. URL encodes the query 
	 * 			string and appends it onto the search url.
	 * Params:	tag - the unique tag to lookup the key/pair value
	 *************************************************************************/
	public void sendQueryIntent(String tag) {

		// the URL encoder is required to be in a try/catch block
		try {
			
			// build the query string
			String query = _savedSearches.getString(tag, null);
			String url = getString(R.string.search_url) + URLEncoder.encode(query, "UTF-8");
			
			// fire the intent
			Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity (webIntent);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**************************************************************************
	 * Method: 	editTag
	 * Purpose:	Edit an existing search tag. Sets the application variable 
	 * 			to record which tag is being edited and fills in the query/tag text
	 * 			fields. The save click listener will update the tag.
	 * Params:	tag - the unique tag to lookup the key/pair value
	 *************************************************************************/
	public void editTag(String tag) {
		// get query from tag pairing in shared preferences
		String query = _savedSearches.getString(tag, null);

		// update text fields to display stored information
		_objTag.setText(tag);
		_objQuery.setText(query);
		
		_editTag = tag;
	}

	/**************************************************************************
	 * Method:	deleteTag
	 * Purpose:	Delete an existing tag from the saved preferences.
	 * Params:	tag - the unique tag to lookup the key/pair value
	 *************************************************************************/
	private void deleteTag(String tag) {
		
		// create the editor and remove the tag
		SharedPreferences.Editor prefEditor = _savedSearches.edit();
		prefEditor.remove(tag);
		prefEditor.commit();
		
		// refresh the current tag view
		getTaggedSearches();
	}

	/**************************************************************************
	 * Method:	deleteAllTags
	 * Purpose: Deletes all key/value pairs from the saved preferences and
	 * 			removes all the inflated views from the tagged searches.
	 * Params:	none
	 *************************************************************************/
	private void deleteAllTags() {

		// create the editor and remove all tags
		SharedPreferences.Editor prefEditor = _savedSearches.edit();
		prefEditor.clear();
		prefEditor.commit();
		
		// remove all inflated tagged search views
		_tagListTableLayout.removeAllViews();	
	}

	/**************************************************************************
	 * Method:	sendCheckInputAlert
	 * Purpose:	Sends an alert if both the query and tag text fields are empty
	 * Params:	none
	 *************************************************************************/
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

	/**************************************************************************
	 * Method:	sendDeleteTagAlert
	 * Purpose:	Sends an alert to the user to confirm a single tag delete
	 * Params:	none
	 *************************************************************************/
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
	
		// add the buttons - if yes, fire listener
		alertBuilder.setPositiveButton(R.string.alert_yes, deleteTagConfirmButtonListener);
		alertBuilder.setNegativeButton(R.string.alert_cancel, null);
		
		// show the alert
		AlertDialog alert = alertBuilder.create();
		alert.show();		
	}

	/**************************************************************************
	 * Method:	sendClearTagsAlert
	 * Purpose:	Sends an alert to the user to confirm deletion of all searches
	 * Params:	none
	 *************************************************************************/
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
		
		// add the buttons - if yes, fire listener
		alertBuilder.setPositiveButton(R.string.alert_yes, deleteAllTagsConfirmButtonListener);
		alertBuilder.setNegativeButton(R.string.alert_cancel, null);
	
		// show the alert
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}
}
