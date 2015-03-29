package cn.edu.sdu.online.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;

public class CompleteContactActivity extends Activity implements OnClickListener {
	private EditText etPhone, etWechat, etQQ;
	private ImageView btComplete, btCancle;
	Dialog dialog;
	String phoneNo = "", wechat = "", qq = "";
	User user;
	String TAG = "CompleteContactActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete_contact);
		initUser();

		initView();

	}

	private void initUser() {
		user = FloatApplication.getApp().getUser(
				getString(R.string.userFileName));
		wechat = user.getWeixin();
		qq = user.getQq();

	}

	private void initView() {
		etPhone = (EditText) findViewById(R.id.et_phone);
		etWechat = (EditText) findViewById(R.id.et_wechat);
		if (wechat != null && !wechat.equals("")) {
			etWechat.setText(wechat);
		}
		etQQ = (EditText) findViewById(R.id.et_qq);
		if (qq != null && !qq.equals("")) {
			etQQ.setText(qq);
		}
		btComplete = (ImageView) findViewById(R.id.im_complete);
		btComplete.setTag(0);
		btComplete.setOnClickListener(this);
		btCancle = (ImageView) findViewById(R.id.im_cancle);
		btCancle.setTag(1);
		btCancle.setOnClickListener(this);
		dialog = DialogUtil.createLoadingDialog(this,
				getString(R.string.waitingDialog));

	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		switch (tag) {
		case 0:
			// 上传
			update();
			break;
		case 1:
			// 取消
			this.finish();
			break;

		default:
			break;
		}

	}

	private void update() {
		if (validate()) {
			// 开启dialog开启线程
			dialog.show();
			Thread thread = new Thread(new updateThread());
			thread.start();
		}

	}

	private boolean validate() {
		boolean canUpdate = false;
		phoneNo = etPhone.getText().toString().trim();
		wechat = etWechat.getText().toString().trim();
		qq = etQQ.getText().toString().trim();
		if (!phoneNo.equals("")) {
			canUpdate = true;
			user.setPhoneNo(phoneNo);
			user.setWeixin(wechat);
			user.setQq(qq);
		} else {
			Toast.makeText(this, "手机号为必填", Toast.LENGTH_LONG).show();
			canUpdate = false;

		}
		return canUpdate;
	}

	class updateThread implements Runnable {

		@Override
		public void run() {
			//
			String jsonData = new NetCore().Change_person_information(user);

			Message message = new Message();

			message.what = NetCore.NET_ERROR;

			try {
				JSONObject jsonObject = new JSONObject(jsonData);
				message.what = jsonObject.getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(TAG, "message.what:" + message.what);
			handler.sendMessage(message);

		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (dialog != null) {
				dialog.dismiss();
			}
			switch (message.what) {

			case NetCore.CHANGE_SUCCESS:
				Toast.makeText(getApplicationContext(), "更改成功",
						Toast.LENGTH_SHORT).show();
				// 更新缓存
				updateStoreUser();

				setResult(1);
				CompleteContactActivity.this.finish();
				break;
			case NetCore.CHANGE_ERROR:
				Toast.makeText(getApplicationContext(), "更改失败",
						Toast.LENGTH_SHORT).show();
				break;
			case NetCore.NET_ERROR:
				Toast.makeText(getApplicationContext(), "请检查网络",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};
	protected void updateStoreUser() {
		FloatApplication.getApp().setStoreUser(
				getString(R.string.userFileName), user);

	}

}
