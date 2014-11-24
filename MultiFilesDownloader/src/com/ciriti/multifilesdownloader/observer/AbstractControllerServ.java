package com.ciriti.multifilesdownloader.observer;

import java.util.ArrayList;

import android.app.Service;

import com.ciriti.multifilesdownloader.bean.GenericItemBean;

/**
 * 
 * @author carmelo.iriti
 *
 */
public abstract class AbstractControllerServ extends Service{

	protected ArrayList<Observer> observers;

	public void registerObserver(Observer ob){
		this.observers.add(ob);
	}

	public void deregidterObserver(Observer ob){
		if(this.observers.indexOf(ob) > 0)
			this.observers.remove(ob);
	}

	public void succesDownlodDownloadObserver(GenericItemBean updateData){
		for (Observer ob : observers) {
			ob.succesDownlod(updateData);
		}
	}

	public void updateObserver(GenericItemBean updateData){
		for (Observer ob : observers) {
			ob.update(updateData);
		}
	}

	public void errorObserver(GenericItemBean updateData){
		for (Observer ob : observers) {
			ob.error(updateData);
		}
	}

}
