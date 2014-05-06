package ch.pfimi.apps.lightcontrol;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;

import ch.pfimi.apps.lightcontrol.beans.Channel;

import android.util.Log;

public class Controller {

	private static final String LOG_TAG = "ch.pfimi.apps.lightcontrol.Controller";

	private static final int Ms_WS_SY_Name_in = 10;
	private static final int Mi_WS_SY_enable = 4;
	
	private static final int Mi_WS_DC_Val_in = 100;
	private static final int Mi_WS_DC_CH_in = 102;
	// if CH_SPS equals CH_in the channel was properly set
	private static final int Mi_WS_DC_CH_SPS = 104; 
	private static final int Mi_WS_DC_CH_Name = 110;
	
	private static final int Mi_WS_CC_Val_in = 200;
	private static final int Mi_WS_CC_CH_in = 202;
	// if CH_SPS equals CH_in the channel was properly set
	private static final int Mi_WS_CC_CH_SPS = 204; 
	private static final int Mi_WS_CC_CH_Name = 210;

	private static final String NET_ID = "5.16.167.210.1.1";
	private static final int PORT = 801;
	private static final String SERVICE_URL = "http://192.168.34.1/TcAdsWebService/TcAdsWebService.dll";

	static public Channel setChannel(int channel, boolean useChannelMode) {

		Log.d(LOG_TAG, "Setzte Kanal " + channel);
		Channel channelConfig = new Channel();

		int val_in = Mi_WS_DC_Val_in;
		int ch_in = Mi_WS_DC_CH_in;
		int ch_name = Mi_WS_DC_CH_Name;
		if(useChannelMode) {
			val_in = Mi_WS_CC_Val_in;
			ch_in = Mi_WS_CC_CH_in;
			ch_name = Mi_WS_CC_CH_Name;
			
		}
		
		
		TcAdsSOAP tcSoap = new TcAdsSOAP(SERVICE_URL);
		try {
			// GroupIndex
			// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
			// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

			Log.d(LOG_TAG, "connect to: netId = " + NET_ID + ", port = " + PORT);

			/* write int */
			if (tcSoap.WriteInt(NET_ID, PORT, 0x4020, ch_in, channel)) {
				Log.d(LOG_TAG, "Integer " + channel + " Written");
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				tcSoap.getError().printStackTrace(pw);
				sw.toString();
				Log.d(LOG_TAG, "Error: " + sw);
			}

			// /* write string */
			// String testString = "HHHello Automation";
			// if (tcSoap.WriteString(netId, port, 0x4020, 200, testString))
			// Log.d(LOG_TAG, "String " + testString + " Written");

			// /* read bool */
			// boolean value = tcSoap.ReadBool(netId,port,16417,800);
			// Log.d(LOG_TAG, "Boolean " + value + " read");

			/* read int */
			Log.d(LOG_TAG, String.valueOf(tcSoap.ReadInt(NET_ID, PORT, 0x4020, Mi_WS_DC_CH_in)));

			/* read string */
			channelConfig.setValue(tcSoap.ReadInt(NET_ID, PORT, 0x4020, val_in));
			channelConfig.setName(StringUtils.trim(tcSoap.ReadString(NET_ID, PORT, 0x4020, ch_name, 80)));
			Log.d(LOG_TAG, "Channel name = " + channelConfig.getName() + ", value = " + channelConfig.getValue());
			
//			for(int i = 0; i < channelConfig.getName().length(); ++i) {
//				Log.d(LOG_TAG, "char(" + i + "): " + Integer.toString((int)channelConfig.getName().charAt(i)));
//			}
			
			

		} catch (Exception ex) {
			Log.d(LOG_TAG, String.valueOf(ex));
			ex.printStackTrace();
			channelConfig.setName("Could not set channel!");
		}

		Log.d(LOG_TAG, "finished...");
		return channelConfig;
	}

	static public String setValue(Integer value, boolean useChannelMode) {

		Log.d(LOG_TAG, "Set value " + value);

		int val_in = Mi_WS_DC_Val_in;
		int ch_in = Mi_WS_DC_CH_in;
		int ch_name = Mi_WS_DC_CH_Name;
		if(useChannelMode) {
			val_in = Mi_WS_CC_Val_in;
			ch_in = Mi_WS_CC_CH_in;
			ch_name = Mi_WS_CC_CH_Name;
			
		}
		
		String result = "";
		TcAdsSOAP tcSoap = new TcAdsSOAP(SERVICE_URL);
		try {

			// GroupIndex
			// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
			// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

			Log.d(LOG_TAG, "connect to: netId = " + NET_ID + ", port = " + PORT);

			/* write byte */
			if (tcSoap.WriteInt(NET_ID, PORT, 0x4020, val_in, value)) {
				Log.d(LOG_TAG, "Integer " + value + " Written");
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				tcSoap.getError().printStackTrace(pw);
				sw.toString();
				Log.d(LOG_TAG, "Error: " + sw);
			}

		} catch (Exception ex) {
			Log.d(LOG_TAG, String.valueOf(ex));
			ex.printStackTrace();
		}

		Log.d(LOG_TAG, "finished...");
		return result;
	}

	static public String setName(String name) {

		Log.d(LOG_TAG, "Set name " + name);
		
		String result = "";
		TcAdsSOAP tcSoap = new TcAdsSOAP(SERVICE_URL);
		try {

			// GroupIndex
			// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
			// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

			Log.d(LOG_TAG, "connect to: netId = " + NET_ID + ", port = " + PORT);

			/* write byte */
			if (tcSoap.WriteString(NET_ID, PORT, 0x4020, Ms_WS_SY_Name_in, name)) {
				Log.d(LOG_TAG, "Name " + name + " Written");
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				tcSoap.getError().printStackTrace(pw);
				sw.toString();
				Log.d(LOG_TAG, "Error: " + sw);
			}

		} catch (Exception ex) {
			Log.d(LOG_TAG, String.valueOf(ex));
			ex.printStackTrace();
		}

		Log.d(LOG_TAG, "finished...");
		return result;
	}

	static public short getState() {

		Log.d(LOG_TAG, "get state");
		
		short state = -1;
		TcAdsSOAP tcSoap = new TcAdsSOAP(SERVICE_URL);
		try {

			// GroupIndex
			// 0x4020 = READ_M, WRITE_M, Offset = byteadresse
			// 0x4021 = READ_MX, WRITE_MX, Offset = bitadresse

			Log.d(LOG_TAG, "connect to: netId = " + NET_ID + ", port = " + PORT);
			state = tcSoap.ReadInt(NET_ID, PORT, 0x4020, Mi_WS_SY_enable);
			Log.d(LOG_TAG, "finished reading state: " + state);

		} catch (Exception ex) {
			Log.d(LOG_TAG, String.valueOf(ex));
			ex.printStackTrace();
		}

		Log.d(LOG_TAG, "finished...");
		return state;
	}
	
}
