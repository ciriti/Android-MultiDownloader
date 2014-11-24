package com.ciriti.multifilesdownloader.service;

import com.ciriti.multifilesdownloader.bean.GenericItemBean;

/**
 * 
 * @author carmelo.iriti
 *
 */
public interface ICommunicationService {
	
	/*
	 * Accodare la firma della callback da utilizzare nel codice 
	 */
	public void successDownloaded(GenericItemBean resp);
	public void sendProgress(GenericItemBean resp);
	public void error(GenericItemBean resp);
	public void startService();
	public void checkIfServiceNeedsToStop();
	
}
