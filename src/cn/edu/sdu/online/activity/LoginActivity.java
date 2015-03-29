package cn.edu.sdu.online.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.container.FragmentTabsPager;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.ConvertString;
import cn.edu.sdu.online.util.DialogUtil;

import com.tencent.tauth.Tencent;

public class LoginActivity extends Activity implements View.OnClickListener {
	private String TAG = "LoginActivity";

	private double screenWidth, screenHight, density;

	// 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误
	private boolean isNetError;

	// 控件;
	Button loginButton, registerButton;
	EditText editEmail, editPass;
	CheckBox checkRemember;
	Dialog dialog;

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		findView();

		getRememberMe(checkRemember.isChecked());

	}

	// 初始化控件
	private void findView() {
		loginButton = (Button) findViewById(R.id.bt_login);
		loginButton.setOnClickListener(this);
		loginButton.setTag(1);
		registerButton = (Button) findViewById(R.id.bt_register);
		registerButton.setOnClickListener(this);
		registerButton.setTag(2);
		editEmail = (EditText) findViewById(R.id.et_mail);
		editPass = (EditText) findViewById(R.id.et_passwold);

		checkRemember = (CheckBox) findViewById(R.id.cb_remember);
		checkRemember.setChecked(true);// 默认记住密码

	}

	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	// 监听
	public void onClick(View v) {
		int tag = (Integer) v.getTag();

		switch (tag) {
		case 1:
			String email = editEmail.getText().toString();
			String passWord = editPass.getText().toString();
			// 开启对话框
			dialog = DialogUtil.createLoadingDialog(this,
					getString(R.string.waitingDialog));
			dialog.show();
			Thread loginTh = new Thread(new loginThread(email, passWord));
			loginTh.start();

			break;
		case 2:

			Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(it);

			break;

		}

	}

	/** 读取记住的用户名和密码 */
	private void getRememberMe(boolean isRemember) {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), 0);
		String email = share.getString(getString(R.string.login_email), "");
		String password = share.getString(getString(R.string.login_password),
				"");
		if (!"".equals(email)) {
			editEmail.setText(email);
		}
		if (!"".equals(password)) {
			editPass.setText(password);
			checkRemember.setChecked(true);
		}

		share = null;

	}

	/** 清除密码 */
	private void clearSharePassword() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), 0);
		share.edit().putString(getString(R.string.login_password), "").commit();
		share = null;
	}

	/**
	 * 如果登录成功过,则将登陆用户名和密码记录在SharePreferences
	 * 
	 * @param saveUserName
	 *            是否将用户名保存到SharePreferences
	 * @param savePassword
	 *            是否将密码保存到SharePreferences
	 * */

	private void saveSharePreferences(boolean saveUser) {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), 0);
		if (saveUser) {
			share.edit()
					.putString(getString(R.string.login_password),
							editPass.getText().toString()).commit();
			share.edit()
					.putString(getString(R.string.login_email),
							editEmail.getText().toString()).commit();
		} else {
			clearSharePassword();
		}

		share = null;
	}

	// //

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

	private void saveSession(String jsessionid) {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit().putString(getString(R.string.jsessionid), jsessionid)
				.commit();
		share = null;
	}

	private void setLoginAlready() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Activity.MODE_PRIVATE);
		share.edit()
				.putInt(getString(R.string.login_already),
						FloatApplication.ALREADY_LOGIN).commit();
		share = null;

	}

	Handler loginHandler = new Handler() {
		public void handleMessage(Message message) {
			Log.v(TAG, "message.what" + message.what);
			switch (message.what) {

			case NetCore.LOGIN_SUCCESS:

				// 登录成功
				if (dialog != null) {
					dialog.dismiss();
				}

				saveSharePreferences(checkRemember.isChecked());
				// 保存sessionID
				saveSession(NetCore.jsessionid);
				// 登录标识设为已登录
				setLoginAlready();

				Toast.makeText(LoginActivity.this,
						getString(R.string.loginSuccess), Toast.LENGTH_SHORT)
						.show();
				// 得出用户信息并跳转
				// 开启线程
				Intent intent1 = new Intent();
				intent1.setClass(LoginActivity.this, FragmentTabsPager.class);
				startActivity(intent1);
				LoginActivity.this.finish();

				break;
			case NetCore.LOGIN_ERROR:
				// 登录失败
				if (dialog != null) {
					dialog.dismiss();
				}
				Toast.makeText(LoginActivity.this,
						getString(R.string.loginFail), Toast.LENGTH_SHORT)
						.show();
				clearSharePassword();
				break;
			case NetCore.NET_ERROR:
				// 登录失败
				if (dialog != null) {
					dialog.dismiss();
				}
				Toast.makeText(LoginActivity.this,
						getString(R.string.netError), Toast.LENGTH_SHORT)
						.show();

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

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}

}
