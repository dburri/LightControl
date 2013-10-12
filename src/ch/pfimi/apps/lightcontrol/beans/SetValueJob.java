package ch.pfimi.apps.lightcontrol.beans;

import android.app.Activity;

public class SetValueJob extends AsyncJob {

	private Integer value;

	public SetValueJob(Activity activity, Integer value) {
		this.activity = activity;
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public void onJobDone() {

	}
}
