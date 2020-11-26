package pers.jc.util;

import java.util.Timer;
import java.util.TimerTask;

public class JCTimer {
	private final Timer timer = new Timer();
	
	public JCTimer loop(Runnable runnable, long interval) {
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		}, interval, interval);
		return this;
	}
	
	public JCTimer loop(Runnable runnable, long interval, long delay) {
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		}, delay, interval);
		return this;
	}

	public JCTimer once (Runnable runnable, long delay) {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			timer.cancel();
			}
		}, delay);
		return this;
	}
	
	public void cancel() {
		timer.cancel();
	}
}
