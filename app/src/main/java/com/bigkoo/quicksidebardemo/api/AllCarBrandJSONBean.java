package com.bigkoo.quicksidebardemo.api;

import java.util.List;

public class AllCarBrandJSONBean {

	private int code;
	private List<CarBrandOBJ> info;
	private String msg;
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<CarBrandOBJ> getInfo() {
		return info;
	}

	public void setInfo(List<CarBrandOBJ> info) {
		this.info = info;
	}
	
	

	@Override
	public String toString() {
		return "AllCarBrandJSONBean [code=" + code + ", info=" + info + ", msg="+msg+" ]";
	}



	public static class CarBrandOBJ {

		private String carCategoryName;
		private String shortCut;
		private int carCategoryId;
		private String logoImg;
		private String localServerImg;
		
		private int parentId;
		private int carCategoryType;
		private String series;
		private String manufacturer;


		public String getLocalServerImg() {
			return localServerImg;
		}

		public void setLocalServerImg(String localServerImg) {
			this.localServerImg = localServerImg;
		}

		public String getSeries() {
			return series;
		}

		public void setSeries(String series) {
			this.series = series;
		}

		public String getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}

		public int getCarCategoryType() {
			return carCategoryType;
		}

		public void setCarCategoryType(int carCategoryType) {
			this.carCategoryType = carCategoryType;
		}

		public CarBrandOBJ() {

		}

		public CarBrandOBJ(String carCategoryName, String shortCut, int carCategoryId, String logoImg) {
			super();
			this.carCategoryName = carCategoryName;
			this.shortCut = shortCut;
			this.carCategoryId = carCategoryId;
			this.logoImg = logoImg;
		}

		public String getCarCategoryName() {
			return carCategoryName;
		}

		public void setCarCategoryName(String carCategoryName) {
			this.carCategoryName = carCategoryName;
		}

		public String getShortCut() {
			return shortCut;
		}

		public void setShortCut(String shortCut) {
			this.shortCut = shortCut;
		}

		public int getCarCategoryId() {
			return carCategoryId;
		}

		public void setCarCategoryId(int carCategoryId) {
			this.carCategoryId = carCategoryId;
		}

		public String getLogoImg() {
			return logoImg;
		}

		public void setLogoImg(String logoImg) {
			this.logoImg = logoImg;
		}

		@Override
		public String toString() {
			return "CarBrandOBJ [carCategoryName=" + carCategoryName + ", shortCut=" + shortCut + ", carCategoryId="
					+ carCategoryId + ", logoImg=" + logoImg + ", localServerImg=" + localServerImg + ", parentId="
					+ parentId + ", carCategoryType=" + carCategoryType + ", series=" + series + ", manufacturer="
					+ manufacturer + "]";
		}

		
		
		

	}

}
