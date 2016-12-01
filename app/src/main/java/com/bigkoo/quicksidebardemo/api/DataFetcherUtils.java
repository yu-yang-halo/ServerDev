package com.bigkoo.quicksidebardemo.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataFetcherUtils {
	private static final String URL_ALL_CAR_BRAND = "http://api.qichechaoren.com/superapi/datacenter/car/new/getAllCarBrandOrderByShortCut?plat=CUSTOMER&sourceApp=CAPP";
	private static final String URL_Hot_Brand_List = "http://api.qichechaoren.com/superapi/datacenter/car/new/getHotBrandList?plat=CUSTOMER&sourceApp=CAPP";
	private static final String URL_Car_By_ParentId = "http://api.qichechaoren.com/superapi/datacenter/car/new/getCarByParentId?parentId=%d&plat=CUSTOMER&sourceApp=CAPP";

	/*
	 * 车型图片的保存目录
	 */
	private static final String SAVE_IMAGE_DIR = "H:/workspace/CarBrandFetcher/WebContent/upload/logo/";
	
	/*
	 * 车型JSON数据的保存目录
	 */
	private static final String SAVE_JSON_DIR = "H:/workspace/CarBrandFetcher/WebContent/upload/data/";
	
	/*
	 * 车型JSON数据的保存文件名称
	 */
	private static final String ALL_CAR_BRAND_JSON_FILE_NAME   = "allCarBrand.json";
	private static final String Hot_Brand_List_JSON_FILE_NAME  = "hotBrand.json";
	private static final String Car_By_ParentId_JSON_FILE_NAME = "carByParentId_%d.json";

	private static final String SERVER_IMAGE_ROOT_PATH = "http://localhost:8080/CarBrandFetcher/upload/logo/";

	public static void main(String[] args) {
		executeTask();

	}
	
	public static void executeTask(){
		/*
		 * step0      下载所有车型的图片并保存到本地磁盘              /upload/logo/
		 * step1      下载所有车型的JSON数据   并添加本地服务器的image路径（字段localServerImg）    最终保存到本地磁盘                /upload/data/
		 *         【根据 carCategoryId 获取子系列】     step1_2(carCategoryId)
		 * step2      下载热门车型的JSON数据   并添加本地服务器的image路径（字段localServerImg）    最终保存到本地磁盘                /upload/data/
		 * 
		 */
		
		
		//step0();
		
		step1();
		
		step2();
		
	
		
		
	}
	
	/*
	 *  所有车型的图片的下载
	 */
	private static void step0(){
		String JSON_ALL_CAR_BRAND=DataFetcherUtils.fetechNetWorkData(URL_ALL_CAR_BRAND);
		Gson gson=new Gson();
		Type type = new TypeToken<AllCarBrandJSONBean>(){}.getType();
		AllCarBrandJSONBean bean=gson.fromJson(JSON_ALL_CAR_BRAND,type);
		for(AllCarBrandJSONBean.CarBrandOBJ obj : bean.getInfo()){
			downloadImageResources(obj.getLogoImg(), SAVE_IMAGE_DIR);
		}
	}
	
	
	/*
	 *  所有车型接口返回的JSON数据的处理
	 */
	private static void step1(){
		String JSON_ALL_CAR_BRAND=DataFetcherUtils.fetechNetWorkData(URL_ALL_CAR_BRAND);
		Gson gson=new Gson();
		Type type = new TypeToken<AllCarBrandJSONBean>(){}.getType();
		AllCarBrandJSONBean bean=gson.fromJson(JSON_ALL_CAR_BRAND,type);
		for(AllCarBrandJSONBean.CarBrandOBJ obj : bean.getInfo()){
			String url=obj.getLogoImg();
			if(url!=null&&!url.trim().equals("")){
				obj.setLocalServerImg(SERVER_IMAGE_ROOT_PATH+url.substring(url.lastIndexOf("/")+1));
			}
			
			step1_2(obj.getCarCategoryId());
			
		}
		String beanJSON=gson.toJson(bean);
		try {
			copyFileStreamToDisk(StringTOInputStream(beanJSON), SAVE_JSON_DIR+ALL_CAR_BRAND_JSON_FILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 *  热门车型接口返回的JSON数据的处理
	 */
	private static void step2(){
		String JSON_Hot_Brand_List=DataFetcherUtils.fetechNetWorkData(URL_Hot_Brand_List);
		Gson gson=new Gson();
		Type type = new TypeToken<AllCarBrandJSONBean>(){}.getType();
		AllCarBrandJSONBean bean=gson.fromJson(JSON_Hot_Brand_List,type);
		for(AllCarBrandJSONBean.CarBrandOBJ obj : bean.getInfo()){
			String url=obj.getLogoImg();
			if(url!=null&&!url.trim().equals("")){
				obj.setLocalServerImg(SERVER_IMAGE_ROOT_PATH+url.substring(url.lastIndexOf("/")+1));
			}
		}
		String beanJSON=gson.toJson(bean);
		try {
			copyFileStreamToDisk(StringTOInputStream(beanJSON), SAVE_JSON_DIR+Hot_Brand_List_JSON_FILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *  热门车型接口返回的JSON数据的处理
	 */
	private static void step1_2(int parentId){
		String JSON_Car_By_ParentId=DataFetcherUtils.fetechNetWorkData(String.format(URL_Car_By_ParentId, parentId));
		Gson gson=new Gson();
		Type type = new TypeToken<AllCarBrandJSONBean>(){}.getType();
		AllCarBrandJSONBean bean=gson.fromJson(JSON_Car_By_ParentId,type);
		for(AllCarBrandJSONBean.CarBrandOBJ obj : bean.getInfo()){
			String url=obj.getLogoImg();
			if(url!=null&&!url.trim().equals("")){
				obj.setLocalServerImg(SERVER_IMAGE_ROOT_PATH+url.substring(url.lastIndexOf("/")+1));
			}
		}
		String beanJSON=gson.toJson(bean);
		try {
			copyFileStreamToDisk(StringTOInputStream(beanJSON), SAVE_JSON_DIR+String.format(Car_By_ParentId_JSON_FILE_NAME,parentId));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	

	public static InputStream StringTOInputStream(String str) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes("gbk"));
		return is;
	}

	private static void downloadImageResources(String urlString, String toDir) {
		String fileName = urlString.substring(urlString.lastIndexOf("/") + 1);
		final String savePath = toDir + fileName;

		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 创建一个Request
		final Request request = new Request.Builder().url(urlString).build();
		// new call
		Call call = mOkHttpClient.newCall(request);
		// 请求加入调度
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				System.err.println("exception : " + arg1);
			}

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						copyFileStreamToDisk(arg1.body().byteStream(), savePath);
					}
				}).start();

			}
		});
	}

	private static void copyFileStreamToDisk(InputStream is, String savePath) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			fos = new FileOutputStream(savePath);
			bos = new BufferedOutputStream(fos);
			bis = new BufferedInputStream(is);

			byte[] buffer = new byte[1024];
			int len = -1;
			int size = 0;
			while ((len = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
				size += len;
			}
			bos.flush();
			fos.flush();
			System.out.println("size:" + size);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {

				if (bos != null) {
					bos.close();
				}
				if (bis != null) {
					bis.close();
				}

				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static String fetechNetWorkData(String urlString) {
		StringBuffer sb = null;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			conn.setConnectTimeout(11 * 1000);
			conn.setReadTimeout(11 * 1000);

			is = conn.getInputStream();
			sb = new StringBuffer();

			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = is.read(buffer)) > 0) {

				sb.append(new String(buffer, 0, i,"utf-8"));//utf-8
			}
			System.err.println(sb.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();

	}

}
