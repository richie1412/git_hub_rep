/**
 * 
 */
package com.wxxr.mobile.callhelper.model;

import com.wxxr.mobile.android.ui.AndroidBindingType;
import com.wxxr.mobile.android.ui.annotation.AndroidBinding;
import com.wxxr.mobile.core.ui.annotation.Command;
import com.wxxr.mobile.core.ui.annotation.Navigation;
import com.wxxr.mobile.core.ui.annotation.View;
import com.wxxr.mobile.core.ui.annotation.ViewGroup;
import com.wxxr.mobile.core.ui.api.IViewGroup;
import com.wxxr.mobile.core.ui.api.InputEvent;
import com.wxxr.mobile.core.ui.common.PageBase;




/**
 * @author fudapeng
 *
 */
@View(name = "home", withToolbar = true, description = "通信")
@AndroidBinding(type = AndroidBindingType.FRAGMENT_ACTIVITY, layoutId = "R.layout.home_page_new")
public abstract class HomePage extends PageBase {
	
	private static final String[] VIEWIDs = new String[] { "MainHomeView",
		"MicroMessageView","PlazaHomeView", "PersonalCentreView" };
	
	
	@ViewGroup(viewIds = { "MainHomeView", "MicroMessageView", "PlazaHomeView",
			"PersonalCentreView" })
	IViewGroup contents;
	
	
	/**
	 * 首页点击
	 * 
	 * @param event
	 * @return
	 */
	@Command(navigations = { @Navigation(on = "*", showView = "MainHomeView") })
	String goHome(InputEvent event) {
		return "";
	}

	/**
	 * 交易微资讯
	 * 
	 * @param event
	 * @return
	 */
	@Command(navigations = { @Navigation(on = "*", showView = "MicroMessageView") })
	String goNews(InputEvent event) {
		return "";
	}

	/**
	 * 广场
	 * 
	 * @param event
	 * @return
	 */
	@Command(navigations = { @Navigation(on = "*", showView = "PlazaHomeView") })
	String goPlaza(InputEvent event) {
		return "";
	}

	/**
	 * 行情个人中心
	 * 
	 * @param event
	 * @return
	 */
	@Command(navigations = { @Navigation(on = "*", showView = "PersonalCentreView") })
	String goPersonalCentre(InputEvent event) {
		return "";
	}

		
}
