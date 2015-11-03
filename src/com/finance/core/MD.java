package com.finance.core;

import java.util.Date;

import com.finance.util.TimeUtil;

/**
 * @author 陈霖 2015-9-19
 */
public class MD {

	/**
	 * 最新价
	 */
	private double price;
	/**
	 * 开盘价
	 */
	private double open;
	/**
	 * 收盘价
	 */
	private double close;
	/**
	 * 最高价
	 */
	private double high;
	/**
	 * 最低价
	 */
	private double low;
	/** 
	 * 成交均价
	 */
	private double avgPrice;
	/**
	 * 成交量
	 */
	private int dealVol;
	/**
	 * 持仓量
	 */
	private int vol;
	/**
	 * 涨跌价格(price-open)
	 */
	private double priceChange;
	/**
	 * 涨跌幅(price-open)/open*100%
	 */
	private double priceChangeRate;
	/**
	 * 行情时间
	 */
	private long timeStamp;
	/**
	 * 时间格式
	 */
	private String format;

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * 设置xxx
	 * 
	 * @param price
	 *            xxx
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getOpen() {
		return open;
	}

	/**
	 * 设置xxx
	 * 
	 * @param open
	 *            xxx
	 */
	public void setOpen(double open) {
		this.open = open;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getClose() {
		return close;
	}

	/**
	 * 设置xxx
	 * 
	 * @param close
	 *            xxx
	 */
	public void setClose(double close) {
		this.close = close;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * 设置xxx
	 * 
	 * @param high
	 *            xxx
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getLow() {
		return low;
	}

	/**
	 * 设置xxx
	 * 
	 * @param low
	 *            xxx
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getAvgPrice() {
		return avgPrice;
	}

	/**
	 * 设置xxx
	 * 
	 * @param avgPrice
	 *            xxx
	 */
	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public int getDealVol() {
		return dealVol;
	}

	/**
	 * 设置xxx
	 * 
	 * @param dealVol
	 *            xxx
	 */
	public void setDealVol(int dealVol) {
		this.dealVol = dealVol;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public int getVol() {
		return vol;
	}

	/**
	 * 设置xxx
	 * 
	 * @param vol
	 *            xxx
	 */
	public void setVol(int vol) {
		this.vol = vol;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getPriceChange() {
		return priceChange;
	}

	/**
	 * 设置xxx
	 * 
	 * @param priceChange
	 *            xxx
	 */
	public void setPriceChange(double priceChange) {
		this.priceChange = priceChange;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public double getPriceChangeRate() {
		return priceChangeRate;
	}

	/**
	 * 设置xxx
	 * 
	 * @param priceChangeRate
	 *            xxx
	 */
	public void setPriceChangeRate(double priceChangeRate) {
		this.priceChangeRate = priceChangeRate;
	}

	/**
	 * 获取当前时间字符串(yyyy/MM/dd HH:mm)
	 * 
	 * @return
	 */
	public String date2Str() {
		if (format == null) {
			format = TimeUtil.date2Str2(new Date(timeStamp));
		}
		return format;
	}
}
