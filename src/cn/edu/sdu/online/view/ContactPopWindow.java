package cn.edu.sdu.online.view;

import java.util.ArrayList;
import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.StaticValues;

public class ContactPopWindow extends PopupWindow implements OnClickListener {
	private View contentView;
	private TextView qq;
	private TextView tel;
	private TextView wechat;
	private Task task;
	private Context context;

	public ContactPopWindow(final Context context, Task task) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		contentView = inflater.inflate(R.layout.contact_popup_dialog, null);
		this.task = task;
		qq = (TextView) contentView.findViewById(R.id.tv_qq);
		tel = (TextView) contentView.findViewById(R.id.tv_tel);
		wechat = (TextView) contentView.findViewById(R.id.tv_wechat);
		if (task != null) {
			qq.setText(Html.fromHtml("<u>" + task.getQq() + "</u>"));
			tel.setText(task.getPhoneNo());
			wechat.setText(Html.fromHtml("<u>" + task.getWeixin() + "</u>"));
			qq.setOnClickListener(this);
			tel.setOnClickListener(this);
			wechat.setOnClickListener(this);
		}

		// 设置SelectPicPopupWindow的View
		this.setContentView(contentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
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
			this.showAtLocation(parent, Gravity.CENTER, 0, 0);
			// this.showAsDropDown(parent, parent.getLayoutParams().width / 2,
			// 18);
		} else {
			this.dismiss();
		}
	}

	private void getCopy(String text) {
		ClipboardManager clipboardManager = (ClipboardManager) (context
				.getSystemService(Context.CLIPBOARD_SERVICE));
		clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
		if (clipboardManager.hasPrimaryClip()) {
			clipboardManager.getPrimaryClip().getItemAt(0).getText();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.tv_qq) {
			Toast.makeText(context, "QQ号码已经复制", Toast.LENGTH_SHORT).show();
			getCopy(task.getQq());
		} else if (v.getId() == R.id.tv_wechat) {
			Toast.makeText(context, "微信号码已经复制", Toast.LENGTH_SHORT).show();
			getCopy(task.getWeixin());
		}
		Log.v("aaa", "我被点击了");
		if (task.getState() != 2) {
			List<Task> taskList = FloatApplication.getApp().getStoreTaskList(
					StaticValues.STORE_CONTACTTASK);
			if (taskList == null) {
				taskList = new ArrayList<Task>();
			} else {
				if (!taskList.contains(task))
					taskList.add(task);
			}
			FloatApplication.getApp()
					.setStoreTaskList(
							StaticValues.STORE_CONTACTTASK + task.getUserId(),
							taskList);
		}
	}
}
