package ch.pfimi.apps.lightcontrol;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
import ch.pfimi.apps.lightcontrol.beans.AsyncJob;
import ch.pfimi.apps.lightcontrol.beans.ChannelScan;
import ch.pfimi.apps.lightcontrol.beans.SetChannelJob;
import ch.pfimi.apps.lightcontrol.beans.SetValueJob;

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

		// create jobs
		ChannelScan channelScan = new ChannelScan(this, 1, 100);

		StringBuilder sb = new StringBuilder();
		// sb.append(i + ": " + "\n");
		textChannelSearch.setText(sb.toString());
	}

	public void onSetChannel(View view) {
		String ch = textChannel.getText().toString();
		Integer channel = Integer.parseInt(ch);
		Log.v(LOG_TAG, "channel = " + channel);
		setChannel(channel);
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
		SetChannelJob asyncJob = new SetChannelJob(this, channel);
		new Controller().execute(asyncJob);
	}

	public void setValue(Integer value) {
		SetValueJob asyncJob = new SetValueJob(this, value);
		new Controller().execute(asyncJob);
	}

	// AsyncTask
	private class Controller extends
			AsyncTask<AsyncJob, String, List<AsyncJob>> {

		@Override
		protected List<AsyncJob> doInBackground(AsyncJob... asyncJobs) {

			List<AsyncJob> asyncJobResults = new ArrayList<AsyncJob>();
			for (AsyncJob asyncJob : asyncJobs) {
				asyncJobResults.add(executeAsyncJob(asyncJob));
			}

			return asyncJobResults;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(List<AsyncJob> result) {

			for (AsyncJob asyncJob : result) {
				asyncJob.onJobDone();
			}
			Log.v(LOG_TAG, "finished");
		}

		private AsyncJob executeAsyncJob(AsyncJob asyncJob) {
			if (asyncJob.getJobType() == AsyncJob.JobType.SET_CHANNEL) {
				String result = setChannel(asyncJob.getChannel());
				asyncJob.setResult(result);
			} else if (asyncJob.getJobType() == AsyncJob.JobType.SET_VALUE) {
				setValue(asyncJob.getValue());
			}
			return asyncJob;
		}

		private String setChannel(int channel) {

			Log.v(LOG_TAG, "Setzte Kanal " + channel);
			String channelName = "";

			TcAdsSOAP tcSoap = new TcAdsSOAP(
					"http://192.168.34.1/TcAdsWebService/TcAdsWebService.dll");
			try {
				String netId = "5.16.167.210.1.1";
				int port = 801;

				// GroupIndex
				// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
				// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

				Log.v(LOG_TAG, "connect to: netId = " + netId + ", port = "
						+ port);

				/* write int */
				if (tcSoap.WriteInt(netId, port, 0x4020, 102, channel)) {
					Log.v(LOG_TAG, "Integer " + channel + " Written");
				} else {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					tcSoap.getError().printStackTrace(pw);
					sw.toString();
					Log.v(LOG_TAG, "Error: " + sw);
				}

				// /* write string */
				// String testString = "HHHello Automation";
				// if (tcSoap.WriteString(netId, port, 0x4020, 200, testString))
				// Log.v(LOG_TAG, "String " + testString + " Written");

				// /* read bool */
				// boolean value = tcSoap.ReadBool(netId,port,16417,800);
				// Log.v(LOG_TAG, "Boolean " + value + " read");

				/* read int */
				Log.v(LOG_TAG, String.valueOf(tcSoap.ReadInt(netId, port,
						0x4020, 102)));

				/* read string */
				channelName = tcSoap.ReadString(netId, port, 0x4020, 110, 80);
				Log.v(LOG_TAG, channelName);

			} catch (Exception ex) {
				Log.v(LOG_TAG, String.valueOf(ex));
				ex.printStackTrace();
				channelName = "Could not set channel!";
			}

			Log.v(LOG_TAG, "finished...");
			return channelName;
		}

		private void setValue(Integer value) {

			Log.v(LOG_TAG, "Set value " + value);

			TcAdsSOAP tcSoap = new TcAdsSOAP(
					"http://192.168.34.1/TcAdsWebService/TcAdsWebService.dll");
			try {
				String netId = "5.16.167.210.1.1";
				int port = 801;

				// GroupIndex
				// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
				// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

				Log.v(LOG_TAG, "connect to: netId = " + netId + ", port = "
						+ port);

				/* write byte */
				if (tcSoap.WriteInt(netId, port, 0x4020, 100, value)) {
					Log.v(LOG_TAG, "Integer " + value + " Written");
				} else {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					tcSoap.getError().printStackTrace(pw);
					sw.toString();
					Log.v(LOG_TAG, "Error: " + sw);
				}

			} catch (Exception ex) {
				Log.v(LOG_TAG, String.valueOf(ex));
				ex.printStackTrace();
			}

			Log.v(LOG_TAG, "finished...");
		}

	}

}
