package com.finance.core;

/**
 * 图区大小及坐标(左下角原点)
 */
public class Bound {
	/**
	 * 区域的左下角横坐标
	 */
	public int x;
	/**
	 * 区域的左下角纵坐标
	 */
	public int y;
	/**
	 * 区域宽度
	 */
	public int width;
	/**
	 * 区域高度
	 */
	public int height;
	/**
	 * GL区域宽度因子(-1,1)
	 */
	public float delw;
	/**
	 * GL区域高度因子(-1,1)
	 */
	public float delh;

	/**
	 * 把int坐标转化成float坐标(-1.0f,1.0f)
	 * 
	 * @param x
	 *            横坐标
	 * @return float
	 */
	public float toxf(int x) {
		return x * delw - 1.0f;
	}

	/**
	 * 把int坐标转化成float坐标(-1.0f,1.0f)
	 * 
	 * @param y
	 *            横坐标
	 * @return float
	 */
	public float toyf(int y) {
		return y * delh - 1.0f;
	}

	@Override
	public String toString() {
		return "Bound(" + x + "," + y + "," + width + "," + height + ")";
	}
}