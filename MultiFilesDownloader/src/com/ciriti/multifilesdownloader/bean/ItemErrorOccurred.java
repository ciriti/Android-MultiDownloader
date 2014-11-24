package com.ciriti.multifilesdownloader.bean;

public class ItemErrorOccurred extends GenericItemBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String errorMsg;
	int errorCode;

	public ItemErrorOccurred(String packageName, String name, String errorMsg) {
		super(packageName, name);
		// TODO Auto-generated constructor stub
		this.errorMsg = errorMsg;
	}
	
	public ItemErrorOccurred(String packageName, String name, String errorMsg, boolean interrupted) {
		super(packageName, name, interrupted);
		// TODO Auto-generated constructor stub
		this.errorMsg = errorMsg;
	}
	
	public ItemErrorOccurred(String packageName, String name, String errorMsg, int errorCode) {
		super(packageName, name);
		// TODO Auto-generated constructor stub
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
