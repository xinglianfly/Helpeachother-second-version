package cn.edu.sdu.online.adapter;

 import java.util.List;

import cn.edu.sdu.online.R;
 
 import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
 public class CityAdapter extends BaseAdapter{
 
 	private Context context;
 	private List<String> data;
 	private LayoutInflater mLayoutInflater;
 	private List<Integer> letterCharList;
 	private String[] title;
 	
 	public CityAdapter(Context context, List<String> data, 
 			List<Integer> letterCharList, String[] title) {
 		super();
 		this.context = context;
 		this.data = data;
 		this.letterCharList = letterCharList;
 		this.title = title;
 	}
 
 	public int getCount() {
 		return data.size();
 	}
 
 	public Object getItem(int position) {
 		return data.get(position);
 	}
 
 	public long getItemId(int position) {
 		return position;
 	}
 	
 	public View getView(int position, View convertView, ViewGroup parent) {
 		if(mLayoutInflater == null){
 			mLayoutInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		}
 		if(convertView == null){
 			convertView = mLayoutInflater.inflate(R.layout.city_list_item, null, false);
 		}
 		TextView tv02 = (TextView) convertView.findViewById(R.id.mainlist_item_tv01);
 		if(letterCharList.get(position) >= 0){
 			tv02.setVisibility(0);
 			tv02.setText(title[letterCharList.get(position)]);
 		}else{
 			tv02.setVisibility(View.GONE);
 		}

 		TextView tv01 = (TextView) convertView.findViewById(R.id.mainlist_item_tv02);
 		tv01.setText(data.get(position));
 		return convertView;
 	}
 
 }