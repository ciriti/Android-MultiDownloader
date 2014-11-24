package com.ciriti.multifilesdownloader.bean;


/**
 * 
 * @author carmelo.iriti
 *
 */
public class ItemToDownload extends GenericItemBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String url;

	public ItemToDownload(String packageName, String name, String url) {
		super(packageName, name);
		this.url 			= url;
	}
	
	public ItemToDownload(String packageName, String name, String url, boolean interruptTask) {
		super(packageName, name, interruptTask);
		this.url 			= url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public static ItemToDownload newInstance(String packageName, String name, String url){
		ItemToDownload instance = new ItemToDownload(packageName, name, url);
		return instance;
	}
	

	

}
