package cn.edu.sdu.online.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.activity.DetailedTaskActivity;
import cn.edu.sdu.online.activity.MyTaskDetailActivity;
import cn.edu.sdu.online.activity.PersonInfoActivity;
import cn.edu.sdu.online.adapter.ContactTaskAdapter;
import cn.edu.sdu.online.adapter.MylistviewAdapter;
import cn.edu.sdu.online.adapter.ReleaseTaskAdapter;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.util.HeadPhotoUtil;
import cn.edu.sdu.online.util.ParseJson;
import cn.edu.sdu.online.util.StaticValues;
import cn.edu.sdu.online.view.CustomDialog;
import cn.edu.sdu.online.view.OnRefreshListener;
import cn.edu.sdu.online.view.RefreshListView;

public class PersonFragment extends Fragment implements OnClickListener {
	int refreshTag = 0;// 按钮的标志
	LayoutInflater inflater;
	RefreshListView myListView;// 刷新列表
	ImageView iv_head, iv_sex;
	Button bt_release;
	Button bt_contact;
	TextView nickname, tv_tel, my_email;
	RelativeLayout person_info;
	View view;
	private List<Task> taskList = new ArrayList<Task>();// 解析得到的任务列表
	private ReleaseTaskAdapter myrealeaseAdapter;
	private ContactTaskAdapter mycontactAdapter;
	// private Dialog progressDialog;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	String TAG = "RersonFragment";
	private FloatApplication app;
	String userId;
	User user;

	@Override
	public void onPause() {

		super.onPause();
		if (refreshTag == 0 || refreshTag == R.id.bt_release)
			if (myrealeaseAdapter != null)
				myrealeaseAdapter.notifyDataSetChanged();
			else if (refreshTag == R.id.bt_contact)
				if (mycontactAdapter != null)
					mycontactAdapter.notifyDataSetChanged();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (refreshTag == 0 || refreshTag == R.id.bt_release)
			if (myrealeaseAdapter != null)
				myrealeaseAdapter.notifyDataSetChanged();
			else if (refreshTag == R.id.bt_contact)
				if (mycontactAdapter != null)
					mycontactAdapter.notifyDataSetChanged();
		setInfo();
		super.onResume();

	}

	private void setInfo() {
		app = FloatApplication.getApp();
		user = app.getUser(getString(R.string.userFileName));
		userId = user.getId();
		int sex = user.getSex();
		if (sex == 0) {
			iv_sex.setImageResource(R.drawable.female);
		} else if (sex == 1) {
			iv_sex.setImageResource(R.drawable.male);// 还有保密de！！！！
		} else if (sex == 2)
			iv_sex.setImageResource(R.drawable.secret);
		if (user.getNickName() == null)
			nickname.setText("  ");
		else
			nickname.setText(user.getNickName());
		if (user.getPhoneNo() == null)
			tv_tel.setText("      ");
		else
			tv_tel.setText(user.getPhoneNo());

		my_email.setText(user.getEmail());
		HeadPhotoUtil.setMyHeadPhoto(userId, iv_head, getActivity());

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.person_fragment, null);
		person_info = (RelativeLayout) view.findViewById(R.id.person_info);
		iv_head = (ImageView) view.findViewById(R.id.iv_head);
		iv_sex = (ImageView) view.findViewById(R.id.iv_sex);

		bt_release = (Button) view.findViewById(R.id.bt_release);
		bt_contact = (Button) view.findViewById(R.id.bt_contact);
		nickname = (TextView) view.findViewById(R.id.mynickname);

		tv_tel = (TextView) view.findViewById(R.id.my_tel);

		my_email = (TextView) view.findViewById(R.id.my_email);
		setInfo();
		bt_release.setOnClickListener(this);
		bt_contact.setOnClickListener(this);
		person_info.setOnClickListener(this);
		List<Task> tempList = getListFromCache();
		if (tempList == null || tempList.size() == 0) {
			Log.v(TAG, "缓存为空，远程获取数据");
			getTaskByCon(R.id.bt_release);
		} else {
			Log.v(TAG, "从缓存中获取列表,listSize:" + tempList.size());
			taskList = tempList;
		}

