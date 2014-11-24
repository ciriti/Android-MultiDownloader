package com.ciriti.multifilesdownloader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.ciriti.multifilesdownloader.observer.Observer;
import com.ciriti.multifilesdownloader.service.ControllerServ;
import com.ciriti.multifilesdownloader.service.ControllerServ.LocalBinder;

/**
 * 
 * @author carmelo.iriti
 *
 */
public abstract class AbObserverActivity extends FragmentActivity implements Observer{
	
	protected ControllerServ mService;
	private boolean mBound = false;
	
	@Override
	protected void onStart() {
		super.onStart();
		// Bind to ControllerServ
		Intent intent = new Intent(this, ControllerServ.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}



	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
			rem();
		}
	}

	/**
	 * Register this Observer to the Subject
	 */
	protected void rec() {
		super.onResume();
		// segister Observer
		mService.registerObserver(this);
	}
	
	/**
	 * Deregister this Observer from the Subject
	 */
	protected void rem() {
		super.onPause();
		// deregister observer
		mService.deregidterObserver(this);
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to ControllerServ, cast the IBinder and get ControllerServ instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			rec();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
			rem();
		}
	};

}
