package com.finance.core.indicator;

/**
 * MACD指标<br>
 * 公式参考:EMA[i](CLOSE,N)= (EMA[i-1]*(N-1)+close[i]*2) / (N+1)<br>
 * DIFF = EMA(CLOSE,SHORT)-EMA(CLOSE,LONG)<br>
 * DEA = EMA(DIFF,M)<br>
 * MACD=2*(DIFF-DEA)<br>
 * 
 * @author 陈霖 2015-9-14
 */
public class MACD {

	/**
	 * MACD对应3个参数
	 */
	private int[] n = new int[3];
	/**
	 * 快线EMA
	 */
	private double emas;
	/**
	 * 慢线EMA
	 */
	private double emal;
	/**
	 * 差离值
	 */
	private double dif;
	/**
	 * 离差平均值
	 */
	private double dea;
	/**
	 * 差值
	 */
	private double del;
	/**
	 * 横轴坐标
	 */
	private float x;
	/**
	 * DIF坐标值
	 */
	private float diff;
	/**
	 * DEA坐标值
	 */
	private float deaf;
	/**
	 * 柱状图坐标值
	 */
	private float delf;

	/**
	 * 默认(12,26,9)
	 */
	public MACD() {
		this(12, 26, 9);
	}

	/**
	 * @param s
	 * @param l
	 * @param m
	 */
	public MACD(int s, int l, int m) {
		this.n[0] = s;
		this.n[1] = l;
		this.n[2] = m;
	}

	/**
	 * @param macd
	 * @param close
	 */
	public void caculate(MACD macd, double close) {
		if (macd == null) {
			this.emas = close;
			this.emal = close;
			this.dea = 0;
			this.dif = 0;
		} else {
			this.emas = (macd.getEmas() * (n[0] - 1) + 2 * close) / (n[0] + 1);
			this.emal = (macd.getEmal() * (n[1] - 1) + 2 * close) / (n[1] + 1);
			this.dif = this.emas - this.emal;
			this.dea = (macd.getDea() * (n[2] - 1) + 2 * dif) / (n[2] + 1);
			this.del = 2 * (dif - dea);
		}
	}

	/**
	 * @param x
	 * @param max
	 */
	public void refresh(float x, double max) {
		this.x = x;
		this.diff = (float) (dif / max) * 0.9f;
		this.deaf = (float) (dea / max) * 0.9f;
		this.delf = (float) (del / max) * 0.9f;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public int[] getN() {
		return n;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public double getEmas() {
		return emas;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public double getEmal() {
		return emal;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public double getDif() {
		return dif;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public double getDea() {
		return dea;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public double getDel() {
		return del;
	}

	/**
	 * 获取dif坐标
	 * 
	 * @return
	 */
	public float[] getDiff() {
		float[] v = { x, diff };
		return v;
	}

	/**
	 * 获取dea坐标
	 * 
	 * @return
	 */
	public float[] getDeaf() {
		float[] v = { x, deaf };
		return v;
	}

	/**
	 * 获取del坐标
	 * 
	 * @return
	 */
	public float[] getDelf() {
		float[] v = { x, delf };
		return v;
	}

	@Override
	public String toString() {
		return "MACD[emas=" + emas + ", emal=" + emal + ", diff=" + dif + ", dea=" + dea + ", del=" + del + "]";
	}

}
