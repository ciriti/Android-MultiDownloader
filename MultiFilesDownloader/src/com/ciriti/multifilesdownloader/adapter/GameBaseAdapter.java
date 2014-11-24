package com.ciriti.multifilesdownloader.adapter;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ciriti.multifilesdownloader.R;
import com.ciriti.multifilesdownloader.bean.GameItemBean;
import com.ciriti.multifilesdownloader.bean.ItemCurrentProgress;
import com.ciriti.multifilesdownloader.bean.ItemToDownload;
import com.ciriti.multifilesdownloader.service.ControllerServ;
import com.ciriti.multifilesdownloader.util.Utils;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class GameBaseAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private List<GameItemBean> elements;
	private Context mContext;
	Map<ProgressBar, String> progressTest;


	public GameBaseAdapter(Context context, GameItemBean[] items) {
		this.inflater 		= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.elements 		= Arrays.asList(items);
		this.mContext 		= context;
		this.progressTest	= new HashMap<ProgressBar, String>(items.length);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Pattern holder to improve performance
		Holder holder = null;
		final GameItemBean currentItem = (GameItemBean) getItem(position);
		if(convertView == null){
			holder = new Holder();
			convertView = inflater.inflate(R.layout.item_game_list, parent, false);
			holder.nameGame = (TextView) convertView.findViewById(R.id.name_game);
			holder.progress = (ProgressBar) convertView.findViewById(R.id.progress_download);
			holder.btn = (Button) convertView.findViewById(R.id.btn_download);
			convertView.setTag(holder);
		}else
			holder = (Holder) convertView.getTag();

		progressTest.put(holder.progress, currentItem.getPackageName());
		
		// set up action of user
		configureUserAction(holder.btn, currentItem, convertView);
		
		// set the current information
		holder.nameGame.setText(currentItem.getGameName());
		holder.progress.setProgress(currentItem.getProgress());
		holder.btn.setText("Download");
		holder.progress.setVisibility(Utils.isPackageInstalled(currentItem.getPackageName(), mContext)?View.INVISIBLE:View.VISIBLE);
		holder.btn.setVisibility(Utils.isPackageInstalled(currentItem.getPackageName(), mContext)?View.INVISIBLE:View.VISIBLE);
		
		// tell to user that the ap isn't installed but is already downloaded
		if(fileExist(currentItem) && !Utils.isPackageInstalled(currentItem.getPackageName(), mContext)){
			holder.progress.setProgress(100);
			holder.btn.setText("Install");
		}

		return convertView;
	}
	
	private void configureUserAction(Button btn, final GameItemBean currentItem, View convertView) {
		// TODO Auto-generated method stub
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// If apk already exist start the installation process
				if(fileExist(currentItem))
					Utils.startInstallGame(mContext, currentItem);
				// vice versa start the download process
				else
					startDownload(currentItem);
			}
		});;
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// If apk already installed start the uninstall process
				if(Utils.isPackageInstalled(currentItem.getPackageName(), mContext)){
					Utils.uninstallApp(mContext, currentItem.getPackageName());
				}
				// If apk already downloaded delete the apk
				else if(fileExist(currentItem)){
					new File(currentItem.getAbsolutePathFile()).delete();
					currentItem.setProgress(0);
					Toast.makeText(mContext, currentItem.getGameName() + ".apk deleted from SD", Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
				}
				return false;
			}
		});
		
	}

	private boolean fileExist(GameItemBean currentItem){
		boolean res = false;
		if(new File(Utils.buildPathFile(currentItem.getGameName())).exists())
			res = true;
		return res;
	}


	public static class Holder{
		public TextView nameGame;
		public ImageView image;
		public ProgressBar progress;
		public Button btn;
	}

	public void updateProgress(ItemCurrentProgress item){
		for(GameItemBean game : elements){
			if(game.getPackageName().equals(item.getPackageName())){
				game.setProgress(item.getCurrentProgress());
				break;
			}
		}
		
		Set<ProgressBar> keySet = progressTest.keySet();
		for (ProgressBar progressBar : keySet) {
			if(progressTest.get(progressBar).equals(item.getPackageName())){
				progressBar.setProgress(item.getCurrentProgress());
				break;
			}
		}
		if(item.getCurrentProgress() >= 100)
			notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return elements.size();
	}

	@Override
	public Object getItem(int position) {
		return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void startDownload(GameItemBean item){
		ControllerServ.startDownloadItem(mContext, ItemToDownload.newInstance(item.getPackageName(), item.getGameName(), item.getUrl()));
	}

	public void resetProgressBar(){
		Set<ProgressBar> keySet = progressTest.keySet();
		for (ProgressBar progressBar : keySet) {
			progressBar.setProgress(0);
		}
	}

}
