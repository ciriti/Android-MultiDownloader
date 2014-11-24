package com.ciriti.multifilesdownloader.observer;

import com.ciriti.multifilesdownloader.bean.GenericItemBean;

/**
 * 
 * @author carmelo.iriti
 *
 */
public interface Observer {
	
	public void succesDownlod(GenericItemBean dataUpdate);
	public void update(GenericItemBean dataUpdate);
	public void error(GenericItemBean dataUpdate);

}
