package com.ciriti.multifilesdownloader.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import com.ciriti.multifilesdownloader.bean.GenericItemBean;
import com.ciriti.multifilesdownloader.bean.ItemCurrentProgress;
import com.ciriti.multifilesdownloader.bean.ItemErrorOccurred;
import com.ciriti.multifilesdownloader.bean.ItemToDownload;
import com.ciriti.multifilesdownloader.observer.AbstractControllerServ;
import com.ciriti.multifilesdownloader.observer.Observer;
import com.ciriti.multifilesdownloader.service.task.AbstractRunnable;
import com.ciriti.multifilesdownloader.service.task.FileDownloader;
import com.ciriti.multifilesdownloader.util.AcoLog;
import com.ciriti.multifilesdownloader.util.Utils;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class ControllerServ extends AbstractControllerServ implements ICommunicationService{

	private final IBinder 						mBinder 			= new LocalBinder();
	private final Random 						mGenerator 			= new Random();
	private CommandServiceExecutor 				mExecutor 			= null;
	private ControllerWakeLock 					waveLoker 			= null;
	private static HandlerThread 				executorThread		= null;
	ThreadPoolExecutor							executor			= null;
	public static int 							NUMBER_OF_THREAD 	= 0;
	AbstractRunnable 							command;
	public static final String 					TAG 				= "RegisterActivitySecond";
	private int 								mStartId = 0; 
	public static final String 					KEY_ITEM_DOWNLOAD 	= "key_item_download";
	public HashMap<String, AbstractRunnable> 	packageMap;

	public static final int WHAT_CURRENT_PROGRESS 		= 1;
	public static final int WHAT_COMPLETED_DOWNLOAD		= 2;
	public static final int WHAT_ERROR 					= -1;

	/**
	 * Questo handler ricever√† i messaggi dei comandi in coda
	 */
	public Handler handler = new ProcessCommunicationHandler(this);

	public ControllerServ() {
		super();
		observers = new ArrayList<Observer>();
		NUMBER_OF_THREAD = Utils.getNumCores()==1?2:Utils.getNumCores();
	}

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public ControllerServ getService() {
			// Return this instance of LocalService so clients can call public methods
			return ControllerServ.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		waveLoker = new ControllerWakeLock((PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE));
		AcoLog.i(TAG,"MultiDownload stopSelfResult onCreate()");
		packageMap = new HashMap<String, AbstractRunnable>();
	}

	/**
	 * Allows you to start the download process of apk
	 * 
	 * @param context
	 *  			A Context of the application package implementing this class.
	 *  
	 * @param infoItem
	 * 				The item's infoes 
	 */
	public static void startDownloadItem(Context context, ItemToDownload infoItem) {
		Intent i = new Intent(context, ControllerServ.class);
		i.putExtra(ControllerServ.KEY_ITEM_DOWNLOAD, infoItem);
		context.startService(i);
	}

	public static void removeDownloadItem(Context context, ItemToDownload infoItem) {
		ItemToDownload mInfoItem = new ItemToDownload(infoItem.getPackageName(), infoItem.getName(), infoItem.getUrl(), true);
		Intent i = new Intent(context, ControllerServ.class);
		i.putExtra(ControllerServ.KEY_ITEM_DOWNLOAD, mInfoItem);
		context.startService(i);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		ItemToDownload fileInfo					= (ItemToDownload) intent.getSerializableExtra(KEY_ITEM_DOWNLOAD);
		if(packageMap.containsKey(fileInfo.getPackageName()) && !fileInfo.isActionRemove()){
			AcoLog.i(TAG,"MultiDownload gi‡ in download action["+ fileInfo.getPackageName()+"]");
			return 0;
		}
		mStartId 								= startId;

		AcoLog.i(TAG,"MultiDownload stopSelfResult startId["+startId+"] action["+ fileInfo.getPackageName()+"]");

		/**
		 * l'handler che processera'† il messaggio di ritorno si trova nella classe ProcessCommunicationHandler
		 * l'handler definito in questa classe (CommandServiceExecutor) si occupa solo di schedulare le azioni in modo sequenziale 
		 */
		command = FileDownloader.newInstance(handler, fileInfo);
		
		/**
		 * keep trace to Task if I have not remove it
		 */
		if(!fileInfo.isActionRemove())
			packageMap.put(fileInfo.getPackageName(), command);
		
		if(command != null) {
			if(!fileInfo.isActionRemove()){
				getParallelExecutor().execute(command);
			}else{
				interruptTask(getParallelExecutor(), fileInfo);
			}
		}
		/**
		 * significa che se si interrompe l'esecuzione del servizio non ripartira'†in automatico
		 */
		return START_NOT_STICKY;
	}

	/**
	 * Interrupt the specific task
	 * @param parallelExecutor
	 * @param fileInfo
	 */
	private void interruptTask(ThreadPoolExecutor parallelExecutor, ItemToDownload fileInfo) {
		AbstractRunnable taskToRemove = getTaskToRemove(fileInfo);
		if(taskToRemove != null)
			taskToRemove.interruptTask();
	}

	/**
	 * search in map the runnable to interrupt
	 * @param fileInfo
	 * @return
	 */
	private AbstractRunnable getTaskToRemove(ItemToDownload fileInfo) {
		// TODO Auto-generated method stub
		if(packageMap.containsKey(fileInfo.getPackageName())){
			return packageMap.get(fileInfo.getPackageName());
		}
		return null;
	}

	private static Looper createLooper(){

		if(executorThread == null){
			AcoLog.d("test", "Creating new handler thread");
			executorThread = new HandlerThread("ControllerService.Executor");
			executorThread.start();
		}
		return executorThread.getLooper();
	}

	/**
	 * handler che reppresenta la coda di processamento dei comandi; man mano che arrivano i comandi dall'interfaccia
	 * li inserisco in coda
	 * 
	 * @author Carmelo Fabio Iriti
	 *
	 */
	public static class CommandServiceExecutor extends Handler{
		WeakReference<ControllerServ> handlerService;

		public CommandServiceExecutor(ControllerServ s) {
			super(createLooper());
			this.handlerService = new WeakReference<ControllerServ>(s);
		}

		/**
		 * aggiunge task alla coda
		 * 
		 * @param task
		 */
		public void execute(Runnable task){
			ControllerServ s = handlerService.get();
			if(s != null)
				s.waveLoker.acquire(task);
			Message.obtain(this, 0/* don't care */, task).sendToTarget();
		}

		/**
		 * rimuove tutti gli oggetti dalla coda in attesa di essere utilizzati
		 */
		public void cancelAll(){
			removeCallbacksAndMessages(null);
		}

		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof Runnable){
				executeInternal((Runnable) msg.obj);
			}else {
				AcoLog.i("test", "can't handle msg: " + msg);
			}
		}

		private void executeInternal(Runnable task){
			try{
				task.run();
			}catch(Throwable t){
				AcoLog.e("Test", "run task: " + task, t);
			}finally{
				ControllerServ s = handlerService.get();
				if(s != null){
					s.waveLoker.release(task);
				}
			}
		}

	}

	/**
	 * Comunicazione fra thread e servizio
	 * @author root
	 *
	 */

	public static class ProcessCommunicationHandler extends Handler{

		WeakReference<ICommunicationService> commWeakRef;

		public ProcessCommunicationHandler(ICommunicationService comm) {
			super();
			this.commWeakRef = new WeakReference<ICommunicationService>(comm);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			ICommunicationService comm = commWeakRef.get();
			if(comm == null) return;

			AcoLog.i(TAG,"stopSelfResult msg.what["+msg.what+"]");

			switch (msg.what) {

			case WHAT_CURRENT_PROGRESS:
				if(msg.obj instanceof ItemCurrentProgress){
					comm.sendProgress((ItemCurrentProgress)msg.obj);
				}

				break;
			case WHAT_ERROR:
				if(msg.obj instanceof ItemErrorOccurred){
					comm.error((ItemErrorOccurred)msg.obj);
				}
				break;
			case WHAT_COMPLETED_DOWNLOAD:
				if(msg.obj instanceof ItemCurrentProgress){
					comm.successDownloaded((ItemCurrentProgress)msg.obj);
				}

				break;

			}
			comm.checkIfServiceNeedsToStop();
		}

	}

	/**
	 * restituisce l'oggetto che implementa la coda dei messaggi
	 * 
	 * @return
	 */
	public CommandServiceExecutor getSerialExecutor(){
		if(mExecutor == null)
			mExecutor = new CommandServiceExecutor(this);
		return mExecutor;
	}

	public ThreadPoolExecutor getParallelExecutor(){
		if(executor == null)
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREAD);
		return executor;
	}

	/** method for clients */
	public int getRandomNumber() {
		return mGenerator.nextInt(100);
	}



	@Override
	public void sendProgress(GenericItemBean resp) {
		updateObserver(resp);
		if(((ItemCurrentProgress)resp).getCurrentProgress() >= 100){
			packageMap.remove(((ItemCurrentProgress)resp).getPackageName());
		}
	}


	@Override
	public void checkIfServiceNeedsToStop() {
		boolean res = false;
		if(getParallelExecutor().getActiveCount() == 0){
			res = stopSelfResult(mStartId);
			AcoLog.i(TAG,"MultiDownload stopSelfResult[" + res +"]");
			packageMap.clear();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AcoLog.i(TAG,"MultiDownload stopSelfResult onDestroy()");
	}

	@Override
	public void startService() {

	}

	@Override
	public void error(GenericItemBean resp) {
		errorObserver(resp);
		packageMap.remove(((ItemErrorOccurred)resp).getPackageName());

	}

	@Override
	public void successDownloaded(GenericItemBean resp) {
		// TODO Auto-generated method stub
		succesDownlodDownloadObserver(resp);
		packageMap.remove(((ItemCurrentProgress)resp).getPackageName());
	}


}