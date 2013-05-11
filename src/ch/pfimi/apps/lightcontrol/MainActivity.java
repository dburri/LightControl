package ch.pfimi.apps.lightcontrol;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.Inflater;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSeekBarChangeListener {

	private static final String LOG_TAG = "MainController";

	private EditText textChannel;
	private TextView textChannelName;
	private TextView textChannelValue;
	private SeekBar barChannelValue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview);
		
		
		// fill overview with content
		ArrayList<ViewItem> your_array_list = new ArrayList<ViewItem>();
		
        your_array_list.add(new ViewItem("System (SY)", 0));
        your_array_list.add(new ViewItem("DMX-Channel (DC)", 1));
        your_array_list.add(new ViewItem("TEST", 99));
        
		ArrayAdapter<ViewItem> adapter = new ArrayAdapter<ViewItem>(this, android.R.layout.simple_list_item_1, your_array_list);
		ListView listView = (ListView) findViewById(R.id.AvailableViewsList);
		listView.setAdapter(adapter);
		
		
		// Create a message handling object as an anonymous class.
		OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				Log.v(LOG_TAG, "item clicked, position = " + position + ", id = " + id);
				
//				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				View view = inflater.inflate(R.id.DebugLayout, null);
//				Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.standard_transition);
//				view.startAnimation(hyperspaceJumpAnimation);
				
			}
		};

		listView.setOnItemClickListener(mMessageClickedHandler); 
		
		
		
		//textChannel = (EditText) findViewById(R.id.editText1);
		//textChannelName = (TextView) findViewById(R.id.textView1);
		//textChannelValue = (TextView) findViewById(R.id.textView2);

		//barChannelValue = (SeekBar) findViewById(R.id.seekBar1);
		//barChannelValue.setOnSeekBarChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void setChannel(View view) {
		String ch = textChannel.getText().toString();
		Log.v(LOG_TAG, "channel = " + ch);
		Integer channel = Integer.parseInt(ch);
		new Controller().execute(0, channel);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

		// change progress text label with current seekbar value
		textChannelValue.setText("Value: " + progress);
		new Controller().execute(1, progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	// AsyncTask
	private class Controller extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			if ((Integer) arg0[0] == 0) {
				Integer channel = (Integer) arg0[1];
				setChannel(channel);
			} else if ((Integer) arg0[0] == 1) {
				Integer value = (Integer) arg0[1];
				setValue(value);
			}
			return null;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Object result) {
			Log.v(LOG_TAG, "finished");
		}

		public void setChannel(int channel) {

			Log.v(LOG_TAG, "Setzte Kanal " + channel);

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
				String channelName = tcSoap.ReadString(netId, port, 0x4020,
						110, 80);
				Log.v(LOG_TAG, channelName);
				textChannelName.setText(channelName);

			} catch (Exception ex) {
				Log.v(LOG_TAG, String.valueOf(ex));
				ex.printStackTrace();
			}

			Log.v(LOG_TAG, "finished...");
		}

		public void setValue(Integer value) {

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
