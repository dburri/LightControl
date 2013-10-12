package ch.pfimi.apps.lightcontrol.beans;

import android.app.Activity;
import android.widget.TextView;
import ch.pfimi.apps.lightcontrol.R;

public class SetChannelJob extends AsyncJob {

	private Integer channel;

	public SetChannelJob(Activity activity, Integer channel) {
		this.activity = activity;
		this.channel = channel;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	@Override
	public void onJobDone() {
		((TextView) activity.findViewById(R.id.textView1)).setText(result);
	}
}
