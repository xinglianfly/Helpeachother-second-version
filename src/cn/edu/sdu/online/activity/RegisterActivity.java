package cn.edu.sdu.online.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.adapter.MyArrayAdapter;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.util.DialogUtil;

public class RegisterActivity extends Activity {
	String TAG = "REGISTER_ACTIVITY";
	// 组件
	EditText editText_email, editText_password, editText_password_again;
	Button button_complete;
	// Dialog
	Dialog dialog;
	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initialization();
		setListeners();

	}

	// 初始化方法
	private void initialization() {
		// email-----------------------------------------------------------------------------
		editText_email = (EditText) this.findViewById(R.id.et_mail_re);

		// password--------------------------------------------------------------------------
		editText_password = (EditText) this.findViewById(R.id.et_passwold_re);
		// password_again--------------------------------------------------------------------
		editText_password_again = (EditText) this.findViewById(R.id.et_confirm);
		// complete
		// button------------------------------------------------------------------------------------
		button_complete = (Button) findViewById(R.id.register_complete);

	}

	private void setListeners() {
		button_complete.setOnClickListener(new OnClickListener() // 按钮监听
				{

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String email = editText_email.getText().toString();
						String password = editText_password.getText()
								.toString();
						String password_again = editText_password_again
								.getText().toString();
						validateAndRegister(email, password, password_again);

					}
				});
	}

	Handler registerHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 取消dialog
			if (dialog != null) {
				Log.v(TAG, "dialog消失");
				dialog.dismiss();
			}
			switch (msg.what) {
			case NetCore.REGISTER_ALREADY:
				// 已经被注册
				Toast.makeText(RegisterActivity.this,
						getString(R.string.registerAlready), Toast.LENGTH_SHORT)
						.show();
				break;
			case NetCore.REGISTER_SUCCEESS:
				// 注册成功
				Log.v(TAG, "注册成功弹出toast");
				Toast.makeText(RegisterActivity.this,
						getString(R.string.registerSuccess), Toast.LENGTH_SHORT)
						.show();
				// 直接登录
				RegisterActivity.this.finish();
				break;
			case NetCore.REGISTER_FAILE:
				// 注册失败
				Toast.makeText(RegisterActivity.this,
						getString(R.string.registerFaild), Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		}
	};

	// 验证格式是否正确
	private void validateAndRegister(String email, String password,
			String password_again) {
		// 判断输入是否为空
		if (email.equals("") || password.equals("")
				|| password_again.equals("")) {
			// 为空
			Toast.makeText(RegisterActivity.this,
					this.getString(R.string.register_toast_empty),
					Toast.LENGTH_LONG).show();
		} else// 不为空
		{
			// 判断email是否有效
			Pattern pattern = Pattern
					.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			Matcher matcher = pattern.matcher(email);
			// email无效
			if (!matcher.matches()) {
				Toast.makeText(RegisterActivity.this,
						this.getString(R.string.register_toast_emailerror),
						Toast.LENGTH_LONG).show();
			}
			// //email有效
			else {
				// 判断密码是否一致
				// 不一致
				if (!password.equals(password_again)) {
					Toast.makeText(RegisterActivity.this,
							this.getString(R.string.register_toast_error),
							Toast.LENGTH_LONG).show();

				}
				// 密码一致
				else {
					// 开始注册
					startRegister(email, password);
				}

			}

		}
	}

	private void startRegister(String email, String password) {
		// 打开对话框
		dialog = DialogUtil.createLoadingDialog(this,
				this.getString(R.string.waitingDialog));
		dialog.show();
		// 开启线程
		Thread thread = new Thread(new RegisterThread(email, password));
		thread.start();

	}

	class RegisterThread implements Runnable {
		String email, password;

		public RegisterThread(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		public void run() {
			User user = new User();
			user.setEmail(email);
			user.setPassword(password);
			String resultStr = new NetCore().Registe(user);
			Log.v(TAG, "resultStr:" + resultStr);
			int result = 404;// 404为请求数据失败
			try {
				result = new JSONObject(resultStr).getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = result;
			registerHandler.sendMessage(message);

		}

	}

}
