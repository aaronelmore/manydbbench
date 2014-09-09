package edu.mit.csail;

import java.io.Serializable;

public class WorkMsg implements Serializable {

	public String databaseURL;
	public int qps;
	public long durationMS;
	/**
	 * 
	 */
	private static final long serialVersionUID = -107590847735677174L;
	
	public WorkMsg(String databaseURL, int qps, long durationMS) {
		super();
		this.databaseURL = databaseURL;
		this.qps = qps;
		this.durationMS = durationMS;
	}

	@Override
	public String toString() {
		return "WorkMsg [databaseURL=" + databaseURL + ", qps=" + qps
				+ ", durationMS=" + durationMS + "]";
	}
}
