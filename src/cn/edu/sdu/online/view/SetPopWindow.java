package cn.edu.sdu.online.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.activity.SearchActivity;

import com.baidu.location.LocationClient;

public class SetPopWindow extends PopupWindow implements OnClickListener {
	private View contentView;
	private LinearLayout search, location, set;
	private Activity context;
	private LocationClient mLocationClient;

	public SetPopWindow(final Activity context, LocationClient mLocationClient) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.mLocationClient = mLocationClient;
		contentView = inflater.inflate(R.layout.set_popup_dialog, null);
		search = (LinearLayout) contentView.findViewById(R.id.search);
		location = (LinearLayout) contentView.findViewById(R.id.location);
		set = (LinearLayout) contentView.findViewById(R.id.set);
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		search.setOnClickListener(this);
		location.setOnClickListener(this);
		set.setOnClickListener(this);

		// 设置SelectPicPopupWindow的View
		this.setContentView(contentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w / 4+10);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(996699);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(new BitmapDrawable());
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);

	}

	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
		} else {
			this.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search:
			Intent intent = new Intent(context, SearchActivity.class);
			context.startActivity(intent);
			break;
		case R.id.location:
			mLocationClient.start();
			Toast.makeText(context, "正在刷新定位...", Toast.LENGTH_SHORT).show();
			Log.v("SetPoPWindow", "LocationResult");
			break;
		case R.id.set:

			break;

		default:
			break;
		}

	}
}
