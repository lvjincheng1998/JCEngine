package pers.jc.util;

import java.util.Timer;
import java.util.TimerTask;

public abstract class JCTimeout {
	private Timer timer = new Timer();
	
	public JCTimeout (double delay) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					JCTimeout.this.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
				timer.cancel();
			}
		}, (long)(delay * 1000));
	}
	
	public void cancel() {
		timer.cancel();
	}
	
	public abstract void run();
}
