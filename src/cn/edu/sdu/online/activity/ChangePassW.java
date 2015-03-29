package cn.edu.sdu.online.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;

public class ChangePassW extends Activity implements OnClickListener,OnCheckedChangeListener {
	private EditText text1;
	private EditText text2;
	private EditText text3;
	private CheckBox checkDisplay;
	private Button back;
	private ImageView baocun;
	Dialog dialog;
	User user = new User();
	String newpassword;
	String TAG = "ChangePassW";
	private final static int CHANGE_SUCCESS = 1;// 改变成功
	private final static int CHANGE_ERROR = 0;// 改变失败
	private final static int CHANGE_DENY = 2;// 与原密码不一致

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pass_w);
		initialization();
	}

	private void initialization() {
		text1 = (EditText) this.findViewById(R.id.et_oldpass);
		text2 = (EditText) this.findViewById(R.id.et_newpass);
		text3 = (EditText) this.findViewById(R.id.et_confirmnewpass);
		
		baocun = (ImageView) this.findViewById(R.id.im_save);
		back = (Button)this.findViewById(R.id.bt_back);
		back.setOnClickListener(this);
		baocun.setOnClickListener(this);
		checkDisplay = (CheckBox)findViewById(R.id.cb_display);
		checkDisplay.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		String oldPass = text1.getText().toString();
		String newPass = text2.getText().toString();
		String newconfirm = text3.getText().toString();
		switch (v.getId()) {
		case R.id.im_save:
			if (oldPass.trim().length() == 0 || newPass.trim().length() == 0
					|| newconfirm.trim().length() == 0) {
				Toast.makeText(this, "不能为空", 1000).show();
				return;
			}
			if (!newPass.equals(newconfirm)) {
				Toast.makeText(this, "前后密码不一致", 1000).show();
				return;
			}

			// else if (str1 != "           ...")// 原先的密码
			// Toast.makeText(this, "原密码输入不正确", 1000).show();

			// else {
			// Toast.makeText(this, "修改成功", 1000).show();
			// this.finish();
			// }
			// 打开对话框
			user.setId(FloatApplication.getApp()
					.getUser(getString(R.string.userFileName)).getId());
			user.setPassword(oldPass);
			newpassword = newPass;
			
			dialog =  DialogUtil.createLoadingDialog(this,"修改中...");
			dialog.show ();
			Thread thread = new Thread(new ChangeThread());
			thread.start();
			break;
		case R.id.bt_back:
			this.finish();
			break;
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if(dialog!=null){
				dialog.dismiss();
			}
			switch (message.what) {
			case CHANGE_SUCCESS:
				Toast.makeText(ChangePassW.this, "修改成功", Toast.LENGTH_SHORT)
						.show();
				//设置未登陆
				setLoginTag();
				Intent intent2 = new Intent(ChangePassW.this,LoginActivity.class);
				startActivity(intent2);
				ChangePassW.this.finish();
				break;
			case CHANGE_DENY:
				Toast.makeText(ChangePassW.this, "原密码输入错误", Toast.LENGTH_SHORT)
						.show();
				break;
			case CHANGE_ERROR:
				Toast.makeText(ChangePassW.this, "修改失败", Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		}
	};
	private void setLoginTag() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Context.MODE_PRIVATE);
		share.edit()
				.putInt(getString(R.string.login_already),
						FloatApplication.NEVER_LOGIN).commit();
		share=null;

	}
	private class ChangeThread implements Runnable {

		@Override
		public void run() {
			String jsonReSult = new NetCore().ChangePassword(user, newpassword);
			Log.v(TAG, "jsonReSult"+jsonReSult);
			Message message = new Message();
			message.what = 404;
			try {
				JSONObject jsonObject =new JSONObject(jsonReSult);
				
				message.what = jsonObject.getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.sendMessage(message);
			
			
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_display:
			Log.v(TAG, "isChecked:"+isChecked);
			break;

		default:
			break;
		}
		
		
	}
}
