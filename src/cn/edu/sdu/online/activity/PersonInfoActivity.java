package cn.edu.sdu.online.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.HeadPhotoUtil;

import com.umeng.fb.util.Log;

public class PersonInfoActivity extends Activity implements OnClickListener {
	private FloatApplication app;
	String TAG = "PersonInfoActivity";
	RelativeLayout back;
	Button publish_button_change;
	ImageView info_head;
	ImageView info_head_small;
	ImageView info_img_sex;
	ImageView exitLogin;

	TextView info_email, info_name, info_birthday, info_phone, info_campus,
			info_wechat, info_count_publish, info_count_complement;

	@Override
	protected void onResume() {
		// 从缓存中重新获得user并设置
		setInfo();
		super.onResume();
	}

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_person_info);

		initialize();// 初始化

	}

	public void initialize() {
		back = (RelativeLayout) this.findViewById(R.id.relativelayout_back);// 初始化返回按钮
		back.setOnClickListener(this);
		exitLogin = (ImageView)this.findViewById(R.id.exit_login);
		exitLogin.setOnClickListener(this);

		publish_button_change = (Button) this
				.findViewById(R.id.publish_button_change);// 初始化编辑按钮
		publish_button_change.setOnClickListener(this);

		// 初始化各种信息
		info_head = (ImageView) findViewById(R.id.personal_info_head);
		info_head_small = (ImageView) findViewById(R.id.person_info_head_small);
		info_img_sex = (ImageView) findViewById(R.id.img_info_sex);

		info_email = (TextView) findViewById(R.id.tv_account);
		info_name = (TextView) findViewById(R.id.tx_info_name);
		info_birthday = (TextView) findViewById(R.id.tx_info_birthday);
		info_phone = (TextView) findViewById(R.id.tx_info_phone);
		info_campus = (TextView) findViewById(R.id.tx_info_campus);
		info_wechat = (TextView) findViewById(R.id.tx_info_wechat);

		info_count_publish = (TextView) findViewById(R.id.info_count_piblish);
		// info_count_complement=(TextView)findViewById(R.id.info_count_complement);

		// 得到application对象

		app = (FloatApplication) getApplication();
		info_count_publish = (TextView) findViewById(R.id.info_count_piblish);
		setInfo();

	}

	private String getBirthFormat(String birth) {
		// TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), birth, Toast.LENGTH_SHORT)
		// .show();
		String birthFormat = null;
		if (birth != null && !birth.equals("")) {
			String year = birth.substring(0, 4);
			String month = birth.substring(4, 6);
			String day = birth.substring(6, 8);
			birthFormat = year + "年" + month + "月" + day + "日";
			return birthFormat;
		} else {
			return "请完善生日信息";
		}
	}

	private void setInfo() {
		// 信息得到
		// head=app.getHeadphoto(path);
		User user = app.getUser(getString(R.string.userFileName));

		int info_sex = user.getSex();
		// //赋值头像
		HeadPhotoUtil.setMyHeadPhoto(user.getId(), info_head, this);
		HeadPhotoUtil.setMyHeadPhoto(user.getId(), info_head_small, this);

		// 赋值各种信息
		String nickName = user.getNickName();
		if (nickName == null || nickName.equals(""))
			info_name.setText("请完善昵称信息");
		else
			info_name.setText(nickName);
		info_email.setText(user.getEmail());
		String phone = user.getPhoneNo();
		if (phone == null || phone.equals(""))
			info_phone.setText("请完善手机信息");
		else
			info_phone.setText(phone);
		String compus = user.getSchool();
		if (compus == null || compus.equals(""))
			info_campus.setText("请完善学校信息");
		else
			info_campus.setText(compus);
		String wechat = user.getWeixin();
		if (wechat == null || wechat.equals(""))
			info_wechat.setText("请完善微信信息");
		else
			info_wechat.setText(wechat);

		if (info_sex == 0) {
			info_img_sex.setImageDrawable(getResources().getDrawable(
					R.drawable.female));
		} else if (info_sex == 1) {
			info_img_sex.setImageDrawable(getResources().getDrawable(
					R.drawable.male));
		} else if (info_sex == 2) {
			info_img_sex.setImageDrawable(getResources().getDrawable(
					R.drawable.secret));
		}
		// else
		info_birthday.setText(getBirthFormat(user.getBirthday()));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.relativelayout_back:
			// 返回

			this.finish();
			break;
		case R.id.publish_button_change:
			// 进入编辑信息界面
			// setResult(2);
			Intent intent = new Intent();
			intent.setClass(this, ChangePersonActivity.class);
			startActivity(intent);

			break;
		case R.id.exit_login:
			//设置未登陆
			setLoginTag();
			FloatApplication.getApp().getFtPagerActivity().finish();
			Intent intent2 = new Intent(this,LoginActivity.class);
			startActivity(intent2);
			this.finish();
		default:
			break;
		}

	}

	private void setLoginTag() {
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), Context.MODE_PRIVATE);
		share.edit()
				.putInt(getString(R.string.login_already),
						FloatApplication.NEVER_LOGIN).commit();
	
		
		share=null;

	}

}
