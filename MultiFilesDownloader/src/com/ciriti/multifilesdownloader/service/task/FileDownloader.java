package com.ciriti.multifilesdownloader.service.task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.os.Handler;

import com.ciriti.multifilesdownloader.bean.ItemCurrentProgress;
import com.ciriti.multifilesdownloader.bean.ItemErrorOccurred;
import com.ciriti.multifilesdownloader.bean.ItemToDownload;
import com.ciriti.multifilesdownloader.service.ControllerServ;
import com.ciriti.multifilesdownloader.util.AcoLog;
import com.ciriti.multifilesdownloader.util.Utils;

/**
 * 
 * @author carmelo.iriti
 *
 */
public class FileDownloader extends AbstractRunnable {

	private ItemToDownload fileInfo;
	private ItemCurrentProgress itemCurrentProgress;


	public static final int TIMEOUT_CONNECTION = 5000;

	public static FileDownloader newInstance(Handler handler, ItemToDownload fileInfo){
		FileDownloader instance = new FileDownloader(handler, fileInfo);
		return instance;
	}

	private FileDownloader(Handler handler, ItemToDownload fileInfo) {
		super(handler);
		this.fileInfo = fileInfo;
		itemCurrentProgress = new ItemCurrentProgress(this.fileInfo.getPackageName(), fileInfo.getName(), 0);
	}

	@Override
	protected void doRun() throws Exception {
		AcoLog.i(tag, "FileDownloader PackageName[" + fileInfo.getPackageName() +"]" + " Strart download!");
		/**
		 * Start the download of the request file
		 */
		downloadFileAndTraceProgress(fileInfo);
	}

	private void downloadFileAndTraceProgress(ItemToDownload fileInfo) {
		//		sendCurrentInfo(fileInfo.getPackageName(), 0);
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(fileInfo.getUrl());
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(TIMEOUT_CONNECTION);
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				//				return "Server returned HTTP " + connection.getResponseCode()
				//						+ " " + connection.getResponseMessage();
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();

			// download the file
			input = connection.getInputStream();
			output = new FileOutputStream(itemCurrentProgress.getAbsolutePathFile());

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			int percent = 0;
			int percentOld = -1;
			while ((count = input.read(data)) != -1 && !interruptTask) {
				total += count;
				percent = (int) (total * 100 / fileLength);
				// publishing the progress....
				if (fileLength > 0 && percent > percentOld){
					// only if total length is known
					sendCurrentProgress(fileInfo.getPackageName(), percent);
				}

				output.write(data, 0, count);
				percentOld = percent;
			}
			
			if(interruptTask){
				throw new InterruptedException("Download " + fileInfo.getName() + " interrupted!!!");
			}
			else if(!Utils.renameFileComplete(itemCurrentProgress)){
				throw new Exception("Somthings goes wrong!!");
			}
			else{
				completedDownload(fileInfo.getPackageName(), percent);
			}
			
		} catch(SocketTimeoutException se){

			sendErrorMessageOccurred(se.getMessage()==null?"":se.getMessage());

		}catch(InterruptedException ie){

			sendInterruptedErrorMessageOccurred(ie.getMessage()==null?"":ie.getMessage());

		} catch (Exception e) {
			sendErrorMessageOccurred(e.getMessage()==null?"":e.getMessage());
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}

	}

	private void sendErrorMessageOccurred(String errorMsg){
		sendMessage(handler.obtainMessage(ControllerServ.WHAT_ERROR, 
				new ItemErrorOccurred(itemCurrentProgress.getPackageName(), itemCurrentProgress.getName(), errorMsg)));
	}
	private void sendInterruptedErrorMessageOccurred(String errorMsg){
		sendMessage(handler.obtainMessage(ControllerServ.WHAT_ERROR, 
				new ItemErrorOccurred(itemCurrentProgress.getPackageName(), itemCurrentProgress.getName(), errorMsg, true)));
	}

	private void sendCurrentProgress(String idPackageName, int currentProgress) {
		//Set the current value
		itemCurrentProgress.setCurrentProgress(currentProgress);
		// Send the value
		sendMessage(handler.obtainMessage(ControllerServ.WHAT_CURRENT_PROGRESS, itemCurrentProgress));
	}

	private void completedDownload(String idPackageName, int currentProgress){
		//Set the current value
		itemCurrentProgress.setCurrentProgress(currentProgress);
//		itemCurrentProgress.setAbsolutePathFile(Utils.buildPathFile(itemCurrentProgress.getName()));
		// Send the value
		sendMessage(handler.obtainMessage(ControllerServ.WHAT_COMPLETED_DOWNLOAD, itemCurrentProgress));
	}

}
