package com.finance.ui;

/**
 * 整个画布
 * 
 * @author Chen Lin 2015-11-2
 */
public class PlotBase {

	/**
	 * 区域宽度
	 */
	protected static int width;
	/**
	 * 区域高度
	 */
	protected static int height;
	/**
	 * GL区域宽度因子(-1,1)
	 */
	protected static float delw;
	/**
	 * GL区域高度因子(-1,1)
	 */
	protected static float delh;

	/**
	 * @param width
	 * @param height
	 */
	protected void init(int width, int height) {
		PlotBase.width = width;
		PlotBase.height = height;
		PlotBase.delw = 2.0f / (width - 1.0f);
		PlotBase.delh = 2.0f / (height - 1.0f);
	}

	/**
	 * 把int坐标转化成float坐标(-1.0f,1.0f)
	 * 
	 * @param x
	 *            横坐标
	 * @return float
	 */
	protected float toxf(int x) {
		return x * delw - 1.0f;
	}

	/**
	 * 把int坐标转化成float坐标(-1.0f,1.0f)
	 * 
	 * @param y
	 *            横坐标
	 * @return float
	 */
	protected float toyf(int y) {
		return y * delh - 1.0f;
	}
}
