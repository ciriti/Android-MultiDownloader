package com.ciriti.multifilesdownloader;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.ciriti.multifilesdownloader.adapter.GameArrayAdapter;
import com.ciriti.multifilesdownloader.bean.GameItemBean;
import com.ciriti.multifilesdownloader.bean.GenericItemBean;
import com.ciriti.multifilesdownloader.bean.ItemCurrentProgress;
import com.ciriti.multifilesdownloader.bean.ItemErrorOccurred;
import com.ciriti.multifilesdownloader.util.AcoLog;
import com.ciriti.multifilesdownloader.util.DownloaderConfig;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class MainActivity extends AbObserverActivity {

	ListView list;
	//	GameBaseAdapter adapter;
	GameArrayAdapter arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_list);
		list = (ListView) findViewById(R.id.games_list);
		//		adapter = new GameBaseAdapter(getApplicationContext(),getDataArray());
		arrayAdapter = new GameArrayAdapter(getApplicationContext(), getDataArray());
		list.setAdapter(arrayAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//		adapter.notifyDataSetChanged();
		arrayAdapter.notifyDataSetChanged();
	}

	@Override
	public void update(GenericItemBean dataUpdate) {

		if(dataUpdate instanceof ItemCurrentProgress){
			ItemCurrentProgress itemCurrentProgress = (ItemCurrentProgress) dataUpdate;
			AcoLog.i(getClass().getName(), "MultiDownload PackageName[" + itemCurrentProgress.getPackageName()+ "]" + " CurrentProgress[" + itemCurrentProgress.getCurrentProgress() + "]");
			// update data in adapter
			//			adapter.updateProgress(itemCurrentProgress);
			arrayAdapter.updateProgress(itemCurrentProgress);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.sort_asc) {
			arrayAdapter.sort(GameItemBean.BY_GAME_NAME_ASC);
			return true;
		}else if (id == R.id.sort_desc) {
			arrayAdapter.sort(GameItemBean.BY_GAME_NAME_DESC);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static GameItemBean[] getDataArray(){
		return new GameItemBean[]{
				new GameItemBean("testapk1", "com.example.testapk1", DownloaderConfig.LIK_1),
				new GameItemBean("testapk2", "com.example.testapk2", DownloaderConfig.LIK_2),
				new GameItemBean("testapk3", "com.example.testapk3", DownloaderConfig.LIK_3),
				new GameItemBean("testapk4", "com.example.testapk4", DownloaderConfig.LIK_4),
				new GameItemBean("testapk5", "com.example.testapk5", DownloaderConfig.LIK_5),
				new GameItemBean("testapk6", "com.example.testapk6", DownloaderConfig.LIK_6),
				new GameItemBean("testapk7", "com.example.testapk7", DownloaderConfig.LIK_7),
				new GameItemBean("testapk8", "com.example.testapk8", DownloaderConfig.LIK_8),
				new GameItemBean("testapk9", "com.example.testapk9", DownloaderConfig.LIK_9),
				new GameItemBean("testapk10", "com.example.testapk10", DownloaderConfig.LIK_10),
				new GameItemBean("testapk11", "com.example.testapk11", DownloaderConfig.LIK_10)
		};
	}

	@Override
	public void succesDownlod(GenericItemBean dataUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(GenericItemBean dataUpdate) {
		// TODO Auto-generated method stub
		if(dataUpdate instanceof ItemErrorOccurred)
			Toast.makeText(getApplicationContext(), ((ItemErrorOccurred)dataUpdate).getErrorMsg(), Toast.LENGTH_SHORT).show();	
		if(dataUpdate.isActionRemove()){
			arrayAdapter.downloadInterrupted(dataUpdate);
		}else
			arrayAdapter.resetProgressBar();
	}
}
