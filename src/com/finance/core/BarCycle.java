package com.finance.core;

/**
 * @author YuLin Nov 12, 2015
 */
public class BarCycle {

	/**
	 * 合约代码
	 */
	private String code;
	/**
	 * K线同期
	 */
	private long period;

	/**
	 * @param code
	 * @param period
	 */
	public BarCycle(String code, long period) {
		this.code = code;
		this.period = period;
	}

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
	 * @return the period
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof BarCycle) {
			BarCycle cycle = (BarCycle) obj;
			if (this.code.equals(cycle.code) && this.period == cycle.period) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) (code.hashCode() + period);
	}

	@Override
	public String toString() {
		return code + "@" + period;
	}
}
