package cn.edu.sdu.online.adapter;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.net.NetCore;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.util.StaticValues;
import cn.edu.sdu.online.view.CustomDialog;

public class ReleaseTaskAdapter extends BaseAdapter {

	LayoutInflater inflater;
	List<Task> taskListFromNet;
	private Context context;
	private static final int FINISH_SUCCESS = 1;
	private static final int FINISH_FAILED = 0;
	private Dialog progressDialog;
	private Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			switch (msg.what) {
			case FINISH_SUCCESS:
				// 刷新数据
				Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();

				notifyDataSetChanged();

				break;
			case FINISH_FAILED:
				Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}

		};

	};

	public ReleaseTaskAdapter(Context context, List<Task> taskListFromNet) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.taskListFromNet = taskListFromNet;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (taskListFromNet != null)
			return taskListFromNet.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.storage_release_listview,
					null);
			viewHolder = new ViewHolder();

			viewHolder.bt_finish = (ImageButton) convertView
					.findViewById(R.id.bt_finish);
			viewHolder.bt_delete = (ImageButton) convertView
					.findViewById(R.id.bt_delete);

			// 设置
			viewHolder.location = (TextView) convertView
					.findViewById(R.id.location);

			viewHolder.leftTime = (TextView) convertView
					.findViewById(R.id.left_time);

			viewHolder.reward = (TextView) convertView
					.findViewById(R.id.reward);
			viewHolder.formalText = (TextView) convertView
					.findViewById(R.id.formal_text);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.bt_finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 弹出联系的简单对话框
				showDialog(position, "你确定要设置完成么？！", 0);

			}
		});
		viewHolder.bt_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 弹出联系的简单对话框
				showDialog(position, "你确定要删除么？！", 1);

			}
		});
		int state = taskListFromNet.get(position).getState();
		if (state == 2) {
			viewHolder.bt_finish
					.setBackgroundResource(R.drawable.button_finish);
			viewHolder.bt_finish.setClickable(false);
		}

		if (taskListFromNet.get(position).getLocation() != null)
			viewHolder.location.setText(taskListFromNet.get(position)
					.getLocation());
		else
			viewHolder.location.setText("");
		String str = taskListFromNet.get(position).getLimitTime();
		if (str != null) {
			String str1 = str.substring(0, 4);
			String str2 = str.substring(4, 6);
			String str3 = str.substring(6, 8);
			viewHolder.leftTime.setText(str1 + str2 + str3);
		} else
			viewHolder.leftTime.setText("长期有效");

		switch (taskListFromNet.get(position).getAwardStatus()) {
		case 0:
			try {
				viewHolder.reward.setText(taskListFromNet.get(position)
						.getTipAward() + "元");
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;
		case 1:
			try {
				viewHolder.reward.setText(taskListFromNet.get(position)
						.getSpiritAward() + "");
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;

		default:
			break;
		}
		viewHolder.formalText.setText(taskListFromNet.get(position)
				.getContent());
		return convertView;

	}

	static class ViewHolder {
		TextView leftTime, reward, formalText, location;
		ImageButton bt_finish, bt_delete;
	}

	public List<Task> getTaskListFromNet() {
		return taskListFromNet;
	}

	public void setTaskListFromNet(List<Task> taskListFromNet) {
		this.taskListFromNet = taskListFromNet;
	}

	// 弹窗
	private void showDialog(final int position, String content, final int tag) {
		final CustomDialog dialog = new CustomDialog(context);
		dialog.setContent(content);
		dialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// dosomething youself
				dialog.dismiss();
			}
		});
		dialog.setOnNegativeListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = DialogUtil.createLoadingDialog(context,
						"设置中...");
				progressDialog.show();
				Log.v("jsondata", "dianji   shanchu ");
				Thread thread = new Thread(new setCDThread(taskListFromNet.get(
						position).getId(), tag));
				thread.start();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	class setCDThread implements Runnable {
		private String taskId;
		private int tag;

		public setCDThread(String taskId, int tag) {
			super();
			this.taskId = taskId;
			this.tag = tag;

		}

		@Override
		public void run() {

			String jsonData = null;
			if (tag == 0)
				jsonData = new NetCore().setFDTask(taskId, 0);
			else
				jsonData = new NetCore().setFDTask(taskId, 1);
			Message message = new Message();
			message.what = 404;
			try {
				JSONObject jsonObject = new JSONObject(jsonData);
				message.what = jsonObject.getInt("result");
				Log.v("jsondata", String.valueOf(message.what));
				if (message.what == FINISH_SUCCESS && tag == 1) {
					Iterator<Task> iter = taskListFromNet.iterator();
					while (iter.hasNext()) {
						if (iter.next().getId() == taskId) {
							iter.remove();
						}
					}
					FloatApplication.getApp().setStoreTaskList(
							StaticValues.STORE_RELEASETASK, taskListFromNet);
				}

			} catch (JSONException e) {
				//
				e.printStackTrace();
			}
			myHandler.sendMessage(message);

		}

	}
}
