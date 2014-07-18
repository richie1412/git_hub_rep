package com.wxxr.callhelper.qg.service;

/**
 * @author cuizaixi
 */
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.util.StringUtils;

public class GuishudiServiceWraper implements ICommonNumberService {
	private String regionName;
	private String rw;
	public GuishudiServiceWraper() {
		super();
	}
	@Override
	public boolean isHandle(String num) {
		Region r = null;
		if (!StringUtils.isBlank(num)) {
			if (num.startsWith("0")) {
				if (num.length() >= 4) {
					r = AppUtils.getService(IGuiShuDiService.class)
							.getRegionByDialogNumber(num);
				}
			} else {
				r = AppUtils.getService(IGuiShuDiService.class)
						.getRegionByMsisdn(num);
			}
		}
		if (r != null) {
			StringBuilder sb = new StringBuilder();
			if (r.getpRegionName() != null) {
				sb.append(r.getpRegionName() + " ");
			}
			if (r.getRegionName() != null) {
				sb.append(r.getRegionName() + " ");
				this.rw = sb.toString();
			}
			if (r.getBrandName() != null) {
				sb.append(r.getBrandName());
			}
			this.regionName = sb.toString();
			return true;
		}
		return false;
	}
	@Override
	public void initData() {

	}

	@Override
	public String getRegion() {
		return this.regionName;
	}
	@Override
	public String getRegionWithoutBrand() {
		return this.rw;
	}

}
