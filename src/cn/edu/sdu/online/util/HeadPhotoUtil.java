package cn.edu.sdu.online.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;

public class HeadPhotoUtil {
	static String TAG = "HeadPhotoUtil";
	public static void setMyHeadPhoto(String userId, ImageView imageView,
			Context context) {
		Bitmap headPhoto = null;
		headPhoto = FloatApplication.getApp().getHeadphoto(
				FloatApplication.APP_DIR + "/" + userId + ".jpg");
		if (headPhoto != null) {
			imageView.setImageBitmap(headPhoto);
			Log.v(TAG, "从缓存中获取头像");
		} else {
			// 开启线程获得头像
			Log.v(TAG, "缓存不存在，从网络获取头像");
			Thread thread = new Thread(new GetMyHeadThread(userId, imageView,context));
			thread.start();
		}

	}
	public static void setOtherHeadPhoto(String userId, ImageView imageView,
			Context context) {
		Bitmap headPhoto = null;
		headPhoto = FloatApplication.getApp().getHeadphoto(
				FloatApplication.HEAD_PHOTOS + "/" + userId + ".jpg");
		if (headPhoto != null) {
			imageView.setImageBitmap(headPhoto);
		} else {
			// 开启线程获得头像
			Thread thread = new Thread(new GetOtherHeadThread(userId, imageView,context));
			thread.start();
		}

	}

	static class GetMyHeadThread implements Runnable {
		String userId;
		ImageView imageView;
		Context context;

		public GetMyHeadThread(String userId, ImageView imageView,Context context) {
			this.userId=userId;
			this.imageView = imageView;
			this.context = context;

		}

		@Override
		public void run() {
			Bitmap bt = new NetCore().DownloadPicture(userId);
			if (bt != null) {
				String path = FloatApplication.APP_DIR+"/"+userId+".jpg";
				FloatApplication.getApp().JPsetStoreheadphto(bt, path);
				Activity activity = (Activity)context;
				activity.runOnUiThread(new SetHeadBitmap(bt, imageView));
			}
		}

	}
	static class GetOtherHeadThread implements Runnable {
		String userId;
		ImageView imageView;
		Context context;

		public GetOtherHeadThread(String userId, ImageView imageView,Context context) {
			this.userId=userId;
			this.imageView = imageView;
			this.context = context;

		}

		@Override
		public void run() {
			Bitmap bt = new NetCore().DownloadPicture(userId);
			if (bt != null) {
				String path = FloatApplication.HEAD_PHOTOS+"/"+userId+".jpg";
				FloatApplication.getApp().JPsetStoreheadphto(bt, path);
				Activity activity = (Activity)context;
				activity.runOnUiThread(new SetHeadBitmap(bt, imageView));
			}
		}

	}
	static class SetHeadBitmap implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		public SetHeadBitmap(Bitmap bitmap,ImageView imageView) {
			this.bitmap = bitmap;
			this.imageView = imageView;
		}

		@Override
		public void run() {
			imageView.setImageBitmap(bitmap);

		}

	}

}
