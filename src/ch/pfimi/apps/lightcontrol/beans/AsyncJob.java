package ch.pfimi.apps.lightcontrol.beans;

import android.app.Activity;

public class AsyncJob {

	protected Activity activity;
	protected Long id;
	protected String result;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void onJobDone() {
		// TODO Auto-generated method stub

	}

}