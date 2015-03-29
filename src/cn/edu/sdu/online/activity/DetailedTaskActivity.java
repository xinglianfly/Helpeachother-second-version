package cn.edu.sdu.online.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.sdu.online.R;

import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.util.ConvertString;
import cn.edu.sdu.online.util.HeadPhotoUtil;
import cn.edu.sdu.online.view.ContactPopWindow;

public class DetailedTaskActivity extends Activity {
	String TAG = "DetailedTaskActivity";
	private ImageButton bt_help;
	private ImageView iv_faceimage, iv_sex;
	private RelativeLayout rela_back;
	private TextView tv_content, tv_nickname, tv_remarks, tv_destination,
			tv_location, tv_time, tv_timehave, tv_reward, tv_tel;
	Task task;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailed_taskinfo);
		initialize();
		rela_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});
		bt_help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ContactPopWindow contactpop = new ContactPopWindow(
						DetailedTaskActivity.this, task);
				contactpop.showPopupWindow(bt_help);
			}
		});

	}

	private void initialize() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		task = (Task) bundle.getSerializable("task");
		Log.v(TAG, task.getContent());
		bt_help = (ImageButton) this.findViewById(R.id.bt_help);
		rela_back = (RelativeLayout) this
				.findViewById(R.id.relativelayout_back);

		tv_nickname = (TextView) this.findViewById(R.id.tv_nickname);
		tv_nickname.setText(task.getNickName());
		// school = (TextView) this.findViewById(R.id.school);
		// school.setText("学校:" + task.getSchool());
		tv_tel = (TextView) findViewById(R.id.tv_tel);
		tv_tel.setText(task.getPhoneNo());
		tv_time = (TextView) this.findViewById(R.id.tv_time);
		String str = task.getLimitTime().toString();
		if (str != null) {
			String str1 = str.substring(0, 4);
			String str2 = str.substring(4, 6);
			String str3 = str.substring(6, 8);
			tv_time.setText(str1 + "-" + str2 + "-" + str3);
		} else
			tv_time.setText("长期有效");
		// lefttime.setText(task.getLimitTime()+"");
		tv_content = (TextView) this.findViewById(R.id.tv_content);
		tv_content.setText(task.getContent());
		tv_reward = (TextView) this.findViewById(R.id.tv_reward);
		if (task.getAwardStatus() == 0) {
			tv_reward.setText(task.getTipAward() + "元");
		}
		if (task.getAwardStatus() == 1) {
			tv_reward.setText(task.getSpiritAward());
		}
		tv_remarks = (TextView) findViewById(R.id.tv_remarks);
		tv_remarks.setText(task.getDetails());// 备注在哪里设置？？

		tv_destination = (TextView) findViewById(R.id.tv_destination);
		tv_destination.setText(task.getDestination());
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_location.setText(task.getLocation());
		tv_timehave = (TextView) findViewById(R.id.tv_timehave);

		iv_faceimage = (ImageView) findViewById(R.id.iv_head);
		HeadPhotoUtil.setOtherHeadPhoto(task.getUserId(), iv_faceimage, this);
		iv_sex = (ImageView) findViewById(R.id.iv_sex);
		int intsex = task.getSex();
		switch (intsex) {
		case 0:
			iv_sex.setImageResource(R.drawable.female);
			break;
		case 1:
			iv_sex.setImageResource(R.drawable.male);
			break;
		case 2:
			iv_sex.setImageResource(R.drawable.secret);
			break;
		default:
			break;
		}
		// detail = (TextView) this.findViewById(R.id.detail);
		// detail.setText("任务详情:" + task.getDetails());
		// change_tel = (TextView) findViewById(R.id.change_tel);
		// change_tel.setText(task.getPhoneNo());
		// grade = (TextView) findViewById(R.id.grade);
		// grade.setText("等级:" + task.getLevel() + "级");

	}

}
