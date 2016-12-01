package com.bigkoo.quicksidebardemo.api;

public class ConstantsURLUtils {
	/*
	 * 汽车超人API接口
	 */
	public static final String URL_ALL_CAR_BRAND = "http://api.qichechaoren.com/superapi/datacenter/car/new/getAllCarBrandOrderByShortCut?plat=CUSTOMER&sourceApp=CAPP";
	public static final String URL_Hot_Brand_List = "http://api.qichechaoren.com/superapi/datacenter/car/new/getHotBrandList?plat=CUSTOMER&sourceApp=CAPP";
	public static final String URL_Car_By_ParentId = "http://api.qichechaoren.com/superapi/datacenter/car/new/getCarByParentId?parentId=%d&plat=CUSTOMER&sourceApp=CAPP";


	/*
	 * 本地服务的API接口  数据接口和汽车超人接口完全统一
	 */
	public static final String LOCAL_SERVER_URL = "http://localhost:8080/CarBrandFetcher/upload/data/";
	
	
	public static final String LOCAL_URL_ALL_CAR_BRAND   = LOCAL_SERVER_URL+"allCarBrand.json";
	public static final String LOCAL_URL_Hot_Brand_List  = LOCAL_SERVER_URL+"hotBrand.json";
	public static final String LOCAL_URL_Car_By_ParentId = LOCAL_SERVER_URL+"carByParentId_%d.json";
	
	
}
