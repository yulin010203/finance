package com.finance.core;

import java.util.Date;

import com.finance.util.TimeUtil;

/**
 * @author 陈霖 2015-9-19
 */
public class MD {

	/**
	 * 合约
	 */
	private String code;
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
	 * 买1价
	 */
	private double bidPrice;
	/**
	 * 买1量
	 */
	private int bidVol;
	/**
	 * 卖1价
	 */
	private double askPrice;
	/**
	 * 卖1量
	 */
	private int askVol;
	/**
	 * 成交金额
	 */
	private double turnover;
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
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param code
	 *            xxx
	 */
	public void setCode(String code) {
		this.code = code;
	}

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
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public double getBidPrice() {
		return bidPrice;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param bidPrice
	 *            xxx
	 */
	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}

	/**
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public int getBidVol() {
		return bidVol;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param bidVol
	 *            xxx
	 */
	public void setBidVol(int bidVol) {
		this.bidVol = bidVol;
	}

	/**
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public double getAskPrice() {
		return askPrice;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param askPrice
	 *            xxx
	 */
	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	/**
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public int getAskVol() {
		return askVol;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param askVol
	 *            xxx
	 */
	public void setAskVol(int askVol) {
		this.askVol = askVol;
	}

	/**
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public double getTurnover() {
		return turnover;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param turnover
	 *            xxx
	 */
	public void setTurnover(double turnover) {
		this.turnover = turnover;
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
	 * 获取 xxx
	 *
	 * @return xxx
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param timeStamp
	 *            xxx
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * 获取当前时间字符串(yyyy/MM/dd HH:mm)
	 * 
	 * @return date
	 */
	public String date2Str() {
		if (format == null) {
			format = TimeUtil.date2Str2(new Date(timeStamp));
		}
		return format;
	}
}
