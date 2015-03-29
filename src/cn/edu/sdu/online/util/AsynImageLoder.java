package cn.edu.sdu.online.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;

public class AsynImageLoder {
	// 线程池
	private ExecutorService executorService;
	private Context context;
	String TAG = "AsynImageLoder";

	public AsynImageLoder(Context context) {
		this.context = context;
		executorService = Executors.newFixedThreadPool(5);

	}

	public void disPlayImage(ImageView imageView, String userId) {
		executorService.submit(new GetPicture(imageView, userId));

	}

	class GetPicture implements Runnable {
		ImageView imageView;
		String userId;

		public GetPicture(ImageView imageView, String userId) {
			this.imageView = imageView;
			this.userId = userId;
		}

		@Override
		public void run() {
			// 先从缓存中获得图片
			Bitmap headPhoto = null;

			headPhoto = FloatApplication.getApp().getHeadphoto(
					FloatApplication.HEAD_PHOTOS + "/" + userId + ".jpg");
			Log.v(TAG, "获取头像");
			System.out.println("获取头像");
			if (headPhoto == null) {
				headPhoto = new NetCore().DownloadPicture(userId);
				Log.v(TAG, "缓存为空，从网络下载头像");
				System.out.println("缓存为空，从网络下载头像");
				if (headPhoto != null) {
					FloatApplication.getApp().JPsetStoreheadphto(
							headPhoto,
							FloatApplication.HEAD_PHOTOS + "/" + userId
									+ ".jpg");
				}

			}

			if (headPhoto == null) {
				Log.v(TAG, "未获得用户头像,用户未设置或网络出错");
				System.out.println("未获得用户头像,用户未设置或网络出错");
				return;
			}
			// 缓存图片
			Activity activity = (Activity) context;
			activity.runOnUiThread(new SetBitMapOnUi(imageView, headPhoto));

		}

	}

	class SetBitMapOnUi implements Runnable {

		ImageView imageView;
		Bitmap bitmap;

		public SetBitMapOnUi(ImageView imageView, Bitmap bitmap) {
			this.imageView = imageView;
			this.bitmap = bitmap;
		}

		@Override
		public void run() {
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}

		}

	}

}
