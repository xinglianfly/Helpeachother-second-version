package cn.edu.sdu.online.view;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.edu.sdu.online.R;

public class CustomDialog extends Dialog {
	private Button positiveButton, negativeButton;
	private TextView title;
	private TextView content;

	public CustomDialog(Context context) {
		super(context, R.style.CustomDialog);
		setCustomDialog();
	}

	private void setCustomDialog() {
		View mView = LayoutInflater.from(getContext()).inflate(
				R.layout.custom_dialog, null);
		title = (TextView) mView.findViewById(R.id.title);
		content = (TextView) mView.findViewById(R.id.message);
		positiveButton = (Button) mView.findViewById(R.id.positiveButton);
		negativeButton = (Button) mView.findViewById(R.id.negativeButton);
		super.setContentView(mView);
	}

	@Override
	public void setContentView(int layoutResID) {
	}

	@Override
	public void setContentView(View view) {
	}

	public void setTitle(String titlecontent) {
		if (title != null)
			title.setText(titlecontent);

	}
	public void setContent(String titlecontent) {
		if (content != null)
			content.setText(titlecontent);
		
	}
//	// 弹窗
//	private void dialog() {
//		CustomDialog   dialog = new CustomDialog(WorkspaceActivity.this);
//	    EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
//	    dialog.setOnPositiveListener(new OnClickListener() {
//	        @Override
//	        public void onClick(View v) {
//	            //dosomething youself
//	            dialog2.dismiss();
//	        }
//	    });
//	    dialog.setOnNegativeListener(new OnClickListener() {
//	        @Override
//	        public void onClick(View v) {
//	            dialog2.dismiss();
//	        }
//	    });
//	    dialog.show();
//	}
	/**
	 * 确定键监听器
	 * 
	 * @param listener
	 */
	public void setOnPositiveListener(View.OnClickListener listener) {
		positiveButton.setOnClickListener(listener);
	}

	/**
	 * 取消键监听器
	 * 
	 * @param listener
	 */
	public void setOnNegativeListener(View.OnClickListener listener) {
		negativeButton.setOnClickListener(listener);
	}
}