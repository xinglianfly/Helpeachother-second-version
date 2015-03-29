package cn.edu.sdu.online.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.container.FragmentTabsPager;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;

public class SplashActivity extends Activity {
	// location
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";// 国测局标准
	private LocationClient mLocationClient;
	private String TAG = "SplashActivity";
	boolean logined;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.splash_activity);
		// 定位
		initLocation();
		startLocation();
		// 如果已登录过，进行登录操作获取并更新sessionid

		logined = alreadyLogin();
		if (logined) {
			updateSessionId();
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if (logined) {
					//
					Log.v(TAG, "已登录，无需输入用户名密码");
					intent.setClass(SplashActivity.this,
							FragmentTabsPager.class);
				} else {
					intent.setClass(SplashActivity.this, LoginActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, 5000);

	}

	private void updateSessionId() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		String email = sharedPreferences.getString(
				getString(R.string.login_email), "");
		String psw = sharedPreferences.getString(
				getString(R.string.login_password), "");
		if (email.equals("") || psw.equals("")) {
			sharedPreferences
					.edit()
					.putInt(getString(R.string.login_already),
							FloatApplication.NEVER_LOGIN).commit();
			return;
		}
		// 开启登录线程
		Thread thread = new Thread(new loginThread(email, psw));
		thread.start();

	}

	private void initLocation() {
		// TODO Auto-generated method stub
		mLocationClient = FloatApplication.getApp().getInitBdLocation()
				.getmLocationClient();

	}

	private void startLocation() {
		setLocation();
		mLocationClient.start();

	}

	private void setLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;

		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 得到具体地址
		mLocationClient.setLocOption(option);
	}

	private boolean alreadyLogin() {
		boolean already = false;
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), FloatApplication.MODE_PRIVATE);
		int judgeLog = 0;
		judgeLog = share.getInt(getString(R.string.login_already), 0);
		if (judgeLog == 1) {
			already = true;
		}
		return already;

	}

	private int startLogin(String email, String password) {
		Log.v(TAG, "startConnect...:");
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		String jsonResult = new NetCore().Login(user);

		Log.v(TAG, "jsonResult:" + jsonResult);
		int result = 404;
		try {
			result = new JSONObject(jsonResult).getInt("result");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v(TAG, "loginresult:" + result);
		return result;
	}

	Handler loginHandler = new Handler() {
		public void handleMessage(Message message) {
			Log.v(TAG, "message.what" + message.what);
			switch (message.what) {

			case NetCore.LOGIN_SUCCESS:

				Log.v(TAG, "SplashAcitivity登录成功，sessionId:"
						+ NetCore.jsessionid);
				// 保存sessionID
				saveSession(NetCore.jsessionid);
				// 登录标识设为已登录
				setLoginAlready();

				// 得出用户信息并跳转
				// 开启线程

				break;
			case NetCore.LOGIN_ERROR:
				// 登录失败
				setLoginNever();
				break;

			default:
				break;
			}

		}
	};

	class loginThread implements Runnable {
		String email, password;

		public loginThread(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result = startLogin(email, password);
			Message message = new Message();
			message.what = result;

			loginHandler.sendMessage(message);
		}
	}

	protected void saveSession(String jsessionid) {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit().putString(getString(R.string.jsessionid), jsessionid)
				.commit();
		share = null;

	}

	protected void setLoginNever() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit()
				.putInt(getString(R.string.login_already),
						FloatApplication.NEVER_LOGIN).commit();
		share = null;

	}

	protected void setLoginAlready() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit()
				.putInt(getString(R.string.login_already),
						FloatApplication.ALREADY_LOGIN).commit();
		share = null;

	}

}
