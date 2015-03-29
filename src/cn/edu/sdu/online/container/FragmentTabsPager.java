/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.sdu.online.container;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Choreographer.FrameCallback;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.activity.LoginActivity;
import cn.edu.sdu.online.activity.PublishTaskActivity;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.fragment.PersonFragment;
import cn.edu.sdu.online.fragment.SquareFragment;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.service.LoginService;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.view.ContactPopWindow;
import cn.edu.sdu.online.view.SetPopWindow;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI that
 * switches between tabs and also allows the user to perform horizontal flicks
 * to move between the tabs.
 */
public class FragmentTabsPager extends FragmentActivity implements
		OnClickListener {
	String TAG = "FragmentTabsPager";
	String userId;
	Dialog dialog;
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	View person, square;
	// static TextView view1;
	// static ActionBar actionbar;
	static TextView titlea;
	View viewTitleBar;
	ImageView iv_location, iv_publish;

	private WindowManager windowManager = null;
	private WindowManager.LayoutParams windowManagerParams = null;

	// location

	private LocationClient mLocationClient;
	private TextView LocationResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setTheme(R.style.MainTheme);
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(FragmentTabsPager.this);
		setContentView(R.layout.fragment_tabs_pager);
		//实例化FloatApplication的本类对象
		FloatApplication.getApp().setFtPagerActivity(this);
		// client
		initUser();
		//开启登录刷新jsession service
		Intent intent = new Intent(this,LoginService.class);
		startService(intent);

		iv_location = (ImageView) findViewById(R.id.iv_location);

		iv_publish = (ImageView) findViewById(R.id.iv_publish);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);// 布局
        
		mTabHost.setup();
		iv_location.setOnClickListener(this);
		iv_publish.setOnClickListener(this);

		// 布局

		square = inflater.inflate(R.layout.tab_square, null);
		person = inflater.inflate(R.layout.tab_personal, null);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		titlea = (TextView) findViewById(R.id.title);// 题目

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);// 解析器

		mTabsAdapter.addTab(mTabHost.newTabSpec("square")// 添加f
				.setIndicator(square), SquareFragment.class, null);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				createView();
			}
		}, 200);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		// 定位
		initLocation();
		String addr = FloatApplication.getApp().getlocalBDlocation();

		if (addr.equals("")) {
			startLocation();
		}
		LocationResult.setText(addr);

	}

	private void initUser() {
		dialog = DialogUtil.createLoadingDialog(this,
				getString(R.string.waitingDialog));
		dialog.show();
		Thread thread = new Thread(new initUserThread());
		thread.start();

	}

	private void initLocation() {
		// TODO Auto-generated method stub
		mLocationClient = FloatApplication.getApp().getInitBdLocation()
				.getmLocationClient();

		LocationResult = (TextView) findViewById(R.id.tv_location);
		LocationResult.requestFocus();
		FloatApplication.getApp().getInitBdLocation().tvLocation = LocationResult;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.iv_search:
		// Intent intent = new Intent(this, SearchActivity.class);
		// startActivity(intent);
		// break;
		case R.id.iv_location:
			// 刷新定位

			SetPopWindow contactpop = new SetPopWindow(this, mLocationClient);
			contactpop.showPopupWindow(iv_location);
			break;
		case R.id.iv_publish:
			Intent inten = new Intent(this, PublishTaskActivity.class);
			startActivity(inten);
			break;
		default:
			break;
		}

	}

	private void startLocation() {

		mLocationClient.start();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}

	// 悬浮按钮
	private void createView() {

		windowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		windowManagerParams = ((FloatApplication) getApplication())
				.getWindowParams();
		windowManagerParams.type = LayoutParams.TYPE_PHONE;
		windowManagerParams.format = PixelFormat.RGBA_8888;
		windowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
		windowManagerParams.x = 800;
		windowManagerParams.y = 900;
		windowManagerParams.width = LayoutParams.WRAP_CONTENT;
		windowManagerParams.height = LayoutParams.WRAP_CONTENT;

	}

	class imageviewListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(FragmentTabsPager.this, PublishTaskActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements
			TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;

		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		String[] title = { "广场", "我的" };

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		// 再说
		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost,
				ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);

		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
			titlea.setText(title[position]);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
			titlea.setText(title[position]);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// System.out.println(state+"{{{{{{{{");
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

	class initUserThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message message = new Message();
			message.what = startGetUser();
			loginHandler.sendMessage(message);
		}

	}

	class GetHeadThread implements Runnable {

		@Override
		public void run() {
			Bitmap bitmap = new NetCore().DownloadPicture(userId);
			if (bitmap != null) {
				FloatApplication.getApp().JPsetStoreheadphto(bitmap,
						FloatApplication.APP_DIR + "/" + userId + ".jpg");
			}

		}

	}

	Handler loginHandler = new Handler() {
		public void handleMessage(Message message) {
			Log.v(TAG, "message.what" + message.what);
			switch (message.what) {

			case 0:

				if (dialog != null) {
					dialog.dismiss();
				}
				// 加载两个fragment
				mTabsAdapter.addTab(
						mTabHost.newTabSpec("person").setIndicator(person),
						PersonFragment.class, null);
				// 开启线程获得用户头像
				Thread headThread = new Thread(new GetHeadThread());
				headThread.start();

				break;
			case 404:
				if (dialog != null) {
					dialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "网络状况不佳。。。",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(FragmentTabsPager.this,
						LoginActivity.class);
				FragmentTabsPager.this.startActivity(intent);
				FragmentTabsPager.this.finish();

			default:
				break;
			}

		}
	};

	private int startGetUser() {
		int state = 404;
		SharedPreferences share = getSharedPreferences(
				getString(R.string.login_tag), 0);

		String email = share.getString(getString(R.string.login_email), "");
		String jsonResult = new NetCore().GetUserInfo(email);
		if (!jsonResult.equals("")) {
			state = 0;

		}
		if (jsonResult.equals("")) {

			return state;

		}

		User user = parseJson(jsonResult);
		userId = user.getId();
		Log.v(TAG, user.getId());
		FloatApplication.getApp().setStoreUser(
				getString(R.string.userFileName), user);

		Log.v(TAG, user.getEmail());
		return state;

	}

	private User parseJson(String jsonReslut) {
		// TODO Auto-generated method stub
		User user = new User();
		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject(jsonReslut);

			user.setId(getStringFromJson(jsonObject, "id"));

			user.setEmail(getStringFromJson(jsonObject, "email"));

			user.setNickName(getStringFromJson(jsonObject, "nickName"));

			user.setSex(getIntFromJson(jsonObject, "sex"));
			Log.v(TAG, "user.sex" + user.getSex());

			user.setPhoneNo(getStringFromJson(jsonObject, "phoneNo"));

			user.setRegisterTime(getStringFromJson(jsonObject, "registerTime"));

			user.setLevel(getIntFromJson(jsonObject, "level"));

			user.setScore(getIntFromJson(jsonObject, "score"));

			user.setLastLogin(getStringFromJson(jsonObject, "lastLogin"));

			user.setLastMsg(getStringFromJson(jsonObject, "lastMsg"));

			user.setHeadPhoto(getStringFromJson(jsonObject, "headPhoto"));

			user.setSchool(getStringFromJson(jsonObject, "school"));

			user.setQq(getStringFromJson(jsonObject, "qq"));

			user.setWeixin(getStringFromJson(jsonObject, "weixin"));

			user.setBirthday(getStringFromJson(jsonObject, "birthday"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return user;
	}

	private String getStringFromJson(JSONObject jsonObject, String string) {
		String str = null;
		try {
			str = jsonObject.getString(string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v(TAG, string + str);
		return str;
	}

	private int getIntFromJson(JSONObject jsonObject, String string) {
		int intValue = 0;
		try {
			intValue = jsonObject.getInt(string);
		} catch (JSONException e) {
			Log.v(TAG, "error:" + string);
			e.printStackTrace();
		}
		return intValue;
	}

}
