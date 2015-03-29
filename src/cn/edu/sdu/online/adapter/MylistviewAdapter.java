package cn.edu.sdu.online.adapter;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.util.AsynImageLoder;
import cn.edu.sdu.online.view.ContactPopWindow;
import cn.edu.sdu.online.view.CustomDialog;

public class MylistviewAdapter extends BaseAdapter {
	private Activity activity;
	private List<Task> listDate;
	private static LayoutInflater inflater = null;
	String TAG = "MylistviewAdapter";
	private Context context;

	public MylistviewAdapter(Context context, List<Task> listDate) {
		this.listDate = listDate;
		this.context = context;
		inflater = LayoutInflater.from(context);
		// this.inflater = (LayoutInflater) activity
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (listDate != null)
			return listDate.size();// listDate.size();
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
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		Log.v(TAG, "getview()");
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.square_frag_listview_item,
					null);
			viewHolder = new ViewHolder();

			viewHolder.iv_sex = (ImageView) convertView
					.findViewById(R.id.iv_sex);
			viewHolder.iv_head = (ImageView) convertView
					.findViewById(R.id.iv_head);

			viewHolder.bt_help = (ImageButton) convertView
					.findViewById(R.id.bt_help);

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
		imageLoder.disPlayImage(viewHolder.iv_head, listDate.get(position)
				.getUserId());

		int sex = listDate.get(position).getSex();
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
						listDate.get(position));
				contactpop.showPopupWindow(parent);

			}
		});
		if (listDate.get(position).getLocation() != null)
			viewHolder.location.setText(listDate.get(position).getLocation());
		else
			viewHolder.location.setText("");
		if (listDate.get(position).getNickName() == null)
			viewHolder.nickname.setText("               ");
		else
			viewHolder.nickname.setText(listDate.get(position).getNickName());
		String str = listDate.get(position).getLimitTime();
		if (str != null) {
			String str1 = str.substring(0, 4);
			String str2 = str.substring(4, 6);
			String str3 = str.substring(6, 8);
			viewHolder.leftTime.setText(str1 + "-" + str2 + "-" + str3);
		} else
			viewHolder.leftTime.setText("长期有效");

		switch (listDate.get(position).getAwardStatus()) {
		case 0:
			try {
				viewHolder.reward.setText(listDate.get(position).getTipAward()
						+ "元");
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;
		case 1:
			try {
				viewHolder.reward.setText(listDate.get(position)
						.getSpiritAward() + "");
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;

		default:
			break;
		}
		viewHolder.formalText.setText(listDate.get(position).getContent());
		return convertView;
	}

	static class ViewHolder {
		TextView leftTime, reward, formalText, nickname, location;
		ImageView iv_head, iv_sex;
		ImageButton bt_help;
	}

	public List<Task> getListDate() {
		return listDate;
	}

	public void setListDate(List<Task> listDate) {
		this.listDate = listDate;
	}
}