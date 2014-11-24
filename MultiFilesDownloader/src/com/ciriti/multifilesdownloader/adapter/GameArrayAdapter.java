package com.ciriti.multifilesdownloader.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ciriti.multifilesdownloader.R;
import com.ciriti.multifilesdownloader.bean.GameItemBean;
import com.ciriti.multifilesdownloader.bean.GenericItemBean;
import com.ciriti.multifilesdownloader.bean.ItemCurrentProgress;
import com.ciriti.multifilesdownloader.bean.ItemToDownload;
import com.ciriti.multifilesdownloader.service.ControllerServ;
import com.ciriti.multifilesdownloader.util.Utils;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class GameArrayAdapter extends ArrayAdapter<GameItemBean>{

	private LayoutInflater inflater;
	private Context mContext;
	Map<ProgressBar, String> progressTest;

	public GameArrayAdapter(Context context, GameItemBean[] items) {
		super(context, 0, items);
		this.inflater 		= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			holder.btnDownload = (Button) convertView.findViewById(R.id.btn_download);
			holder.btnStop = (Button) convertView.findViewById(R.id.btn_stop);
			convertView.setTag(holder);
		}else
			holder = (Holder) convertView.getTag();

		progressTest.put(holder.progress, currentItem.getPackageName());

		// set up action of user
		configureUserAction(holder.btnDownload, holder.btnStop, currentItem, convertView);

		// set the current information
		holder.nameGame.setText(currentItem.getGameName());
		holder.progress.setProgress(currentItem.getProgress());
		holder.btnDownload.setText("Download");
		holder.progress.setVisibility(Utils.isPackageInstalled(currentItem.getPackageName(), mContext)?View.INVISIBLE:View.VISIBLE);
		holder.btnDownload.setVisibility(Utils.isPackageInstalled(currentItem.getPackageName(), mContext)?View.INVISIBLE:View.VISIBLE);
		holder.btnStop.setVisibility(Utils.isPackageInstalled(currentItem.getPackageName(), mContext)?View.INVISIBLE:View.VISIBLE);

		// tell to user that the ap isn't installed but is already downloaded
		if(Utils.isFileExist(currentItem.getGameName()) && !Utils.isPackageInstalled(currentItem.getPackageName(), mContext)){
			holder.progress.setProgress(100);
			holder.btnDownload.setText("Install");
			holder.btnStop.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private void configureUserAction(Button btnDownload, Button btnStop, final GameItemBean currentItem, View convertView) {
		// TODO Auto-generated method stub
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ControllerServ.removeDownloadItem(mContext, ItemToDownload.newInstance(currentItem.getPackageName(), currentItem.getGameName(), currentItem.getUrl()));
			}
		});;
		btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// If apk already exist start the installation process
				if(Utils.isFileExist(currentItem.getGameName()))
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
				else if(Utils.isFileExist(currentItem.getGameName())){
					new File(currentItem.getAbsolutePathFile()).delete();
					currentItem.setProgress(0);
					Toast.makeText(mContext, currentItem.getGameName() + ".apk deleted from SD", Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
				}
				return false;
			}
		});

	}

	
	public static class Holder{
		public TextView nameGame;
		public ImageView image;
		public ProgressBar progress;
		public Button btnDownload;
		public Button btnStop;
	}

	public void updateProgress(ItemCurrentProgress item){

		for(int i = 0; i<getCount(); i++){
			if(getItem(i).getPackageName().equals(item.getPackageName())){
				getItem(i).setProgress(item.getCurrentProgress());
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


	public void startDownload(GameItemBean item){
		ControllerServ.startDownloadItem(mContext, ItemToDownload.newInstance(item.getPackageName(), item.getGameName(), item.getUrl()));
	}


	public void resetProgressBar(){
		Set<ProgressBar> keySet = progressTest.keySet();
		for (ProgressBar progressBar : keySet) {
			if(!Utils.isPackageInstalled(progressTest.get(progressBar), mContext)
					&& progressBar.getProgress() < 100)
				progressBar.setProgress(0);
		}
	}
	
	public void downloadInterrupted(GenericItemBean dataUpdate){
		Set<ProgressBar> keySet = progressTest.keySet();
		for (ProgressBar progressBar : keySet) {
			if(!Utils.isPackageInstalled(progressTest.get(progressBar), mContext)
					&& progressBar.getProgress() < 100)
				progressBar.setProgress(0);
		}
		
		for(int i = 0; i<getCount(); i++){
			if(getItem(i).getPackageName().equals(dataUpdate.getPackageName())){
				getItem(i).setProgress(0);
				break;
			}
		}
		
	}
	

}
