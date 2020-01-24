package pers.jc.util;

import java.util.Timer;
import java.util.TimerTask;

public abstract class JCInterval {
	private Timer timer = new Timer();
	
	public JCInterval (double interval) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					JCInterval.this.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, (long)(interval * 1000), (long)(interval * 1000));
	}
	
	public JCInterval (double interval, double delay) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					JCInterval.this.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, (long)(delay * 1000), (long)(interval * 1000));
	}
	
	public void cancel() {
		timer.cancel();
	}
	
	public abstract void run();
}
