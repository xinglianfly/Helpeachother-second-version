package cn.edu.sdu.online.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.LocationClient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Place;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.util.PublishDestinatePlace;

public class PublishTaskActivity extends Activity implements OnClickListener {

	String TAG = "PublishTaskActivity";

	RelativeLayout back;// 返回键
	ImageView location;// 定位键
	EditText etDescribe, etDetail;
	RelativeLayout ralative_Money, ralative_Spirit;
	ImageView radioMoney, radioSpirit;
	EditText etMoney, etSpirit;
	TextView tvDestination, tvLocation, tvExpire;
	ImageView imComplete;
	Dialog waitDialog;
	// 定位
	private LocationClient mLocationClient;

	// task
	Task task = new Task();
	static String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_publish_task);
		initView();
		initLocation();
		setTime();

	}

	private void initLocation() {
		// TODO Auto-generated method stub
		mLocationClient = FloatApplication.getApp().getInitBdLocation()
				.getmLocationClient();

		FloatApplication.getApp().getInitBdLocation().tvLocation = tvLocation;
	}

	private void initView() {
		task.setAwardStatus(0);
		back = (RelativeLayout) findViewById(R.id.relative_back);
		back.setTag(0);
		back.setOnClickListener(this);
		location = (ImageView) findViewById(R.id.location_pub);
		location.setTag(1);
		location.setOnClickListener(this);
		etDescribe = (EditText) findViewById(R.id.et_describe);
		etDescribe
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {

						if (hasFocus) {
							Toast.makeText(getApplicationContext(),
									"私密性较强的任务尽量不要交给别人完成哦~", Toast.LENGTH_SHORT)
									.show();
						}// TODO Auto-generated method stub
						else {
						}

					}
				});
		etDetail = (EditText) findViewById(R.id.et_detail);
		radioMoney = (ImageView) findViewById(R.id.im_radiobutton);
		ralative_Money = (RelativeLayout) findViewById(R.id.relativeLayout1);
		ralative_Money.setTag(2);
		ralative_Money.setOnClickListener(this);
		radioSpirit = (ImageView) findViewById(R.id.im_radiobutton2);
		ralative_Spirit = (RelativeLayout) findViewById(R.id.relativeLayout2);
		ralative_Spirit.setTag(3);
		ralative_Spirit.setOnClickListener(this);
		etMoney = (EditText) findViewById(R.id.et_money);
		etMoney.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {
					Toast.makeText(getApplicationContext(), "本应用不要大额交易哦~",
							Toast.LENGTH_SHORT).show();
					etMoney.setBackgroundColor(Color.rgb(255, 221, 98));
					etMoney.setTextColor(Color.WHITE);
				}// TODO Auto-generated method stub
				else {
					etMoney.setBackgroundColor(Color.rgb(255, 255, 255));
					etMoney.setTextColor(Color.BLACK);
				}

			}
		});
		etSpirit = (EditText) findViewById(R.id.et_spirit);
		etSpirit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {
					etSpirit.setBackgroundColor(Color.rgb(255, 168, 195));
					etSpirit.setTextColor(Color.WHITE);
				}// TODO Auto-generated method stub
				else {
					etSpirit.setBackgroundColor(Color.rgb(255, 255, 255));
					etSpirit.setTextColor(Color.BLACK);
				}

			}
		});
		tvDestination = (TextView) findViewById(R.id.et_destination);
		tvDestination.setTag(4);
		tvDestination.setOnClickListener(this);
		tvLocation = (TextView) findViewById(R.id.et_location);
		setLocation();
		tvExpire = (TextView) findViewById(R.id.tv_limittime);
		tvExpire.setTag(5);
		tvExpire.setOnClickListener(this);
		imComplete = (ImageView) findViewById(R.id.im_complete);
		imComplete.setTag(6);
		imComplete.setOnClickListener(this);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		switch (tag) {
		case 0:
			this.finish();

			break;
		case 1:
			// 网络获取刷新定位信息
			Toast.makeText(this, "正在刷新定位...", Toast.LENGTH_SHORT).show();
			mLocationClient.start();

			break;
		case 2:
			// radiobutton

			selectType(2);

			break;
		case 3:
			// radiobutton
			selectType(3);
			break;
		case 4:
			// 跳到另外activity选择准确地点，显示并获取坐标
			Log.v("SearchDestinationActivity", "跳转到SearchDestination");
			Intent intent = new Intent(PublishTaskActivity.this,
					SearchDestinationActivity.class);

			startActivity(intent);

			break;
		case 5:
			// 打开时间选择
			createDataDialog().show();

			break;
		case 6:
			// 开启线程提交
			publish();
			break;

		// case 7:
		// //聚焦
		// Toast.makeText(getApplicationContext(), "本应用不要大额交易哦~",
		// Toast.LENGTH_SHORT).show();
		// break;

		default:
			break;
		}

	}

	private void publish() {
		User user = FloatApplication.getApp().getUser(
				getString(R.string.userFileName));
		task.setUserId(user.getId());
		String phoneNo = user.getPhoneNo();
		if (phoneNo == null || phoneNo.trim().equals("")) {
			// 弹框提示修改信息
			Intent intent = new Intent(this, CompleteContactActivity.class);
			startActivityForResult(intent, 100);
			return;

		}
		String describe = etDescribe.getText().toString();
		if (describe.trim().length() < 10) {
			Toast.makeText(this, "任务描述不少于10个字", Toast.LENGTH_SHORT).show();
			return;
		}
		task.setContent(describe);
		task.setDetails(etDetail.getText().toString());

		try {
			task.setTipAward(Integer.parseInt(etMoney.getText().toString()));
		} catch (Exception e) {
			// TODO: handle exception
		}

		task.setSpiritAward(etSpirit.getText().toString() + "");
		// 判断两个地点已定位
		String location = tvLocation.getText().toString();
		String destination = tvDestination.getText().toString();
		if (location.equals("")) {
			// 提示连接网络刷新定位
			Toast.makeText(this, "请刷新网络获取定位", Toast.LENGTH_LONG).show();
			return;
		} else {
			task.setLocation(FloatApplication.getApp().getlocalBDlocation());
			task.setLocation_x(FloatApplication.getApp().getLatitude());
			task.setLocation_y(FloatApplication.getApp().getLongitude());
		}
		if (destination.equals("")) {
			// 提示选择求助目标人群
			Toast.makeText(this, "请选择您想求助的目标人群", Toast.LENGTH_LONG).show();
			return;
		} else {
			task.setDestination(PublishDestinatePlace.getPlace().getName());
			task.setLocation_x1(PublishDestinatePlace.getPlace().getLatitude());
			task.setLocation_y1(PublishDestinatePlace.getPlace().getLongitude());

		}
		waitDialog = DialogUtil.createLoadingDialog(this,
				getString(R.string.waitingDialog));
		waitDialog.show();
		Thread thread = new Thread(new PublishThread());
		thread.start();

	}

	private void selectType(int i) {
		// 换图、editText显示并获取焦点，清空另一个edittext并不显示
		switch (i) {
		case 2:
			radioMoney.setImageResource(R.drawable.radiobutton);
			radioSpirit.setImageResource(R.drawable.radio_buttonds);
			etMoney.setVisibility(View.VISIBLE);
			etMoney.requestFocus();
			etSpirit.setText("");
			etSpirit.setVisibility(View.GONE);
			task.setAwardStatus(0);

			break;
		case 3:
			radioMoney.setImageResource(R.drawable.radio_buttonds);
			radioSpirit.setImageResource(R.drawable.radiobutton);
			etSpirit.setVisibility(View.VISIBLE);
			etSpirit.requestFocus();
			etMoney.setText("");
			etMoney.setVisibility(View.GONE);
			task.setAwardStatus(1);

			break;

		default:
			break;
		}

	}

	// 获取定位
	private void setLocation() {
		// 从本地文件中获取定位，如果定位不到则从网络重新获取
		Log.v(TAG, "setLocation()");
		String addr = FloatApplication.getApp().getlocalBDlocation();

		tvLocation.setText(addr);

	}

	// time--------------------------------------------------------------------------------

	public void setTime() {
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		curDate.setDate(curDate.getDate() + 3);

		String str1 = formatter1.format(curDate);
		String str2 = formatter2.format(curDate);
		Log.v(TAG, str1);

		String nowDate = str1.split(" ")[0];
		String weekday = getWeekOfDate(curDate);

		// texeview_deadline.setText(nowDate);
		tvExpire.setText(nowDate + " " + weekday);
		task.setLimitTime(str2);
	}

	public static String getWeekOfDate(Date dt) {
		System.out.println(dt.toString());
		System.out.println("date:" + dt.getDate() + "day:" + dt.getDay()
				+ "year:" + dt.getYear());

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	private Dialog createDataDialog() {
		// 用来获取日期和时间的
		Calendar calendar = Calendar.getInstance();

		Dialog dialog = null;

		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month,
					int dayOfMonth) {

				// Calendar月份是从0开始,所以month要加1
				String mmmm = (month + 1) + "", dddd = dayOfMonth + "";
				if (month < 10)
					mmmm = "0" + mmmm;
				if (dayOfMonth < 10)
					dddd = "0" + dddd;
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);

				calendar.set(Calendar.MONTH, month);

				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				int weekno = calendar.get(Calendar.DAY_OF_WEEK);
				System.out.println("calendar:" + calendar.toString());
				tvExpire.setText(year + "年" + mmmm + "月" + dddd + "日" + " "
						+ weekDays[weekno - 1]);

				// ///////////////////////////////////////////////
				task.setLimitTime(year + "" + mmmm + "" + dddd);

			}
		};
		dialog = new DatePickerDialog(this, dateListener,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));

		return dialog;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (waitDialog != null) {
				waitDialog.dismiss();
			}
			switch (message.what) {
			case 0:
				// 发布失败
				Toast.makeText(getApplicationContext(), "任务发布失败，请检查您的任务！",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				// 发布成功
				Toast.makeText(getApplicationContext(), "任务发布成功，帮您的人马上来！",
						Toast.LENGTH_SHORT).show();
				PublishTaskActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onRestart() {
		super.onRestart();
		Place place = PublishDestinatePlace.getPlace();
		tvDestination.setText(place.getName());

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 1) {
			publish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	class PublishThread implements Runnable {

		@Override
		public void run() {
			String jsonData = new NetCore().PublishTask(task);
			Log.v(TAG, "jsonData:" + jsonData);
			Message message = new Message();
			message.what = 404;
			try {
				JSONObject jsonObject = new JSONObject(jsonData);
				message.what = jsonObject.getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.sendMessage(message);

		}

	}

}
