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

	private EditText editText2;
	
	private EditText textChannelSearch;
	private EditText textChannel;
	private TextView textChannelName;
	private TextView textChannelValue;
	private TextView textView1;
	private SeekBar barChannelValue = null;
	private Button buttonSetChannel;
	private Button buttonResetValue;
	private Button buttonSetFullValue;
	private RadioButton radioButtonDMX;
	private RadioButton radioButtonControl;

	final static private int DMX_SCAN_FROM = 1;
	final static private int DMX_SCAN_TO = 512;
	final static private int CONTROL_SCAN_FROM = 1;
	final static private int CONTROL_SCAN_TO = 72;

	private boolean useControlChannelMode = true;

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
		textChannelName = (TextView) findViewById(R.id.channelText);
		textChannelValue = (TextView) findViewById(R.id.valueText);
		textView1 = (TextView) findViewById(R.id.textView1);
		textChannelSearch = (EditText) findViewById(R.id.editTextScan);
		
		editText2 = (EditText) findViewById(R.id.editText2);
		
		buttonSetChannel = (Button) findViewById(R.id.buttonScanChannel);
		buttonResetValue = (Button) findViewById(R.id.buttonResetValue);
		buttonSetFullValue = (Button) findViewById(R.id.buttonSetFullValue);
		
		barChannelValue = (SeekBar) findViewById(R.id.seekBar1);
		barChannelValue.setOnSeekBarChangeListener(this);

		radioButtonDMX = (RadioButton) findViewById(R.id.radioButtonDMX);
		radioButtonControl = (RadioButton) findViewById(R.id.radioButtonControl);

		textChannelSearch.setKeyListener(null);
		
		updateState();
	}
	
	public void onConnect(View view) {

		Log.v(LOG_TAG, "connecting " + editText2.getText().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 
	 * @param view
	 */
	public void onScanChannels(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

		int scanFrom = useControlChannelMode ? CONTROL_SCAN_FROM : DMX_SCAN_FROM;
		int scanTo   = useControlChannelMode ? CONTROL_SCAN_TO : DMX_SCAN_TO;
		
		textChannelSearch.append("\n---------------------------------");
		textChannelSearch.append("\nSCANING " + (useControlChannelMode ? "Control-Channels" : "DMX-Channels"));
		textChannelSearch.append("\nFROM CHANNEL " + scanFrom + " TO " + scanTo);
		textChannelSearch.append("\n---------------------------------\n");
		
		
		Integer[] channelsToScan = new Integer[scanTo - scanFrom + 1];
		for (int i = 0; i <= scanTo - scanFrom; ++i) {
			channelsToScan[i] = scanFrom + i;
		}
		enableChannelControls(false);
		new ScanChannelAsync().execute(channelsToScan);
	}

	/**
	 * 
	 * @param view
	 */
	public void onSetChannel(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

		String ch = textChannel.getText().toString();
		try {
			Integer channel = Integer.parseInt(ch);
			String name = editText2.getText().toString();
			name = StringUtils.left(name, 40);
			for(int i = name.length(); i < 40; ++i) {
				name += " ";
			}
			Log.v(LOG_TAG, "channel = " + channel + ", name = " + name);
			updateState();
			setName(name);
			setChannel(channel);
		} catch (Exception e) {
			Log.d(LOG_TAG, "Error: " + e.getMessage());
		}
	}

	/**
	 * 	
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.v(LOG_TAG, "Value = " + progress);
		textChannelValue.setText(Integer.toString(progress));
		setValue(progress);
	}

	/**
	 * 	
	 */
	public void onSet255(View view) {
		textChannelValue.setText("255");
		barChannelValue.setProgress(255);
		setValue(255);
	}

	/**
	 * 	
	 */
	public void onSet127(View view) {
		textChannelValue.setText("127");
		barChannelValue.setProgress(127);
		setValue(127);
	}

	/**
	 * 	
	 */
	public void onSet0(View view) {
		textChannelValue.setText("0");
		barChannelValue.setProgress(0);
		setValue(0);
	}

	/**
	 * 	
	 */
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * 	
	 */
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * 	
	 */
	public void setChannel(Integer channel) {
		new SetChannelAsync().execute(channel);
	}

	/**
	 * 	
	 */
	public void setValue(Integer value) {
		Log.v(LOG_TAG, "Set new value: " + value);
		new SetValueAsync().execute(value);
	}
	
	/**
	 * 	
	 */
	public void setName(String name) {
		new SetNameAsync().execute(name);
	}

	public void updateState() {
		new GetStateAsync().execute();
	}
	
	/**
	 * 	
	 */
	// AsyncTask
	private class SetChannelAsync extends AsyncTask<Integer, String, Channel> {

		@Override
		protected Channel doInBackground(Integer... channels) {
			Channel result = null;
			for (Integer channel : channels) {
				result = Controller.setChannel(channel, useControlChannelMode);
			}
			return result;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Channel channelConfig) {
			textChannelName.setText(channelConfig.getName());
			barChannelValue.setProgress(channelConfig.getValue());
			Log.v(LOG_TAG, "Finished setting channel: " + channelConfig.getName());
		}
	}

	// AsyncTask
	private class SetValueAsync extends AsyncTask<Integer, String, String> {

		@Override
		protected String doInBackground(Integer... values) {
			String result = "";
			for (Integer value : values) {
				result = Controller.setValue(value, useControlChannelMode);
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
	private class SetNameAsync extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... names) {
			String result = "";
			for (String name : names) {
				result = Controller.setName(name);
			}
			return result;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Log.v(LOG_TAG, "Finished setting name");
		}
	}
	// AsyncTask
	private class GetStateAsync extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... is) {
			Integer result = -1;
			//for (Integer i : is) {
				result = Integer.valueOf(Controller.getState());
			//}
			return result;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Integer state) {
			Log.v(LOG_TAG, "Finished getting state = " + state);
			if(state != null && state == 1) {
				textView1.setText("ONLINE");
			} else {
				textView1.setText("OFFLINE");
			}
		}
	}

	// AsyncTask
	private class ScanChannelAsync extends AsyncTask<Integer, String, Channel> {

		@Override
		protected Channel doInBackground(Integer... channels) {
			Channel channelConfig = null;
			for (Integer channel : channels) {
				channelConfig = Controller.setChannel(channel, useControlChannelMode);
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
		switch (view.getId()) {
		case R.id.radioButtonDMX:
			if (checked) {
				useControlChannelMode = false;
			}
			break;
		case R.id.radioButtonControl:
			if (checked) {
				useControlChannelMode = true;
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
		buttonResetValue.setEnabled(enabled);
		buttonSetFullValue.setEnabled(enabled);
	}

}
