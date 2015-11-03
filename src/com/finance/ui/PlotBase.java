package com.finance.ui;

/**
 * @author Chen Lin 2015-11-2
 */
public class PlotBase {

	protected int width;
	protected int height;
	protected float delw;
	protected float delh;

	protected void init(int width, int height) {
		this.width = width;
		this.height = height;
		this.delw = 2.0f / (width - 1.0f);
		this.delh = 2.0f / (height - 1.0f);
	}

	protected float toxf(int x) {
		return x * delw - 1.0f;
	}

	protected float toyf(int y) {
		return y * delh - 1.0f;
	}
}
