package com.ciriti.multifilesdownloader.bean;

import java.io.Serializable;
import java.util.Comparator;

import com.ciriti.multifilesdownloader.util.Utils;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class GameItemBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String game_name;
    public String game_package_name;
    public int progress = 0;
    public String url;
    public String absolutePathFile = null;
    public boolean interrupted = false;
    
    //comparator
    public static final Comparator<GameItemBean> BY_GAME_NAME_ASC = new ByName();
    public static final Comparator<GameItemBean> BY_GAME_NAME_DESC = new ByNameDesc();
    public static final Comparator<GameItemBean> BY_PROGRESS = new ByProgress();
    
	
    public GameItemBean(String game_name, String game_package_name, String url) {
		super();
		this.game_name = game_name;
		this.game_package_name = game_package_name;
		this.url = url;
		this.absolutePathFile = Utils.buildPathFile(game_name);
	}

	public String getGameName() {
		return game_name;
	}

	public String getPackageName() {
		return game_package_name;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getUrl() {
		return url;
	}

	public String getAbsolutePathFile() {
		return absolutePathFile;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	private static class ByName implements Comparator<GameItemBean>{

		@Override
		public int compare(GameItemBean lhs, GameItemBean rhs) {
			// TODO Auto-generated method stub
			return (int)(lhs.game_name.compareToIgnoreCase(rhs.game_name));
		}
		
	}
	
	private static class ByNameDesc implements Comparator<GameItemBean>{

		@Override
		public int compare(GameItemBean lhs, GameItemBean rhs) {
			// TODO Auto-generated method stub
			return (int)(rhs.game_name.compareToIgnoreCase(lhs.game_name));
		}
		
	}
	
	private static class ByProgress implements Comparator<GameItemBean>{

		@Override
		public int compare(GameItemBean lhs, GameItemBean rhs) {
			// TODO Auto-generated method stub
			return (lhs.progress - rhs.progress);
		}
		
	}
    
}
