package cn.edu.sdu.online.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.sdu.online.R;
import cn.edu.sdu.online.activity.LoginActivity;
import cn.edu.sdu.online.container.FragmentTabsPager;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class LoginService extends Service {
	String TAG = "LoginService";
	Timer timer;
	LoginTask loginTask;

	@Override
	public void onCreate() {
		Log.v(TAG,"LoginServiceonCreate()" );
		loginTask = new LoginTask();
		timer = new Timer();
		timer.schedule(loginTask, 1000 * 1800, 1000 * 1800);
	}

	class LoginTask extends TimerTask {

		@Override
		public void run() {
			Log.v(TAG,"LoginTask.run()" );
			SharedPreferences sharedPreferences = getSharedPreferences(
					getString(R.string.login_tag), Activity.MODE_PRIVATE);
			String email = sharedPreferences.getString(
					getString(R.string.login_email), "");
			String psw = sharedPreferences.getString(
					getString(R.string.login_password), "");
			Thread thread = new Thread(new LoginThread(email, psw));
			thread.start();

		}

	}

	class LoginThread implements Runnable {

		String email, password;

		public LoginThread(String email, String password) {
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

	private void setLoginAlready() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit()
				.putInt(getString(R.string.login_already),
						FloatApplication.ALREADY_LOGIN).commit();
		share = null;

	}

	private void saveSession(String jsessionid) {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit().putString(getString(R.string.jsessionid), jsessionid)
				.commit();
		share = null;
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
				Log.v(TAG, "LoginService登录成功，sessionId:"+NetCore.jsessionid);

				// 保存sessionID
				saveSession(NetCore.jsessionid);
				// 登录标识设为已登录
				setLoginAlready();

				break;

			default:
				break;
			}

		}
	};

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

}
