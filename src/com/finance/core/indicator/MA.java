package com.finance.core.indicator;

/**
 * 指标类型:MA指标(移动平均线)<br>
 * 公式参考:MA = SUM(Price, N)/N<br>
 * 
 * @author 陈霖 2015-9-14
 */
public class MA {
	/**
	 * MA平均个数
	 */
	private int n;
	/**
	 * 移动平均值
	 */
	public double ma;
	/**
	 * 横轴坐标
	 */
	private float x;
	/**
	 * MA坐标值
	 */
	private float maf;

	/**
	 * 默认MA5
	 */
	public MA() {
		this(5);
	}

	/**
	 * @param n
	 */
	public MA(int n) {
		this.n = n;
	}

	/**
	 * @param sum
	 */
	public void caculate(double sum) {
		this.ma = sum / n;
	}

	/**
	 * @param ma
	 * @param last
	 * @param close
	 */
	public void caculate(double ma, double last, double close) {
		this.ma = (ma * n - last + close) / n;
	}

	/**
	 * @param x
	 * @param mid
	 * @param del
	 */
	public void refresh(float x, double mid, double del) {
		this.x = x;
		this.maf = (float) ((ma - mid) / del) * 1.80f;
	}

	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	}

	/**
	 * @return the ma
	 */
	public double getMa() {
		return ma;
	}

	/**
	 * @return the ma
	 */
	public float[] getMaf() {
		float[] v = { x, maf };
		return v;
	}

}
