package com.wxxr.callhelper.qg.widget.pick;
//package com.wxxr.callhelper.widget.pick;
//
///**
// * ʡ����������ҳ�棬ʹ���Զ���ؼ�
// */
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import cn.newcom.db.DialogCityDB;
//import cn.newcom.domain.China;
//import cn.newcom.domain.DateForAge;
//import cn.newcom.domain.DialogCityItem;
//import cn.newcom.pickdemo.R;
//import cn.newcom.wheelview.OnWheelChangedListener;
//import cn.newcom.wheelview.WheelView;
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//public class PickViewDemoActivity extends Activity {
//	private WheelView province;// ʡ�ݿؼ�
//	private WheelView city;
//	private WheelView town;
//	private String pro;
//	private String ct;
//	private String tw;
//	private List<String> pros;// ʡ����Ƽ���
//	private List<String> cities;
//	private List<String> towns;
//	private List<DialogCityItem> countyInfo;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.cities_layout);
//		province = (WheelView) findViewById(R.id.province);
//		city = (WheelView) findViewById(R.id.city);
//		town = (WheelView) findViewById(R.id.town);
//		DialogCityDB.insertForeignContry(getApplicationContext());
//		initPickPro();
//	}
//	// ��ʼ��ʡ���б�
//	private void initPickPro() {
//		// String[] states = China.states;
//		// List<String> pros = Arrays.asList(states);
//		List<String> years = DateForAge.getYears();
//		province.setAdapter(new ArrayWheelAdapter1(years));
//		province.setCurrentItem(years.indexOf("1980��"), false);
//		// province.addChangingListener(new ProListener());
//	}
//
//	// ʡ���б?������
//	private class ProListener implements OnWheelChangedListener {
//		@Override
//		public void onChanged(WheelView wheel, int oldValue, int newValue) {
//			pro = pros.get(newValue);
//			initPickCity(pro);
//		}
//	}
//
//	// ��ʼ�������б�
//	public void initPickCity(String pro) {
//		cities = DialogCityDB.getCityInfoStr(this, pro);
//		city.setAdapter(new ArrayWheelAdapter1(cities));
//		city.setCurrentItem(cities.size() / 2);
//		city.addChangingListener(new CityListener());
//	}
//
//	private class CityListener implements OnWheelChangedListener {
//		@Override
//		public void onChanged(WheelView wheel, int oldValue, int newValue) {
//			ct = cities.get(newValue);
//			initPickTown(pro, ct);
//		}
//	}
//	public void initPickTown(String pro, String city) {
//		countyInfo = DialogCityDB.getCountyInfo(this, pro, city);
//		towns = new ArrayList<String>();
//		for (int i = 0; i < countyInfo.size(); i++) {
//			towns.add(countyInfo.get(i).getName());
//		}
//		town.setAdapter(new ArrayWheelAdapter1(towns));
//		town.setCurrentItem(towns.size() / 2);
//		town.addChangingListener(new TownListener());
//	}
//	private String desc;
//	private String pcode;
//	private class TownListener implements OnWheelChangedListener {
//		@Override
//		public void onChanged(WheelView wheel, int oldValue, int newValue) {
//			DialogCityItem dc = countyInfo.get(newValue);
//			desc = dc.getDesc();
//			pcode = dc.getPcode();
//		}
//	}
//	public void done(View v) {
//		String content = desc + "," + pcode;
//		Toast.makeText(this, content, 0).show();
//	}
//}