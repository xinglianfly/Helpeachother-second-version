package cn.edu.sdu.online.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.a.b.e;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.util.HeadPhotoUtil;

public class ChangePersonActivity extends Activity implements OnClickListener {
	private Button back;
	private TextView tvEmail, tvBirth;
	private ImageView save, seMale, seFemale, seSecret, changePassWord;
	private EditText etNickName, etSchool, etPhone, etQQ, etWechat;

	User user = new User();
	String TAG = "ChangePersonActivity";

	int sex = 0;// "0" is girl; "1" is boy;"2" is secret

	Dialog dialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changeinfo);
		initialization();
	}

	private void initialization() {
		User preUser = FloatApplication.getApp().getUser(
				getString(R.string.userFileName));

		user_id = preUser.getId();

		Log.v(TAG, "initialization");
		save = (ImageView) findViewById(R.id.im_complete);
		save.setTag(1);
		save.setOnClickListener(this);
		back = (Button) findViewById(R.id.bt_back);
		back.setTag(0);
		back.setOnClickListener(this);

		tvEmail = (TextView) findViewById(R.id.tv_email);
		tvEmail.setText(preUser.getEmail());

		etNickName = (EditText) findViewById(R.id.tv_nick_name);
		etNickName.setText(preUser.getNickName());
		etSchool = (EditText) findViewById(R.id.tv_school);
		etSchool.setText(preUser.getSchool());
		etPhone = (EditText) findViewById(R.id.tv_phone);
		etPhone.setText(preUser.getPhoneNo());
		etQQ = (EditText) findViewById(R.id.tv_QQ);
		etQQ.setText(preUser.getQq());
		etWechat = (EditText) findViewById(R.id.tv_wechat);
		etWechat.setText(preUser.getWeixin());
		tvBirth = (TextView) findViewById(R.id.tv_birth);
		// tvBirth.setText(getBirthFormat(preUser.getBirthday()));
		tvBirth.setTag(6);
		tvBirth.setOnClickListener(this);
		String birthString = preUser.getBirthday();
		if (birthString != null && !birthString.equals("")) {
			tvBirth.setText(getBirthFormat(preUser.getBirthday()));
		}

		seMale = (ImageView) findViewById(R.id.iv_se_male);
		seMale.setTag(2);
		seMale.setOnClickListener(this);
		seFemale = (ImageView) findViewById(R.id.iv_se_female);
		seFemale.setTag(3);
		seFemale.setOnClickListener(this);
		seSecret = (ImageView) findViewById(R.id.iv_se_secret);
		seSecret.setTag(4);
		seSecret.setOnClickListener(this);
		preSetSex(preUser);
		changePassWord = (ImageView) findViewById(R.id.im_changepass);
		changePassWord.setTag(5);
		changePassWord.setOnClickListener(this);

		image_head = (ImageView) findViewById(R.id.change_info_head);
		HeadPhotoUtil.setMyHeadPhoto(preUser.getId(), image_head, this);
		image_head.setOnClickListener(this);
		image_head.setTag(7);

	}

	private String getBirthFormat(String birth) {
		// TODO Auto-generated method stub
		String birthFormat = null;

		String year = birth.substring(0, 4);
		String month = birth.substring(4, 6);
		String day = birth.substring(6, 8);
		birthFormat = year + "年" + month + "月" + day + "日";
		return birthFormat;

	}

	private String getBirthString(String birthString) {
		// TODO Auto-generated method stub
		String birth = null;

		String year = birthString.substring(0, 4);
		String month = birthString.substring(5, 7);
		String day = birthString.substring(8, 10);
		birth = year + month + day;
		return birth;

	}

	private void preSetSex(User preUser) {
		sex = preUser.getSex();
		Log.v(TAG, "preUserSex:" + sex);
		changeSex();

	}

	@Override
	public void onClick(View v) {
		Log.v(TAG, "click ");
		int tag = (Integer) v.getTag();
		// TODO Auto-generated method stub
		switch (tag) {
		case 1:

			Log.v(TAG, "click complete");
			String nickName = etNickName.getText().toString();
			if (nickName.trim().length()==0) {
				Toast.makeText(this, "昵称不能为空!", Toast.LENGTH_LONG).show();
				return;
			}
			user.setNickName(nickName);
			user.setPhoneNo(etPhone.getText().toString());
			user.setSchool(etSchool.getText().toString());
			user.setSex(sex);
			user.setId(FloatApplication.getApp()
					.getUser(getString(R.string.userFileName)).getId());
			user.setEmail(tvEmail.getText().toString());
			user.setQq(etQQ.getText().toString());
			user.setWeixin(etWechat.getText().toString());
			String birthString = tvBirth.getText().toString();
			if (birthString!=null&&!birthString.equals("")) {
				user.setBirthday(getBirthString(birthString));
			}
			

			Log.v(TAG,
					"nickName:" + user.getNickName() + "phoneNo:"
							+ user.getPhoneNo() + "school:" + user.getSchool()
							+ "sex:" + user.getSex() + "id:" + user.getId());
			FloatApplication.getApp().setStoreUser(
					getString(R.string.userFileName), user);

			dialog = DialogUtil.createLoadingDialog(ChangePersonActivity.this,
					"修改中...");
			// 开启线程
			dialog.show();
			Thread thread = new Thread(new ChangeThread());
			thread.start();

			break;
		case 0:
			this.finish();
		case 2:
			sex = 1;
			changeSex();
			break;
		case 3:
			sex = 0;
			changeSex();
			break;
		case 4:
			sex = 2;
			changeSex();
			break;
		case 5:
			Intent intent = new Intent(ChangePersonActivity.this,
					ChangePassW.class);
			startActivity(intent);
			break;
		case 6:
			// 时间选择器选择生日
			setBrithday();
			break;
		case 7:
			// 上传头像
			
			// this.openOptionsMenu();
			final String[] items = { "相册", "相机" };// 条目列表
			new AlertDialog.Builder(ChangePersonActivity.this)// 建立对话框
					.setTitle("请选择方式")// 标题
					.setItems(items, new DialogInterface.OnClickListener() {// 以下为监听

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (which == 0) {
										// 相册
										chooseAlbum();
									}
									if (which == 1) {
										// 相机
										chooseCamera();
									}
								}
							}).show();

			break;

		default:
			break;
		}
	}

	private void setBrithday() {

		Dialog dialog = null;

		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month,
					int dayOfMonth) {

				// Calendar月份是从0开始,所以month要加1
				String mmmm = (month + 1) + "", dddd = dayOfMonth + "";
				if (month < 9)
					mmmm = "0" + mmmm;
				if (dayOfMonth < 10)
					dddd = "0" + dddd;
				tvBirth.setText(year + "年" + mmmm + "月" + dddd + "日");
				// ///////////////////////////////////////////////
				user.setBirthday(year + "" + mmmm + "" + dddd);

			}
		};
		dialog = new DatePickerDialog(this, dateListener, 1990, 0, 1);

		dialog.show();

	}

	private void changeSex() {
		switch (sex) {
		case 0:
			seFemale.setImageResource(R.drawable.female);
			seMale.setImageResource(R.drawable.maleds);
			seSecret.setImageResource(R.drawable.secretds);

			break;
		case 1:
			seFemale.setImageResource(R.drawable.femaleds);
			seMale.setImageResource(R.drawable.male);
			seSecret.setImageResource(R.drawable.secretds);

			break;
		case 2:
			seFemale.setImageResource(R.drawable.femaleds);
			seMale.setImageResource(R.drawable.maleds);
			seSecret.setImageResource(R.drawable.secret);

			break;

		default:
			break;
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

				ChangePersonActivity.this.finish();
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

	private class ChangeThread implements Runnable {

		@Override
		public void run() {
			//
			String jsonData = new NetCore().Change_person_information(user);

			Message message = new Message();

			message.what = NetCore.NET_ERROR;

			if (isChangeHead) {
				String result = new NetCore().UpLoadPicture(photo_path + "/"
						+ user_id + ".jpg", 1, user_id);
				Log.v(TAG, " upload_result" + result);
			}

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

	ImageView image_head;
	String user_id;
	Bitmap new_head;
	String photo_path = FloatApplication.APP_DIR;
	boolean isChangeHead = false;
	// Uri photoUri;

	private final int PHOTO_REQUEST = 1001;
	private final int CAMERA_REQUEST = 1002;
	private final int CAMERA_CUT_REQUEST = 1003;
	private final int CAMERA_OPTION_ITEM = 2;
	private final int PHOTO_OPTION_ITEM = 1;

	// 创建菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		this.populateMenu(menu);
		return true;
	}

	protected void updateStoreUser() {
		FloatApplication.getApp().setStoreUser(
				getString(R.string.userFileName), user);

	}

	// 菜单加入选项
	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, PHOTO_OPTION_ITEM, 1, "相册");
		menu.add(Menu.NONE, CAMERA_OPTION_ITEM, 2, "照相");

	}

	// 建立头像文件夹功能
	public File makeDir() {

		File tempFile = new File(photo_path + "/" + user_id + ".jpg");// Calendar.getInstance().getTimeInMillis()+".jpg");
																		// //
																		// 以时间秒为文件名
		File temp = new File(photo_path);// 自已项目 文件夹
		if (!temp.exists()) {
			temp.mkdir();
		}

		return tempFile;
	}

	public boolean isSDcardAble() {
		String sdStatus = Environment.getExternalStorageState();// sd card 状态
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) // 检测sd是否可用
		{ // 不可用
			Log.v("TestFile", "SD card is not avaiable/writeable right now.");
			return false;
		}
		return true;
	}

	// 菜单选项监听
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PHOTO_OPTION_ITEM:// 选择相册

			chooseAlbum();
			return true;

		case CAMERA_OPTION_ITEM:// 选择相机

			chooseCamera();
			return true;

		}
		return false;

	}

	//
	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// //得到点击的菜单项的ID（此Id是自己设定的）
	// int itemId = item.getItemId();
	//
	// if(ONE_ID == itemId)
	// {
	// System.out.println("您选择了 1 Pixel 菜单");
	// }
	// return super.onMenuItemSelected(featureId, item);
	// }

	// 调用系统相册，选择并调用裁切，并储存路径
	public void chooseAlbum() {
		Intent innerIntent = new Intent(Intent.ACTION_PICK); // 调用相册
		innerIntent.putExtra("crop", "true");// 剪辑功能
		innerIntent.putExtra("aspectX", 1);// 放大和缩小功能
		innerIntent.putExtra("aspectY", 1);
		innerIntent.putExtra("outputX", 120);// 输出图片大小
		innerIntent.putExtra("outputY", 120);
		innerIntent.setType("image/*");// 查看类型
		innerIntent.putExtra("output", Uri.fromFile(makeDir()));// 传入目标文件
		innerIntent.putExtra("outputFormat", "JPEG"); // 输入文件格式

		startActivityForResult(innerIntent, PHOTO_REQUEST); // 设返回
															// 码为PHOTO_REQUEST
															// onActivityResult
															// 中的 requestCode 对应
	}

	// 调用系统相机
	public void chooseCamera() {
		// photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
		// cameraIntent.putExtra("crop", "true");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(makeDir()));// 传入目标文件
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	// 照完后剪切
	public void chooseCamera_cut(Uri photo_uri) {
		Intent cutIntent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
		// cutIntent.setClassName("com.android.camera",
		// "com.android.camera.CropImage");
		// cutIntent.setType("image/*");//查看类型
		cutIntent.setDataAndType(photo_uri, "image/*");
		cutIntent.putExtra("crop", "true");// 进行修剪
		cutIntent.putExtra("aspectX", 1);// 放大和缩小功能
		cutIntent.putExtra("aspectY", 1);
		cutIntent.putExtra("outputX", 120);// 输出图片大小
		cutIntent.putExtra("outputY", 120);
		cutIntent.putExtra("return-data", true);
		// cutIntent.putExtra("scale", true);
		// cutIntent.putExtra("noFaceDetection", true);
		// cutIntent.putExtra("output", Uri.fromFile(makeDir()));//传入目标文件
		// cutIntent.putExtra("outputFormat", "JPEG"); //输入文件格式
		startActivityForResult(cutIntent, CAMERA_CUT_REQUEST);
	}

	// 对于Activity for result 做响应
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 判断是哪个请求界面的返回
		switch (requestCode) {

		case CAMERA_REQUEST:// 照相返回

			switch (resultCode) {
			case Activity.RESULT_OK:// 照相完成点击确定

				if (!isSDcardAble()) // 检测sd是否可用
				{ // 不可用
					Log.v("TestFile",
							"SD card is not avaiable/writeable right now.");
					return;
				}
				// ////////////////////////////////////////////////////////////////
				// 照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。网上流传的其他Demo这里的照片名称都写死了，
				// 则会发生无论拍照多少张，后一张总会把前一张照片覆盖。细心的同学还可以设置这个字符串，比如加上“ＩＭＧ”字样等；然后就会发现
				// sd卡中myimage这个文件夹下，会保存刚刚调用相机拍出来的照片，照片名称不会重复。
				// String str = null;
				// Date date = null;
				// SimpleDateFormat format = new
				// SimpleDateFormat("yyyyMMddHHmmss");// 获取当前时间，进一步转化为字符串
				// date = new Date(resultCode);
				// str = format.format(date);
				// String fileName = "/sdcard/HelpEachOther/" + str + ".jpg";
				// photo_path=fileName;
				// sendBroadcast(fileName);
				// /////////////////////////////////////////////////////////////////////
				// 获得uri
				// Uri
				// photo_uri=Uri.parse(Environment.getExternalStorageDirectory()+"/HelpEachOther/head_photo.jpg");
				// Toast.makeText(getApplicationContext(),
				// photo_uri.toString(),Toast.LENGTH_SHORT).show();
				// 找到文件
				File file = new File(photo_path + "/" + user_id + ".jpg");
				// 开始剪切
				chooseCamera_cut(Uri.fromFile(file));
				break;

			case Activity.RESULT_CANCELED:// 取消
				break;
			}
			break;

		case PHOTO_REQUEST:// 相册返回
			switch (resultCode) {
			case Activity.RESULT_OK: // 正确选择

				new_head = BitmapFactory.decodeFile(photo_path + "/" + user_id
						+ ".jpg");// 赋值给新头像容器
				image_head.setImageBitmap(new_head);// 反应于界面
				isChangeHead = true;// 改变状态

				// Uri chose_photo_uri= data.getData();
				// String []imgs={MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
				// Cursor cursor=this.managedQuery(chose_photo_uri, imgs, null,
				// null, null);
				// int
				// index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// cursor.moveToFirst();
				// photo_path=cursor.getString(index);//得到图片路径
				break;

			case Activity.RESULT_CANCELED:// 取消 选择
				break;
			}
			break;

		case CAMERA_CUT_REQUEST:

			switch (resultCode) {
			case Activity.RESULT_OK:

				if (!isSDcardAble()) // 检测sd是否可用
				{ // 不可用
					Log.v("TestFile",
							"SD card is not avaiable/writeable right now.");
					return;
				}
				// 将切好图片存入sd卡
				outputPhoto(data);

				new_head = BitmapFactory.decodeFile(photo_path + "/" + user_id
						+ ".jpg");// 赋值给新头像容器
				image_head.setImageBitmap(new_head);// 反应于界面
				isChangeHead = true;// 改变状态
				break;
			case Activity.RESULT_CANCELED:
				break;
			}
			break;

		}
	}

	public void outputPhoto(Intent data) {
		Bundle bundle = data.getExtras();
		Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
		// new_head=bitmap;//为当前头像赋值
		FileOutputStream b = null;

		makeDir();
		try {
			b = new FileOutputStream(photo_path + "/" + user_id + ".jpg");
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally // 善后
		{
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 80;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

}