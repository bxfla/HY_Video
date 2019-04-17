package com.sdhy.video.client;
/**
 * 软件版本自动更新
 * 
 * */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

//import com.sdhy.video.client.R;
     public class UpdateVersionManager {
		private Context mContext;
		//提示语
		private String updateMsg = "最新的软件包，快下载吧~";
		//返回的安装包url
		private String apkUrl = "/HyAndroidApp.apk";
		private Dialog noticeDialog;
		private Dialog downloadDialog;
		 /* 下载包安装路径 */
	    private static  String savePath = "/sdcard/updatedemo/";
	    private static  String saveFileName = "";

	    /* 进度条与通知ui刷新的handler和msg常量 */
	    private ProgressBar mProgress;
	    private static final int DOWN_UPDATE = 1;
	    private static final int DOWN_OVER = 2;
	    private int progress;
	    private Thread downLoadThread;
	    private boolean interceptFlag = false;
	    private Handler mHandler = new Handler(){
	    	@Override
			public void handleMessage(Message msg) {
	    		switch (msg.what) {
				case DOWN_UPDATE:
					mProgress.setProgress(progress);
					break;
				case DOWN_OVER:
					
					installApk();
					break;
				default:
					break;
				}
	    	};
	    };
	    
		public UpdateVersionManager(Context context) {
			this.mContext = context;
		}
		
		//外部接口让主Activity调用
		public void checkUpdateInfo(String url,int ver ){
			int oldVer=getVersionCode();
			apkUrl =url+apkUrl;
			 if(ver>oldVer){
				//有新版本需要更新
				showNoticeDialog();
			}
			
		}
		
		
		private void showNoticeDialog(){
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setTitle("软件版本更新");
			builder.setMessage(updateMsg);
			builder.setPositiveButton("下载", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showDownloadDialog();			
				}
			});
			builder.setNegativeButton("以后再说", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();				
				}
			});
			noticeDialog = builder.create();
			noticeDialog.show();
		}
		
		private void showDownloadDialog(){
		 	AlertDialog.Builder builder = new Builder(mContext);
			builder.setTitle("软件版本更新");
			
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			View v = inflater.inflate(R.layout.progress, null);
			mProgress = (ProgressBar)v.findViewById(R.id.progress);
			
			builder.setView(v);
			builder.setNegativeButton("取消", new OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					interceptFlag = true;
				}
			});
			downloadDialog = builder.create();
			downloadDialog.show(); 
			
			downloadApk();
		}
		//APK下载线程
		private Runnable mdownApkRunnable = new Runnable() {	
			@Override
			public void run() {
				try {
					 // 判断SD卡是否存在，并且是否具有读写权限  
	                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))  
	                { 
	                	 // 获得存储卡的路径  
	                    String sdpath = Environment.getExternalStorageDirectory() + "/";  
	                    savePath = sdpath + "download";  
	                    URL url = new URL(apkUrl);
	    				
						HttpURLConnection conn = (HttpURLConnection)url.openConnection();
						conn.connect();
						int length = conn.getContentLength();
						InputStream is = conn.getInputStream();
						
						File file = new File(savePath);
						if(!file.exists()){
							file.mkdir();
						}
						saveFileName=savePath + "/视频客户端.apk";
						String apkFile = saveFileName;
						File ApkFile = new File(apkFile);
						FileOutputStream fos = new FileOutputStream(ApkFile);
						int count = 0;
						byte buf[] = new byte[1024];
						
						do{   		   		
				    		int numread = is.read(buf);
				    		count += numread;
				    	    progress =(int)(((float)count / length) * 100);
				    	    //更新进度
				    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
				    		if(numread <= 0){	
				    			//下载完成通知安装
				    			mHandler.sendEmptyMessage(DOWN_OVER);
				    			break;
				    		}
				    		fos.write(buf,0,numread);
				    	}while(!interceptFlag);//点击取消就停止下载.
						fos.close();
						is.close();
	                }
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch(IOException e){
					e.printStackTrace();
				}
				
			}
		};
		/** 
		 * 获取软件版本号 
		 *  
		 * @param context 
		 * @return 
		 */  
		private int getVersionCode( )  
		{  
		    int versionCode = 0;  
		    try  
		    {  
		        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode  
		        versionCode = mContext.getPackageManager().getPackageInfo("com.sdhy.video.client", 0).versionCode;  
		    } catch (NameNotFoundException e)  
		    {  
		        e.printStackTrace();  
		    }  
		    return versionCode;  
		}	
		 /**
	     * 下载apk
	     * @param url
	     */
		
		private void downloadApk(){
			downLoadThread = new Thread(mdownApkRunnable);
			downLoadThread.start();
		}
		 /**
	     * 安装apk
	     * @param url
	     */
		private void installApk(){
			File apkfile = new File(saveFileName);
	        if (!apkfile.exists()) {
	            return;
	        } 
	        noticeDialog.cancel();
	        Intent i = new Intent(Intent.ACTION_VIEW);
	        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
	        mContext.startActivity(i);
		
		}
	}
