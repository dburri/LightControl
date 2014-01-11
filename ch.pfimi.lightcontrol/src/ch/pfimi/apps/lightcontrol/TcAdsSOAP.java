package ch.pfimi.apps.lightcontrol;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

class TcAdsSOAP {
	private String soapTargetUrl;
	private final String soap_read = "\"http://beckhoff/action/TcAdsSync.Read\"";
	private final String soap_write = "http://beckhoff.org/action/TcAdsSync.Write";
	private Exception error;
	public String key = "ppData";

	public TcAdsSOAP(String szSoapTargetUrl) {
		soapTargetUrl = szSoapTargetUrl;
	}

	public short ReadInt(String netId, int port, long group, long offset)
			throws Exception {
		DataInputStream data = null;
		data = SOAPCall(netId, port, group, offset, 2, true);
		if (data == null) {
			throw error;
		}

		try {
			return data.readShort();
		} catch (Exception e) {
			throw new Exception("Error casting data to Short");
		}
	}

	public boolean ReadBool(String netId, int port, long group, long offset)
			throws Exception {
		DataInputStream data = null;
		data = SOAPCall(netId, port, group, offset, 2);
		if (data == null) {
			System.out.println("data == NULL");
			throw error;
		}

		try {
			byte[] x = new byte[2];
			data.readFully(x);
			if (x[0] == '0')
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new Exception("Error casting data to Bool");
		}
	}

