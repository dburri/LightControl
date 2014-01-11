package ch.pfimi.apps.lightcontrol;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
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

		textChannelSearch = (EditText) findViewById(R.id.editText2);

		barChannelValue = (SeekBar) findViewById(R.id.seekBar1);
		barChannelValue.setOnSeekBarChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onScanChannels(View view) {

		textChannelSearch.setText("");

		Integer channelFrom = 1;
		Integer channelTo = 100;
		Integer[] channelsToScan = new Integer[channelTo - channelFrom];
		for (int i = 0; i < channelTo - channelFrom; ++i) {
			channelsToScan[i] = channelFrom + i;
		}

		new ScanChannelAsync().execute(channelsToScan);

		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < 100; ++i) {
		// sb.append(i + ": " + "\n");
		// }
		// textChannelSearch.setText(sb.toString());
	}

	public void onSetChannel(View view) {
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
	private class SetChannelAsync extends AsyncTask<Integer, String, String> {

		@Override
		protected String doInBackground(Integer... channels) {
			String result = "";
			for (Integer channel : channels) {
				result = Controller.setChannel(channel);
			}
			return result;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			textChannelName.setText(result);
			Log.v(LOG_TAG, "Finished setting channel: " + result);
		}
	}

	// AsyncTask
	private class SetValueAsync extends AsyncTask<Integer, String, String> {

		@Override
		protected String doInBackground(Integer... values) {
			String result = "";
			for (Integer value : values) {
				result = Controller.setValue(value);
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
	private class ScanChannelAsync extends AsyncTask<Integer, String, String> {

		@Override
		protected String doInBackground(Integer... channels) {
			String result = "";
			for (Integer channel : channels) {
				result = Controller.setChannel(channel);
				publishProgress(channel + ": " + result);

			}
			return result;
		}

		protected void onProgressUpdate(String... values) {
			textChannelSearch.append(values[0] + "\n");
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Log.v(LOG_TAG, "Finished setting value");
		}
	}

}
