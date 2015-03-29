package cn.edu.sdu.online.adapter;

import java.util.Iterator;
import java.util.List;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.AsynImageLoder;
import cn.edu.sdu.online.util.DialogUtil;
import cn.edu.sdu.online.util.StaticValues;
import cn.edu.sdu.online.view.ContactPopWindow;
import cn.edu.sdu.online.view.CustomDialog;

public class ContactTaskAdapter extends BaseAdapter {
	private List<Task> myListDate;
	private static LayoutInflater inflater = null;
	String TAG = "MylistviewAdapter";
	private Context context;
	private Dialog progressDialog;
	private static final int FINISH_SUCCESS = 1;
	private static final int FINISH_FAILED = 0;
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

	public ContactTaskAdapter(Context context, List<Task> listDate) {
		this.myListDate = listDate;
		this.context = context;
		inflater = LayoutInflater.from(context);
		// this.inflater = (LayoutInflater) activity
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (myListDate != null)
			return myListDate.size();// myListDate.size();
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

	// 弹窗
	private void showDialog(String content, final int position) {
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
						"删除中...");
				progressDialog.show();
				Thread thread = new Thread(new setDeleteThread(myListDate.get(
						position).getId()));
				thread.start();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		Log.v(TAG, "getview()");
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.storage_contact_listview,
					null);
			viewHolder = new ViewHolder();

			viewHolder.iv_sex = (ImageView) convertView
					.findViewById(R.id.iv_sex);
			viewHolder.iv_head = (ImageView) convertView
					.findViewById(R.id.iv_head);

			viewHolder.bt_help = (ImageButton) convertView
					.findViewById(R.id.bt_help);
			viewHolder.bt_delete = (ImageButton) convertView
					.findViewById(R.id.bt_delete);

			// 设置
			viewHolder.location = (TextView) convertView
					.findViewById(R.id.location);

			viewHolder.nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);

			viewHolder.leftTime = (TextView) convertView
					.findViewById(R.id.tv_lefttime);

			viewHolder.reward = (TextView) convertView
					.findViewById(R.id.tv_reward);

			viewHolder.formalText = (TextView) convertView
					.findViewById(R.id.formal_text);

			convertView.setTag(viewHolder);
			Log.v(TAG, "viewholder:" + "convertview为null"
					+ viewHolder.formalText.getText());
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			Log.v(TAG, "viewholder:" + "convertview不为null"
					+ viewHolder.formalText.getText());
		}
		AsynImageLoder imageLoder = new AsynImageLoder(context);
		if (viewHolder.iv_head == null) {
			Log.v(TAG, "viewHolder.iv_head==null");
		}
		imageLoder.disPlayImage(viewHolder.iv_head, myListDate.get(position)
				.getUserId());

		int sex = myListDate.get(position).getSex();
		if (sex == 0) {
			viewHolder.iv_sex.setImageResource(R.drawable.female);
		}
		if (sex == 1) {
			viewHolder.iv_sex.setImageResource(R.drawable.male);// 还有保密de！！！！
		}
		if (sex == 2)
			viewHolder.iv_sex.setImageResource(R.drawable.secret);
		viewHolder.bt_help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 弹出联系的简单对话框
				ContactPopWindow contactpop = new ContactPopWindow(context,
						myListDate.get(position));
				contactpop.showPopupWindow(parent);

			}
		});
		viewHolder.bt_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog("你确定要删除么？！", position);
			}
		});
		if (myListDate.get(position).getLocation() != null)
			viewHolder.location.setText(myListDate.get(position).getLocation());
		else
			viewHolder.location.setText("");
		if (myListDate.get(position).getNickName() == null)
			viewHolder.nickname.setText("               ");
		else
			viewHolder.nickname.setText(myListDate.get(position).getNickName());
		String str = myListDate.get(position).getLimitTime();
		if (str != null) {
			String str1 = str.substring(0, 4);
			String str2 = str.substring(4, 6);
			String str3 = str.substring(6, 8);
			viewHolder.leftTime.setText(str1 + "-" + str2 + "-" + str3);
		} else
			viewHolder.leftTime.setText("长期有效");

		switch (myListDate.get(position).getAwardStatus()) {
		case 0:
			try {
				viewHolder.reward.setText(myListDate.get(position)
						.getTipAward() + "元");
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;
		case 1:
			try {
				viewHolder.reward.setText(myListDate.get(position)
						.getSpiritAward() + "");
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;

		default:
			break;
		}
		viewHolder.formalText.setText(myListDate.get(position).getContent());
		return convertView;
	}

	static class ViewHolder {
		TextView leftTime, reward, formalText, nickname, location;
		ImageView iv_head, iv_sex;
		ImageButton bt_help;
		ImageButton bt_delete;
	}

	public List<Task> getListDate() {
		return myListDate;
	}

	public void setListDate(List<Task> listDate) {
		this.myListDate = listDate;
	}

	class setDeleteThread implements Runnable {
		private String taskId;

		public setDeleteThread(String taskId) {
			super();
			this.taskId = taskId;

		}

		@Override
		public void run() {
			Message message = new Message();
			message.what = FINISH_FAILED;
			try {
				Iterator<Task> iter = myListDate.iterator();
				while (iter.hasNext()) {
					if (iter.next().getId() == taskId) {
						iter.remove();
						message.what = FINISH_SUCCESS;
					}
				}
				FloatApplication.getApp().setStoreTaskList(
						StaticValues.STORE_CONTACTTASK, myListDate);
			} catch (Exception e) {
				//
				e.printStackTrace();
			}
			myHandler.sendMessage(message);
		}

	}
}