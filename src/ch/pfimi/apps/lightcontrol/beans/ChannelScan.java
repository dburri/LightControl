package ch.pfimi.apps.lightcontrol.beans;

import android.app.Activity;

public class ChannelScan extends AsyncJob {

	private int from;
	private int to;

	public ChannelScan(Activity activity, int from, int to) {
		this.activity = activity;
		for (int i = 0; i < 100; ++i) {
			// asyncJobs.add(new AsyncJob(AsyncJob.JobType.SET_CHANNEL, i,
			// null));
		}
	}

	@Override
	public void onJobDone() {

	}

}
