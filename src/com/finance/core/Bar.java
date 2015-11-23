package com.finance.core;

import java.util.Date;

import com.finance.util.TimeUtil;

/**
 * @author 陈霖 2015-8-31
 */
public class Bar {

	/**
	 * 合约编码
	 */
	private String code;
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
	 * 周期
	 */
	private long period;
	/**
	 * 开始时间
	 */
	private long startTime;
	/**
	 * 结束时间
	 */
	private long endTime;
	/**
	 * 时间格式
	 */
	private String timeFormat;
	/**
	 * 时间格式
	 */
	private String format;
	/**
	 * K线状态(开始0/中间1/结束2)
	 */
	private int status = 0;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * 设置xxx
	 * 
	 * @param period
	 *            xxx
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * 设置xxx
	 * 
	 * @param startTime
	 *            xxx
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * 设置xxx
	 * 
	 * @param endTime
	 *            xxx
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 设置xxx
	 * 
	 * @param format
	 *            xxx
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public boolean isStart() {
		return status == 0;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public boolean isMidddle() {
		return status == 1;
	}

	/**
	 * 获取xxx
	 * 
	 * @return xxx
	 */
	public boolean isEnd() {
		return status == 2;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param status
	 *            xxx
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 更新价格波动
	 */
	public void updatePriceChange() {
		this.priceChange = open - close;
		if (open != 0) {
			this.priceChangeRate = this.priceChange / this.open * 100d;
		}
	}

	/**
	 * 获取当前时间格式
	 * 
	 * @return time
	 */
	public String time2Str() {
		if (timeFormat == null || !isEnd()) {
			if (period >= 3600 * 1000) {
				timeFormat = TimeUtil.date2Str3(new Date(startTime));
			} else {
				timeFormat = TimeUtil.time2Str3(new Date(startTime));
			}
		}
		return timeFormat;
	}

	/**
	 * 获取当前时间字符串(yyyy/MM/dd HH:mm~HH:mm)
	 * 
	 * @return time
	 */
	public String date2Str() {
		if (format == null || !isEnd()) {
			format = TimeUtil.date2Str2(new Date(startTime)) + "~" + TimeUtil.time2Str2(new Date(endTime));
		}
		return format;
	}

}
