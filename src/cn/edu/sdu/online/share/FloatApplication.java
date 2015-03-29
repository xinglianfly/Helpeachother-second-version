package cn.edu.sdu.online.share;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.container.FragmentTabsPager;
import cn.edu.sdu.online.entity.Place;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.util.InitBdLocation;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;

public class FloatApplication extends Application {

	private static FloatApplication app;
	private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
	//持有首页的实例（FragmentTabsPager）
	private FragmentTabsPager ftPagerActivity;
	// 定位
	private InitBdLocation initBdLocation;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";// 国测局标准
	// 登录状态
	public static final int ALREADY_LOGIN = 1;
	public static final int NEVER_LOGIN = 0;
	//
	public static final int ALREADY_LOCATION = 1;
	public static final int NEVER_LOCATION = 0;
	public static final String APP_DIR = Environment
			.getExternalStorageDirectory().getPath() + "/HelpEachOther";
	public static final String HEAD_PHOTOS = APP_DIR + "/" + "HeadPhotos";

	public WindowManager.LayoutParams getWindowParams() {
		return windowParams;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app = this;
		makePhotoDir();
		SDKInitializer.initialize(this);
		initBdLocation = new InitBdLocation(getApplicationContext());
		setLocation();
		
		// 如果之前已登录则自动登录

	}

	private void setLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;

		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 得到具体地址
		initBdLocation.getmLocationClient().setLocOption(option);
	}

	private void makePhotoDir() {
		File file1 = new File(APP_DIR);
		if (!file1.exists()) {
			file1.mkdir();
		}

		File file2 = new File(HEAD_PHOTOS);
		if (!file2.exists()) {
			file2.mkdir();
		}

	}

	/*
	 * 任务列表
	 */
	public void setStoreTaskList(String filename, List<Task> tasklist) {
		try {
			FileOutputStream out = openFileOutput(filename, MODE_PRIVATE);
			ObjectOutputStream oj = new ObjectOutputStream(out);
			oj.writeObject(tasklist);
			oj.close();
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static FloatApplication getApp() {
		return app;
	}

	/*
	 * 存储用户信息
	 */

	public void setStoreUser(String filename, User user) {
		try {

			FileOutputStream out = openFileOutput(filename, MODE_PRIVATE);
			ObjectOutputStream oj = new ObjectOutputStream(out);
			oj.writeObject(user);
			oj.close();
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.v("erroir1", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.v("erroir2", e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 存储位置信息
	 * 
	 * 
	 * @param location
	 */
	public void setStoreLocation(BDLocation location) {
		SharedPreferences preferences = getSharedPreferences(
				getString(R.string.location_tag), 0);
		preferences.edit()
				.putString(getString(R.string.addr_tag), location.getAddrStr())
				.commit();
		preferences.edit()
				.putString(getString(R.string.city_tag), location.getCity())
				.commit();
		preferences
				.edit()
				.putString(getString(R.string.longitude_tag),
						location.getLongitude() + "").commit();
		preferences
				.edit()
				.putString(getString(R.string.latitude_tag),
						location.getLatitude() + "").commit();

		preferences = null;

	}

	/**
	 * 得到位置信息
	 * 
	 * @param bm
	 * @param path
	 */
	public Place getPlace() {

		SharedPreferences preferences = getSharedPreferences(
				getApplicationContext().getString(R.string.location_tag), 0);
		String addr = preferences.getString(getString(R.string.addr_tag), "");
		String city = preferences.getString(getString(R.string.city_tag), "");
		double longitude = Double.parseDouble(preferences.getString(
				getString(R.string.longitude_tag),
				getString(R.string.def_longitude)));
		double latitude = Double.parseDouble(preferences.getString(
				getString(R.string.latitude_tag),
				getString(R.string.def_latitude)));

		preferences = null;
		Place place = new Place();
		place.setLatitude(latitude);
		place.setLongitude(longitude);
		place.setCity(city);
		place.setAddr(addr);

		return place;
	}

	public String getlocalBDlocation() {

		Log.v("getlocalBDlocation", "getlocalBDlocation");
		SharedPreferences preferences = getSharedPreferences(
				getApplicationContext().getString(R.string.location_tag), 0);
		String addr = preferences.getString(getString(R.string.addr_tag), "");

		preferences = null;
		return addr;
	}

	// 城市
	public String getLocalCity() {

		SharedPreferences preferences = getSharedPreferences(
				getApplicationContext().getString(R.string.location_tag), 0);
		String city = preferences.getString(getString(R.string.city_tag), "");

		preferences = null;
		return city;

	}

	// 经度
	public double getLongitude() {

		SharedPreferences preferences = getSharedPreferences(
				getApplicationContext().getString(R.string.location_tag), 0);
		double longitude = Double.parseDouble(preferences.getString(
				getString(R.string.longitude_tag),
				getString(R.string.def_longitude)));

		preferences = null;
		return longitude;

	}

	// 纬度
	public double getLatitude() {

		SharedPreferences preferences = getSharedPreferences(
				getApplicationContext().getString(R.string.location_tag), 0);
		double latitude = Double.parseDouble(preferences.getString(
				getString(R.string.latitude_tag),
				getString(R.string.def_latitude)));

		preferences = null;
		return latitude;

	}

	/*
	 * 存储用户头像
	 */
	public void JPsetStoreheadphto(Bitmap bm, String path) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			FileOutputStream out = new FileOutputStream(new File(path));
			out.write(baos.toByteArray());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 得到广场任务列表
	 */
	public List<Task> getStoreTaskList(String filename) {
		List<Task> TaskList = null;
		try {
			FileInputStream in = openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(in);
			TaskList = (List<Task>) is.readObject();

			is.close();
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TaskList;

	}

	/*
	 * 得到用户信息
	 */
	public User getUser(String filename) {
		User user = null;
		try {
			FileInputStream in = openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(in);
			user = (User) is.readObject();
			is.close();
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	/*
	 * 得到用户头像
	 */
	public Bitmap getHeadphoto(String path) {
		Bitmap bit = null;

		try {
			FileInputStream fis = new FileInputStream(path);
			bit = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bit;
	}

	public InitBdLocation getInitBdLocation() {
		return initBdLocation;
	}

	public FragmentTabsPager getFtPagerActivity() {
		return ftPagerActivity;
	}

	public void setFtPagerActivity(FragmentTabsPager ftPagerActivity) {
		this.ftPagerActivity = ftPagerActivity;
	}

}