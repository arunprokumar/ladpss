package com.adobe.aem.lacounty.dpss.core.beans;

import java.util.List;

public class HomePageBannerBean {

	String title;
	String seachPlaceHolder;
	List<HomePageBannerButtonBean> bannerButtons;
	public String getTitle() {
		return title;
	}
	public String getSeachPlaceHolder() {
		return seachPlaceHolder;
	}
	public List<HomePageBannerButtonBean> getBannerButtons() {
		return bannerButtons;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setSeachPlaceHolder(String seachPlaceHolder) {
		this.seachPlaceHolder = seachPlaceHolder;
	}
	public void setBannerButtons(List<HomePageBannerButtonBean> bannerButtons) {
		this.bannerButtons = bannerButtons;
	}
	
}
