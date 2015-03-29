package cn.edu.sdu.online.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.activity.DetailedTaskActivity;
import cn.edu.sdu.online.adapter.MylistviewAdapter;
import cn.edu.sdu.online.entity.Place;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.util.ParseJson;
import cn.edu.sdu.online.util.StaticValues;
import cn.edu.sdu.online.view.OnRefreshListener;
import cn.edu.sdu.online.view.RefreshListView;

public class SquareFragment extends Fragment implements OnClickListener {

	@Override
	public void onPause() {
		// taskList.clear();
		if (mylistviewAdapter != null) {
			mylistviewAdapter.notifyDataSetChanged();
		}

		super.onPause();

	}

	@Override
	public void onResume() {
		Log.v(TAG, "squareFragmentonResume");
		if (mylistviewAdapter != null) {
			mylistviewAdapter.notifyDataSetChanged();
		}
		super.onResume();

	}

	int refreshTag = 0;// 按钮的标志
	LayoutInflater inflater;
	RefreshListView refreshListView;// 刷新列表
	private List<Task> taskList = new ArrayList<Task>();// 解析得到的任务列表
	private MylistviewAdapter mylistviewAdapter;
	private String TAG = "squareFragement";
	// private Dialog progressDialog;
	private View view;
	private Button bt_new, bt_money, bt_urge, bt_all;
	private ImageView slideBar1, slideBar2, slideBar3, slideBar4;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private final static int GET_TASK_LIST = 0;
	// 动画
	private ImageView cursor;// 动画图片
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度
	private int currIndex = 0;// 当前按钮编号

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.square_fragment, container, false);
		bt_new = (Button) view.findViewById(R.id.bt_new);
		bt_money = (Button) view.findViewById(R.id.bt_money);
		bt_urge = (Button) view.findViewById(R.id.bt_urge);
		bt_all = (Button) view.findViewById(R.id.bt_all);

		InitImageView();
		bt_money.setOnClickListener(this);
		bt_urge.setOnClickListener(this);
		bt_all.setOnClickListener(this);
		bt_new.setOnClickListener(this);
		List<Task> tempList = getListFromCache();
		if (tempList == null || tempList.size() == 0) {
			Log.v(TAG, "缓存为空，远程获取数据");
			// new Thread(new SearchThread()).start();
			getTaskByCon(R.id.bt_new);
		} else {
			Log.v(TAG, "从缓存中获取列表,listSize:" + tempList.size());
			taskList = tempList;
		}
		initSquareLayout();
		return view;

	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) view.findViewById(R.id.slide_bar);

		bmpW = BitmapFactory
				.decodeResource(getResources(), R.drawable.slidebar).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	private List<Task> getListFromCache() {
		List<Task> cacheList;
		cacheList = FloatApplication.getApp().getStoreTaskList(
				StaticValues.STORE_SQUARETASKLIST);
		return cacheList;
	}

	private void initSquareLayout() {
		refreshListView = (RefreshListView) view
				.findViewById(R.id.square_listview);
		refreshListView.setDivider(null);

		initListView();

	}

	private void initListView() {
		mylistviewAdapter = new MylistviewAdapter(getActivity(), taskList);
		refreshListView.setAdapter(mylistviewAdapter);
		refreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 怎么可以每次刷新都是请求最新的任务！
				if (refreshTag != 0)
					getTaskByCon(refreshTag);
				else
					getTaskByCon(R.id.bt_all);

			}

			@Override
			public void onLoadMoring() {
				if (refreshTag != 0)
					onLoadByCon(refreshTag);
				else
					onLoadByCon(R.id.bt_all);

			}
		});
		refreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.v(TAG, "arg2" + arg2 + "arg3" + arg3);

				Bundle bundle = new Bundle();
				bundle.putSerializable("task", taskList.get(arg2 - 1));

				Intent intent = new Intent(getActivity(),
						DetailedTaskActivity.class);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);

			}
		});

	}

	// 上拉加载

	private void onLoadByCon(final int id) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(1000);
				// 做刷新的地方
				// 请求新数据并加入Tasklist

				String jsonResult;
				Place place = FloatApplication.getApp().getPlace();
				if (id == R.id.bt_new) {
					jsonResult = new NetCore().GetTask(taskList.size(), place,
							0);
				} else if (id == R.id.bt_money) {
					jsonResult = new NetCore().GetTask(taskList.size(), place,
							3);
				} else if (id == R.id.bt_urge) {
					jsonResult = new NetCore().GetTask(taskList.size(), place,
							2);
				} else {
					jsonResult = new NetCore().GetTask(taskList.size(), place,
							1);
				}
				if (jsonResult == null || jsonResult.length() == 0)
					Log.v(TAG, "bu不能获取到数据");
				Log.v(TAG, jsonResult);
				List<Task> taskListre = new ParseJson()
						.getTaskListFromJson(jsonResult);
				for (int i = 0; i < taskListre.size(); i++) {
					taskList.add(taskListre.get(i));
				}

				// / if (id == R.id.bt_new)// 只把最新的按钮的刷新存入缓存？？
				FloatApplication.getApp().setStoreTaskList(
						StaticValues.STORE_SQUARETASKLIST, taskList);
				return null;
			}

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (mylistviewAdapter != null) {

					mylistviewAdapter.notifyDataSetChanged();
				}
				// 隐藏头布局
				refreshListView.onRefreshFinish();
			}
		}.execute(new Void[] {});

	}

	private void getTaskByCon(final int id) {

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(1000);
				// 做刷新的地方
				// 请求新数据并加入Tasklist
				Place place = FloatApplication.getApp().getPlace();
				String jsonResult;
				if (id == R.id.bt_new) {
					jsonResult = new NetCore().GetTask(0, place, 0);
				} else if (id == R.id.bt_money) {
					jsonResult = new NetCore().GetTask(0, place, 3);// 需要修改，去掉
				} else if (id == R.id.bt_urge) {
					jsonResult = new NetCore().GetTask(0, place, 2);
				} else {
					jsonResult = new NetCore().GetTask(0, place, 1);
				}
				if (jsonResult == null || jsonResult.length() == 0
						|| jsonResult.trim().equals("")) {
					Activity activity = SquareFragment.this.getActivity();
					activity.runOnUiThread(new NetErrorThread());
					return null;
					

				}

				Log.v(TAG, jsonResult);

				taskList = new ParseJson().getTaskListFromJson(jsonResult);
				mylistviewAdapter.setListDate(taskList);
				Log.v(TAG, "taskList.size()" + taskList.size());
				// 每次刷新都存入缓存这样不好吧？？？
				// / if (id == R.id.bt_new)// 只把最新的按钮的刷新存入缓存？？
				FloatApplication.getApp().setStoreTaskList(
						StaticValues.STORE_SQUARETASKLIST, taskList);
				return null;
			}

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (mylistviewAdapter != null) {
					Log.v(TAG, "count:" + mylistviewAdapter.getCount());
					mylistviewAdapter.notifyDataSetChanged();
				}
				
				// if (progressDialog != null) {
				// Log.v(TAG, "对话框快消失！");
				// progressDialog.dismiss();
				// }
				refreshListView.onRefreshFinish();// 隐藏头布局
			}
		}.execute(new Void[] {});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// progressDialog = DialogUtil
		// .createLoadingDialog(getActivity(), "查询中...");
		// progressDialog.show();
		switch (v.getId()) {
		case R.id.bt_new:
			refreshTag = R.id.bt_new;
			startAnimition(R.id.bt_new);

			currIndex = 0;
			bt_new.setTextColor(Color.parseColor("#46d6af"));
			bt_money.setTextColor(Color.parseColor("#4c4c4c"));
			bt_urge.setTextColor(Color.parseColor("#4c4c4c"));
			bt_all.setTextColor(Color.parseColor("#4c4c4c"));
			// getTaskByCon(R.id.bt_new);
			refreshListView.onfresh();// 刷新动作
			break;
		case R.id.bt_money:
			refreshTag = R.id.bt_money;
			startAnimition(R.id.bt_money);
			currIndex = 1;
			bt_new.setTextColor(Color.parseColor("#4c4c4c"));
			bt_money.setTextColor(Color.parseColor("#46d6af"));
			bt_urge.setTextColor(Color.parseColor("#4c4c4c"));
			bt_all.setTextColor(Color.parseColor("#4c4c4c"));
			// getTaskByCon(R.id.bt_money);
			refreshListView.onfresh();

			break;
		case R.id.bt_urge:
			refreshTag = R.id.bt_urge;
			startAnimition(R.id.bt_urge);
			currIndex = 2;
			bt_new.setTextColor(Color.parseColor("#4c4c4c"));
			bt_money.setTextColor(Color.parseColor("#4c4c4c"));
			bt_urge.setTextColor(Color.parseColor("#46d6af"));
			bt_all.setTextColor(Color.parseColor("#4c4c4c"));
			// getTaskByCon(R.id.bt_urge);
			refreshListView.onfresh();

			break;
		case R.id.bt_all:
			refreshTag = R.id.bt_all;
			startAnimition(R.id.bt_all);
			currIndex = 3;
			bt_new.setTextColor(Color.parseColor("#4c4c4c"));
			bt_money.setTextColor(Color.parseColor("#4c4c4c"));
			bt_urge.setTextColor(Color.parseColor("#4c4c4c"));
			bt_all.setTextColor(Color.parseColor("#46d6af"));
			// getTaskByCon(R.id.bt_all);
			refreshListView.onfresh();

			break;
		default:
			break;
		}

	}

	// 动画可以设置一个记录前一个的指针
	private void startAnimition(int viewId) {
		int one = offset * 2 + bmpW;// 0 ->1 偏移量
		int two = one * 2;// 0 -> 2 偏移量
		int three = one * 3;// 0-> 3偏移量
		Animation animation = null;
		switch (viewId) {
		case R.id.bt_new:
			if (currIndex == 1) {
				animation = new TranslateAnimation(one, 0, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, 0, 0, 0);
			} else if (currIndex == 3) {
				animation = new TranslateAnimation(three, 0, 0, 0);
			} else if (currIndex == 0) {
				return;
			}
			break;
		case R.id.bt_money:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			} else if (currIndex == 3) {
				animation = new TranslateAnimation(three, one, 0, 0);
			} else if (currIndex == 1) {
				return;
			}
			break;
		case R.id.bt_urge:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			} else if (currIndex == 3) {
				animation = new TranslateAnimation(three, two, 0, 0);
			} else if (currIndex == 2) {
				return;
			}
			break;
		case R.id.bt_all:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, three, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, three, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, three, 0, 0);
			} else if (currIndex == 3) {
				return;
			}
			break;
		}

		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		cursor.startAnimation(animation);

	}

	class NetErrorThread implements Runnable {

		@Override
		public void run() {
			Toast.makeText(
					getActivity(),
					SquareFragment.this.getActivity().getString(
							R.string.netError), Toast.LENGTH_LONG).show();

		}

	}

}
