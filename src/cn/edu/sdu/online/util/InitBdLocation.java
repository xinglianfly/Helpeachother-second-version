package cn.edu.sdu.online.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.share.FloatApplication;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.ap;

public class InitBdLocation {

	private LocationClient mLocationClient;
	public TextView tvLocation;
	
private Context context;
	public LocationClient getmLocationClient() {
		return mLocationClient;
	}

	public MyLocationListener mMyLocationListener;

	public InitBdLocation(Context context) {
		this.context = context;
		tvLocation = new TextView(context);
		
		
		mLocationClient = new LocationClient(context);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			tvLocation.setText(location.getAddrStr());
			FloatApplication.getApp().setStoreLocation(location);
			mLocationClient.stop();
			// 设置定位TAG为成功,并提醒SquareFragmente刷新列表
			if (location.getLocType()==161) {
				Toast.makeText(context, "刷新定位成功", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(context, "刷新定位失败", Toast.LENGTH_LONG).show();
			}
			
			// 存储位置信息到文件
			Log.i("BaiduLocationApiDem", sb.toString());
		}

		

	}

}
