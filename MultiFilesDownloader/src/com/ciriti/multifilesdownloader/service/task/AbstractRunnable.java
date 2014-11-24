package com.ciriti.multifilesdownloader.service.task;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author Carmelo Fabio Iriti
 *
 */
public abstract class AbstractRunnable implements Runnable {

	protected Handler handler;
	public String tag 									= this.getClass().getName();
	boolean interruptTask								= false;

	public AbstractRunnable(Handler handler) {
		super();
		this.handler = handler;
	}

	protected abstract void doRun() throws Exception;

	public void run() {
		try {
			// Abstract method to implements
			doRun();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void sendMessage(Message msg){
		handler.sendMessage(msg);
	}
	
	public void interruptTask(){
		interruptTask = true;
	}
	
}