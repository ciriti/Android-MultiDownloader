package com.ciriti.multifilesdownloader.bean;

import com.ciriti.multifilesdownloader.util.Utils;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class ItemCurrentProgress extends GenericItemBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Integer currentProgress;
	String absolutePathFile;

	public ItemCurrentProgress(String packageName, String name, Integer currentProgress) {
		super(packageName, name);
		// TODO Auto-generated constructor stub
		this.currentProgress = currentProgress;
		this.absolutePathFile = Utils.buildPathFileTmp(name);
	}
	
	public Integer getCurrentProgress() {
		return currentProgress;
	}
	
	public void setCurrentProgress(Integer currentProgress) {
		this.currentProgress = currentProgress;
	}
		
	public String getAbsolutePathFile() {
		return absolutePathFile;
	}

}