		initSquareLayout();
		return view;
	}

	private List<Task> getListFromCache() {
		List<Task> cacheList;
		cacheList = FloatApplication.getApp().getStoreTaskList(
				StaticValues.STORE_RELEASETASK);
		return cacheList;
	}

	private void initSquareLayout() {
		myListView = (RefreshListView) view.findViewById(R.id.person_listview);
		myListView.setDivider(null);
		initListView();

	}

	private void initListView() {
		myrealeaseAdapter = new ReleaseTaskAdapter(getActivity(), taskList);
		mycontactAdapter = new ContactTaskAdapter(getActivity(), taskList);
		myListView.setAdapter(myrealeaseAdapter);
		myListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 异步查询数据
				if (refreshTag == 0)
					getTaskByCon(R.id.bt_release);
				else
					getTaskByCon(refreshTag);

			}

			@Override
			public void onLoadMoring() {
				if (refreshTag == 0)
					onLoadByCon(R.id.bt_release);
				else
					onLoadByCon(refreshTag);

			}
		});

		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.v(TAG, "arg2" + arg2 + "arg3" + arg3);

				Bundle bundle = new Bundle();
				bundle.putSerializable("task", taskList.get(arg2 - 1));
				Intent intent = null;
				if (refreshTag == 0 || refreshTag == R.id.bt_release)
					intent = new Intent(getActivity(),
							MyTaskDetailActivity.class);
				else if (refreshTag == R.id.bt_contact)
					intent = new Intent(getActivity(),
							DetailedTaskActivity.class);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);

			}
		});
	}

	private String getLocation() {
		SharedPreferences share = getActivity().getSharedPreferences(
				SHARE_LOGIN_TAG, Context.MODE_PRIVATE);
		String location = share.getString("location", "");
		return location;
	}

	private void onLoadByCon(final int id) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// SystemClock.sleep(1000);
				// // 做刷新的地方
				// // 请求新数据并加入Tasklist

				// String jsonResult = null;
				// if (id == R.id.bt_release) {
				// jsonResult = new NetCore()
				// .GetReleaseDealList(FloatApplication.getApp()
				// .getUser(getString(R.string.userFileName))
				// .getId());
				// } else if (id == R.id.bt_contact)
				// jsonResult = new NetCore()
				// .GetRecieveDealList(FloatApplication.getApp()
				// .getUser(getString(R.string.userFileName))
				// .getId());
				// if (jsonResult == null || jsonResult.length() == 0)
				// Log.v(TAG, "bu不能获取到数据");
				// Log.v(TAG, jsonResult);
				// List<Task> taskListre = new ParseJson()
				// .getTaskListFromJson(jsonResult);
				// for (int i = 0; i < taskListre.size(); i++) {
				// taskList.add(taskListre.get(i));
				// }
				// // / if (id == R.id.bt_new)// 只把最新的按钮的刷新存入缓存？？
				// FloatApplication.getApp().setStoreRelTask(
				// StaticValues.STORE_RELEASETASK, taskList);
				return null;
			}

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (myrealeaseAdapter != null) {
					myrealeaseAdapter.notifyDataSetChanged();
				}
				if (mycontactAdapter != null) {
					mycontactAdapter.notifyDataSetChanged();
				}
				// 隐藏头布局

				myListView.onRefreshFinish();
			}
		}.execute(new Void[] {});

	}

	private int getTime() {
		Time time = new Time("GMT+8");

		time.setToNow();
		int year = time.year;
		int month = time.month;
		int day = time.monthDay;
		return year * 10000 + month * 100 + day;
	}

	private void getTaskByCon(final int id) {

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(1000);
				// 做刷新的地方
				// 请求新数据并加入Tasklist

				String jsonResult = null;
				if (id == R.id.bt_release) {
					jsonResult = new NetCore()
							.GetReleaseDealList(FloatApplication.getApp()
									.getUser(getString(R.string.userFileName))
									.getId());
					Log.v(TAG, jsonResult);
					taskList = new ParseJson().getTaskListFromJson(jsonResult);
				} else if (id == R.id.bt_contact) {
					taskList = FloatApplication.getApp().getStoreTaskList(
							StaticValues.STORE_CONTACTTASK + user.getId());
					// Log.v("", taskList.toString());
					if (taskList != null)
					// 便利tasklist把过期的删除和设置已完成的删除；
					{
						Log.v("", "contact");
						int nowTime = getTime();
						Iterator<Task> iter = taskList.iterator();

						while (iter.hasNext()) {
							// Log.v(TAG,
							// String.valueOf(iter.next().getState()));
							Task task = iter.next();
							if (Integer.parseInt(task.getLimitTime()) < nowTime
									|| task.getState() == 2)
								iter.remove();
						}
						// System.out.println(iter.next());
					}

				}

				if (id == R.id.bt_release)
					FloatApplication.getApp().setStoreTaskList(
							StaticValues.STORE_RELEASETASK, taskList);
				return null;
			}

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (id == R.id.bt_release) {
					myrealeaseAdapter.setTaskListFromNet(taskList);
					myListView.setAdapter(myrealeaseAdapter);

				} else if (id == R.id.bt_contact) {
					mycontactAdapter.setListDate(taskList);
					myListView.setAdapter(mycontactAdapter);

				}
				if (myrealeaseAdapter != null) {
					myrealeaseAdapter.notifyDataSetChanged();
				}
				if (mycontactAdapter != null) {
					mycontactAdapter.notifyDataSetChanged();
				}
				// 隐藏头布局
				// if (progressDialog != null) {
				// Log.v(TAG, "对话框快消失！");
				// progressDialog.dismiss();
				// }
				myListView.onRefreshFinish();
			}
		}.execute(new Void[] {});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.bt_release:
			// progressDialog = DialogUtil.createLoadingDialog(getActivity(),
			// "查询中...");
			// progressDialog.show();
			refreshTag = R.id.bt_release;
			bt_release.setTextColor(Color.parseColor("#ffffff"));
			bt_contact.setTextColor(Color.parseColor("#4c4c4c"));
			bt_release.setBackgroundColor(Color.parseColor("#46d6af"));
			bt_contact.setBackgroundColor(Color.parseColor("#ffffff"));
			// getTaskByCon(R.id.bt_release);
			myListView.onfresh();
			break;
		case R.id.bt_contact:
			// progressDialog = DialogUtil.createLoadingDialog(getActivity(),
			// "查询中...");
			// progressDialog.show();
			refreshTag = R.id.bt_contact;
			bt_release.setTextColor(Color.parseColor("#4c4c4c"));
			bt_contact.setTextColor(Color.parseColor("#ffffff"));
			bt_release.setBackgroundColor(Color.parseColor("#ffffff"));
			bt_contact.setBackgroundColor(Color.parseColor("#46d6af"));
			// getTaskByCon(R.id.bt_contact);
			myListView.onfresh();
			break;
		case R.id.person_info:
			// 进入修改个人信息界面
			Intent start_info_activy = new Intent(getActivity(),
					PersonInfoActivity.class);
			startActivityForResult(start_info_activy, 100);
			break;
		default:
			break;
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == 1)// 退登
		{
			this.getActivity().finish();
		}
		if (requestCode == 2)// 修改了信息，刷新当前界面信息
		{

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}