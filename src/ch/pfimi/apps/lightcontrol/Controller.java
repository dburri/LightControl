package ch.pfimi.apps.lightcontrol;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public class Controller {

	private static final String LOG_TAG = "ch.pfimi.apps.lightcontrol.Controller";

	static public String setChannel(int channel) {

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

			Log.v(LOG_TAG, "connect to: netId = " + netId + ", port = " + port);

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
			Log.v(LOG_TAG,
					String.valueOf(tcSoap.ReadInt(netId, port, 0x4020, 102)));

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

	static public String setValue(Integer value) {

		Log.v(LOG_TAG, "Set value " + value);

		String result = "";
		TcAdsSOAP tcSoap = new TcAdsSOAP(
				"http://192.168.34.1/TcAdsWebService/TcAdsWebService.dll");
		try {
			String netId = "5.16.167.210.1.1";
			int port = 801;

			// GroupIndex
			// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
			// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

			Log.v(LOG_TAG, "connect to: netId = " + netId + ", port = " + port);

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
		return result;
	}
}
