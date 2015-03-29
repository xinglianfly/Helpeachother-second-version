package cn.edu.sdu.online.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.edu.sdu.online.R;
import cn.edu.sdu.online.entity.Place;
import cn.edu.sdu.online.share.FloatApplication;
import cn.edu.sdu.online.util.DialogUtil;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

public class SearchDestinationActivity extends Activity implements
		View.OnClickListener, OnGetPoiSearchResultListener,
		OnGetSuggestionResultListener {
	String TAG = "SearchDestinationActivity";
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;
	Dialog dialog;

	Button searchButton, backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_search_dest);
		init();
	}

	private void init() {
		Log.v(TAG, "initailize");
		searchButton = (Button) findViewById(R.id.bt_search);
		searchButton.setTag(0);
		searchButton.setOnClickListener(this);
		backButton = (Button) findViewById(R.id.bt_back);
		backButton.setTag(1);
		backButton.setOnClickListener(this);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.tv_search);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		keyWorldsView.setAdapter(sugAdapter);
		setTextChangeListener();
	}

	private void setTextChangeListener() {
		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		keyWorldsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				String city = FloatApplication.getApp().getLocalCity();
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(city));
			}
		});
	}

	public void startSearch() {
		Log.v(TAG, "开始查询地点...");
		String city = FloatApplication.getApp().getLocalCity();
		if (keyWorldsView.getText().toString().trim().length() == 0) {
			return;
		}
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
				.keyword(keyWorldsView.getText().toString().trim())
				.pageNum(load_Index));

	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		switch (tag) {
		case 0:
			// 搜索按钮点击事件
			startSearch();
			// 等待对话框
			dialog = DialogUtil.createLoadingDialog(this,
					getString(R.string.waitingDialog));
			dialog.show();
			break;
		case 1:
			this.finish();

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (dialog != null)
			dialog.dismiss();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (dialog != null) {
			dialog.dismiss();
		}
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(SearchDestinationActivity.this, "未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// finish此页，跳转到下一页选择地点，选择好地点后返回给publishactivity
			List<Place> places = new ArrayList<Place>();
			Place place;
			for (int i = 0; i < result.getAllPoi().size(); i++) {
				place = new Place();
				place.setName(result.getAllPoi().get(i).name);
				place.setAddr(result.getAllPoi().get(i).address);
				place.setLongitude(result.getAllPoi().get(i).location.longitude);
				place.setLatitude(result.getAllPoi().get(i).location.latitude);
				places.add(place);
			}
			Intent intent = new Intent(SearchDestinationActivity.this,
					SelectPlaceActivity.class);
			intent.putExtra("places", (Serializable) places);
			startActivity(intent);
			this.finish();

		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(SearchDestinationActivity.this, strInfo,
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

}