	public String ReadString(String netId, int port, long group, long offset,
			int cblen) throws Exception {
		String ret = "";
		DataInputStream data = null;
		data = SOAPCall(netId, port, group, offset, cblen);
		if (data == null) {
			throw error;
		}

		try {
			char stopchar = 205;
			String x = data.readLine();
			int p = x.indexOf(stopchar);
			if (p != -1) {
				return x.substring(0, p);
			} else
				return x;
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean WriteInt(String netId, int port, long group, long offset,
			int data) {
		String hexs = Integer.toHexString(data);
		int cblen = 2;
		byte[] pData = new byte[cblen];

		while (hexs.length() < cblen * 2)
			hexs = "0" + hexs;

		int j = 0;

		for (int i = 0; i < cblen; i++) {
			pData[(cblen - 1) - i] = (byte) ((t2b(hexs.charAt(i * 2)) * 16) + t2b(hexs
					.charAt((i * 2) + 1)));
		}

		return SOAPWrite(netId, port, group, offset, pData);
	}

	public boolean WriteString(String netId, int port, long group, long offset,
			String data) {
		return SOAPWrite(netId, port, group, offset, data.getBytes());
	}

	public boolean WriteBool(String netId, int port, long group, long offset,
			boolean data) {
		byte[] x = new byte[1];
		if (data)
			x[0] = '1';
		else
			x[0] = '0';

		return SOAPWrite(netId, port, group, offset, x);
	}

	private boolean SOAPWrite(String netId, int port, long group, long offset,
			byte[] data) {
		String soapResponse = new String();
		HttpURLConnection connection = null;

		try {
			URL u = new URL(soapTargetUrl);
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();

			connection = uc;

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"text/xml;charset=utf-8");
			connection.setRequestProperty("SOAPAction", soap_write);
			connection.setRequestProperty("Expect", "100-continue");
			connection.setRequestProperty("Connection", "Keep-Alive");

			OutputStream out = new BufferedOutputStream(
					connection.getOutputStream());
			// OutputStream out = connection.getOutputStream();
			char[] b64 = Base64.encode(data);
			String b64s = new String(b64);

			Writer wout = new OutputStreamWriter(out);
			wout.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			wout.write("<soap:Envelope ");
			wout.write("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
			wout.write("xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
			wout.write("xmlns:tns=\"http://beckhoff.org/wsdl/\" ");
			wout.write("xmlns:types=\"http://beckhoff.org/wsdl/encodedTypes\" ");
			wout.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			wout.write("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
			wout.write("<soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
			wout.write("<q1:Write xmlns:q1=\"http://beckhoff.org/message/\">");
			wout.write("<netId>" + netId + "</netId>");
			wout.write("<nPort>" + port + "</nPort>");
			wout.write("<indexGroup>" + group + "</indexGroup>");
			wout.write("<indexOffset>" + offset + "</indexOffset>");
			wout.write("<pData>" + b64s + "</pData>");
			wout.write("</q1:Write>");
			wout.write("</soap:Body>");
			wout.write("</soap:Envelope>");

			wout.flush();
			wout.close();

			InputStream in = null;

			in = connection.getInputStream();

			int c;

			while ((c = in.read()) != -1)
				soapResponse = soapResponse + (char) c;
			in.close();
		} catch (Exception e) {
			error = e;
			return false;
		}
		if (soapResponse.indexOf("<faultcode>") != -1) {
			error = new Exception("Writing Failed");
			return false;
		}
		return true;
	}

	private DataInputStream SOAPCall(String netId, int port, long group,
			long offset, int cblen) {
		return SOAPCall(netId, port, group, offset, cblen, false);
	}

	private DataInputStream SOAPCall(String netId, int port, long group,
			long offset, int cblen, boolean swap) {
		String data = new String();
		String soapResponse = new String();
		HttpURLConnection connection = null;

		try {
			URL u = new URL(soapTargetUrl);
			URLConnection uc = u.openConnection();

			connection = (HttpURLConnection) uc;

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"text/xml;charset=utf-8");
			connection.setRequestProperty("SOAPAction", soap_read);
			connection.setRequestProperty("Expect", "100-continue");
			connection.setRequestProperty("Connection", "Keep-Alive");

			OutputStream out = null;
			out = connection.getOutputStream();

			Writer wout = new OutputStreamWriter(out);
			wout.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			wout.write("<soap:Envelope ");
			wout.write("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
			wout.write("xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
			wout.write("xmlns:tns=\"http://beckhoff.org/wsdl/\" ");
			wout.write("xmlns:types=\"http://beckhoff.org/wsdl/encodedTypes\" ");
			wout.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			wout.write("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
			wout.write("<soap:Body soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
			wout.write("<q1:Read xmlns:q1=\"http://beckhoff.org/message/\">");
			wout.write("<netId xsi:type=\"xsd:string\">" + netId + "</netId>");
			wout.write("<nPort xsi:type=\"xsd:int\">" + port + "</nPort>");
			wout.write("<indexGroup xsi:type=\"xsd:unsignedInt\">" + group
					+ "</indexGroup>");
			wout.write("<indexOffset xsi:type=\"xsd:unsignedInt\">" + offset
					+ "</indexOffset>");
			wout.write("<cbLen xsi:type=\"xsd:int\">" + cblen + "</cbLen>");
			wout.write("</q1:Read>");
			wout.write("</soap:Body>");
			wout.write("</soap:Envelope>");

			wout.flush();
			wout.close();

			InputStream in = null;

			in = connection.getInputStream();

			int c;

			while ((c = in.read()) != -1)
				soapResponse = soapResponse + (char) c;
			in.close();
		} catch (Exception e) {
			error = e;
			return null;
		}

		data = parseSOAPString(soapResponse);
		if (data.length() == 0)
			return null;
		else {
			try {
				// swap the decode and Swap the stream

				byte[] b64 = new byte[2];
				byte[] b64r = new byte[2];
				b64 = Base64.decode(data.toCharArray());

				if (swap) {
					int x = 1;
					for (int i = 0; i < 2; i++) {
						b64r[x] = b64[i];
						x--;
					}
				} else
					b64r = b64;

				// reinterpret binary data
				ByteArrayInputStream bAis = new ByteArrayInputStream(b64r);
				DataInputStream diS = new DataInputStream(bAis);
				return diS;
			} catch (Exception e) {
				error = e;
				return null;
			}
		}
	}

	private String parseSOAPString(String SOAPMessage) {
		try {
			SOAPMessage = SOAPMessage.substring(SOAPMessage.indexOf(key, 0)
					+ key.length() + 1, SOAPMessage.lastIndexOf(key) - 2);
			return SOAPMessage;
		} catch (Exception e) {
			error = new Exception("Unknown answer from WebService");
			return null;
		}
	}

	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	public static byte t2b(char t) {
		if ((t >= 0x61) && (t <= 0x66))
			return (byte) (t - 0x57);
		if ((t >= 0x41) && (t <= 0x46))
			return (byte) (t - 0x37);
		if ((t >= 0x30) && (t <= 0x39))
			return (byte) (t - 0x30);
		return 0;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}
}