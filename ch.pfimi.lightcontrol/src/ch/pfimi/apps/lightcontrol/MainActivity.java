package ch.pfimi.apps.lightcontrol;

import org.apache.commons.lang3.StringUtils;

import ch.pfimi.apps.lightcontrol.beans.Channel;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSeekBarChangeListener {

	private static final String LOG_TAG = "MainController";

	private EditText textChannelSearch;
	private EditText textChannel;
	private TextView textChannelName;
	private TextView textChannelValue;
	private SeekBar barChannelValue = null;
	private Button buttonSetChannel;
	private RadioButton radioButtonDMX;
	private RadioButton radioButtonControl;

	final static private int SCAN_FROM = 1;
	final static private int SCAN_TO = 512;
	
	private boolean useChannelMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// // fill overview with content
		// ArrayList<ViewItem> your_array_list = new ArrayList<ViewItem>();
		//
		// your_array_list.add(new ViewItem("System (SY)", 0));
		// your_array_list.add(new ViewItem("DMX-Channel (DC)", 1));
		// your_array_list.add(new ViewItem("TEST", 99));
		//
		// ArrayAdapter<ViewItem> adapter = new ArrayAdapter<ViewItem>(this,
		// android.R.layout.simple_list_item_1, your_array_list);
		// ListView listView = (ListView) findViewById(R.id.AvailableViewsList);
		// listView.setAdapter(adapter);
		//
		// // Create a message handling object as an anonymous class.
		// OnItemClickListener mMessageClickedHandler = new
		// OnItemClickListener() {
		// @SuppressWarnings("rawtypes")
		// @Override
		// public void onItemClick(AdapterView parent, View v, int position,
		// long id) {
		// Log.v(LOG_TAG, "item clicked, position = " + position
		// + ", id = " + id);
		//
		// // LayoutInflater inflater =
		// //
		// (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// // View view = inflater.inflate(R.id.DebugLayout, null);
		// // Animation hyperspaceJumpAnimation =
		// // AnimationUtils.loadAnimation(getApplicationContext(),
		// // R.anim.standard_transition);
		// // view.startAnimation(hyperspaceJumpAnimation);
		//
		// }
		// };
		//
		// listView.setOnItemClickListener(mMessageClickedHandler);

		textChannel = (EditText) findViewById(R.id.editText1);
		textChannelName = (TextView) findViewById(R.id.textView1);
		textChannelValue = (TextView) findViewById(R.id.textView2);
		textChannelSearch = (EditText) findViewById(R.id.editTextScan);
		buttonSetChannel = (Button) findViewById(R.id.buttonScanChannel);
		
		barChannelValue = (SeekBar) findViewById(R.id.seekBar1);
		barChannelValue.setOnSeekBarChangeListener(this);
		
		radioButtonDMX = (RadioButton) findViewById(R.id.radioButtonDMX);
		radioButtonControl = (RadioButton) findViewById(R.id.radioButtonControl);
		
		textChannelSearch.setKeyListener(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onScanChannels(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

		textChannelSearch.append("\n---------------------------------");
		textChannelSearch.append("\nSCANING " + (useChannelMode ? "Control-Channels" : "DMX-Channels"));
		textChannelSearch.append("\n---------------------------------\n");
		Integer[] channelsToScan = new Integer[SCAN_TO - SCAN_FROM + 1];
		for (int i = 0; i <= SCAN_TO - SCAN_FROM; ++i) {
			channelsToScan[i] = SCAN_FROM + i;
		}
		enableChannelControls(false);
		new ScanChannelAsync().execute(channelsToScan);
	}

	public void onSetChannel(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    
		String ch = textChannel.getText().toString();
		try {
			Integer channel = Integer.parseInt(ch);
			Log.v(LOG_TAG, "channel = " + channel);
			setChannel(channel);
		} catch (Exception e) {
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.v(LOG_TAG, "Value = " + progress);
		textChannelValue.setText("Value: " + progress);
		setValue(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	public void setChannel(Integer channel) {
		new SetChannelAsync().execute(channel);
	}

	public void setValue(Integer value) {
		new SetValueAsync().execute(value);
	}

	// AsyncTask
	private class SetChannelAsync extends AsyncTask<Integer, String, Channel> {

		@Override
		protected Channel doInBackground(Integer... channels) {
			Channel result = null;
			for (Integer channel : channels) {
				result = Controller.setChannel(channel, useChannelMode);
			}
			return result;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Channel channelConfig) {
			textChannelName.setText(channelConfig.getName());
			barChannelValue.setProgress(channelConfig.getValue());
			Log.v(LOG_TAG,
					"Finished setting channel: " + channelConfig.getName());
		}
	}

	// AsyncTask
	private class SetValueAsync extends AsyncTask<Integer, String, String> {

		@Override
		protected String doInBackground(Integer... values) {
			String result = "";
			for (Integer value : values) {
				result = Controller.setValue(value, useChannelMode);
			}
			return result;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Log.v(LOG_TAG, "Finished setting value");
		}
	}

	// AsyncTask
	private class ScanChannelAsync extends AsyncTask<Integer, String, Channel> {

		@Override
		protected Channel doInBackground(Integer... channels) {
			Channel channelConfig = null;
			for (Integer channel : channels) {
				channelConfig = Controller.setChannel(channel, useChannelMode);
				if (!StringUtils.isEmpty(channelConfig.getName())) {
					publishProgress(channel + ": " + channelConfig.getName()
							+ " val = " + channelConfig.getValue());
				}

			}
			return channelConfig;
		}

		protected void onProgressUpdate(String... values) {
			textChannelSearch.append(values[0] + "\n");
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Channel result) {
			enableChannelControls(true);
			Log.v(LOG_TAG, "Finished setting value");
		}
	}

	public void onSelectChannelTypeClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radioButtonDMX:
	            if (checked) {
	            	useChannelMode = false;
	            }	
	            break;
	        case R.id.radioButtonControl:
	            if (checked) {
	            	useChannelMode = true;
	            }
	            break;
	    }
	}
	
	private void enableChannelControls(boolean enabled) {
		textChannel.setEnabled(enabled);
		buttonSetChannel.setEnabled(enabled);
		barChannelValue.setEnabled(enabled);
		radioButtonDMX.setEnabled(enabled);
		radioButtonControl.setEnabled(enabled);
	}

}
