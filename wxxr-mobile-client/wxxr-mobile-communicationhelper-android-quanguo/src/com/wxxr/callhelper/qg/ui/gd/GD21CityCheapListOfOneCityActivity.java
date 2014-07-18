package com.wxxr.callhelper.qg.ui.gd;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.GDMiniInfoPushMsgAdapter;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.bean.UrlBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IFetchURLofContent;
import com.wxxr.callhelper.qg.service.IGDDownWebService;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;

public class GD21CityCheapListOfOneCityActivity extends BaseActivity {

	GDMiniInfoPushMsgAdapter madaper;
	private ListView listview;
	String area = null;
	private TextView titletextview;
	int readcount = 0;
	final long RE_COUNT_TIME = 10000;
	static  Hashtable<String,String>  city=new  Hashtable<String,String>();
	static{
		
		
		
//		city.put("gz:ch", "广州");
//		city.put("sz:ch", "深圳");
//		city.put("dg:ch", "东莞");
//		city.put("fs:ch", "佛山");
//		city.put("zs:ch", "中山");
//		city.put("hz:ch", "惠州");
//		city.put("zh:ch", "珠海");
//		city.put("jm:ch", "江门");
//		city.put("zq:ch", "肇庆");
//		city.put("zj:ch", "湛江");
//		city.put("st:ch", "汕头");
//		city.put("qy:ch", "清远");
//		city.put("jy:ch", "揭阳");
//		city.put("mm:ch", "茂名");
//		city.put("mz:ch", "梅州");
//		city.put("sg:ch", "韶关");
//		city.put("yj:ch", "阳江");
//		city.put("yf:ch", "云浮");
//		city.put("hy:ch", "河源");
//		city.put("sw:ch", "汕尾");
//		city.put("cz:ch", "潮州");
	
	}
	
	Handler mhandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			ArrayList<HtmlMessageBean> result = getService(
					IGDDownWebService.class).getCheapsOfOneCity(
					 area.split(":")[0]);

			if (result != null && result.size() > 0&&result.get(0).getHtmlMessage().getTitle()!=null
					&&result.get(0).getHtmlMessage().getTitle().length()>0) {
//				titletextview
//				.setText(result.get(0).getHtmlMessage()
//						.getSource());
				madaper.updateData(result);
				madaper.notifyDataSetChanged();
				processbar.setVisibility(View.GONE);
			} else if (readcount < 3) {
				readcount++;
				mhandler.sendEmptyMessageDelayed(0, RE_COUNT_TIME);
			}else{
				processbar.setVisibility(View.GONE);
				nonetdiv.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
			}

		};

	};
	private View nonetdiv;
	private View processbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_cheaplist);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		nonetdiv=findViewById(R.id.no_network);
		processbar=findViewById(R.id.processbar);
		processbar.setVisibility(View.VISIBLE);
	
		
	//	processdialog.setView(view);
		
		titletextview = (TextView) findViewById(R.id.tv_titlebar_name);
		
		area = URLDecoder.decode(getIntent().getStringExtra("area"));
		String strcity = URLDecoder.decode(getIntent().getStringExtra("city"));
		if(city.containsKey(area)){
			titletextview.setText(city.get(area)+"优惠");
		}else if(strcity!=null){
			titletextview.setText(strcity+"优惠");
		}else{
		    titletextview.setText("地市优惠");
		}
		madaper = new GDMiniInfoPushMsgAdapter(this, R.layout.gd_weblistitem, 1);
		listview = (ListView) findViewById(R.id.gd_cheaplist);
		listview.setAdapter(madaper);
        listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HtmlMessageBean messageBean = madaper.getItem(position);
				Intent intent = new Intent(GD21CityCheapListOfOneCityActivity.this,
						GDPushMsgDetailActivity.class);
				intent.putExtra(Constant.PINGDAO_INFO, messageBean);
				intent.putExtra("title",messageBean.getHtmlMessage().getTitle());
				intent.putExtra(Constant.SHARE_ID,ActivityID.SAHRE_FROM_YOUHUI.ordinal());
				
				startActivity(intent);
			}
        	
        	
		});
		
		
		CMProgressMonitor monitor = new CMProgressMonitor(this.getBaseContext()) {

			@Override
			protected void handleDone(final Object returnVal) {
				// TODO Auto-generated method stub
				if (returnVal instanceof ArrayList) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (((ArrayList<HtmlMessageBean>) returnVal).size() > 0) {
//								titletextview
//										.setText(((ArrayList<HtmlMessageBean>) returnVal)
//												.get(0).getHtmlMessage()
//												.getTitle());
								
								madaper.updateData((ArrayList<HtmlMessageBean>) returnVal);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										madaper.notifyDataSetChanged();
										processbar.setVisibility(View.GONE);
									}
								});
							} else {
								readcount++;
								mhandler.sendEmptyMessageDelayed(0,
										RE_COUNT_TIME);
							}
							
						}
					});

				}
			}

			@Override
			protected void handleFailed(Throwable cause) {
				readcount++;
				mhandler.sendEmptyMessageDelayed(0, RE_COUNT_TIME);
			}

		};
		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				UrlBean list = getService(
						IFetchURLofContent.class).getContentOfCheapListUrl(area.split(":")[0]);
					

				String leng[] = new String[list.getUrl().size()];
				
				
				leng=	list.getUrl().toArray(leng);
				
				// leng=new
				// String[]{"http://public.cmhelper.7500.com.cn/magnoliaPublic/commontxzs/home/node17.html","http://public.cmhelper.7500.com.cn/magnoliaPublic/commontxzs/home/node15.html"};
				getService(IOfficeLineHtmlProvideService.class)
						.downCheapsOfOneCity(leng, "dsyh:cu-" + area);
				ArrayList<HtmlMessageBean> result = getService(
						IGDDownWebService.class).getCheapsOfOneCity(
						"dsyh:cu-" + area);
				return result;
			}
		});

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		default:
			break;
		}
	}
	
}
