package cn.edu.sdu.online.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Place;
import cn.edu.sdu.online.util.PublishDestinatePlace;

public class SelectPlaceActivity extends Activity implements OnClickListener {
	String TAG = "SelectPlaceActivity";
	private ImageView backButton;
	// list
	private ListView listView;
	private SimpleAdapter adapter;
	// 用于显示数据的list
	List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
	// 存放place对象的list
	List<Place> places;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_place);
		getData();
		init();

	}

	private void getData() {
		places = (List<Place>) getIntent().getSerializableExtra("places");
		for (int i = 0; i < places.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Place place = places.get(i);
			map.put("name", place.getName());
			map.put("addr", place.getAddr());

			listData.add(map);
		}

	}

	private void init() {
		listView = (ListView) findViewById(R.id.list_place);
		backButton = (ImageView) findViewById(R.id.im_back);
		backButton.setTag(0);
		backButton.setOnClickListener(this);
		adapter = new SimpleAdapter(this, listData,
				R.layout.listitem_sele_place, new String[] { "name", "addr" },
				new int[] { R.id.tv_name, R.id.tv_addr });
		listView.setAdapter(adapter);
		setItemClick();

	}

	private void setItemClick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Place place = places.get(arg2);

				PublishDestinatePlace.setPlace(place.getName(), place.getAddr(),
						place.getLongitude(), place.getLatitude());
				SelectPlaceActivity.this.finish();

			}
		});

	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		switch (tag) {
		case 0:
			// 返回键
			this.finish();
			Log.v(TAG, "finish()");

			break;

		default:
			break;
		}

	}

}
