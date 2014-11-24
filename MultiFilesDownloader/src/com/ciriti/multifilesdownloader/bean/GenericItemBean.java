package com.ciriti.multifilesdownloader.bean;

import java.io.Serializable;

/**
 * 
 * @author carmelo.iriti
 *
 */
public abstract class GenericItemBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String packageName;
	String name;
	boolean actionRemove = false;
	
	public GenericItemBean(String packageName, String name) {
		super();
		this.packageName 	= packageName;
		this.name 			= name;
	}

	public GenericItemBean(String packageName, String name, boolean actionRemove) {
		super();
		this.packageName = packageName;
		this.name = name;
		this.actionRemove = actionRemove;
	}

	public String getPackageName() {
		return packageName;
	}
	
	public String getName() {
		return name;
	}

	public boolean isActionRemove() {
		return actionRemove;
	}



}
