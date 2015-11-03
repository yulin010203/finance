package com.finance.ui.bean;

import com.finance.ui.GLDisplay;
import com.jogamp.opengl.GL2;

/**
 * 成交量
 * 
 * @author Chen Lin 2015-11-2
 */
public class GLCJL {

	/**
	 * 主图画布
	 */
	private GLDisplay display;
	/**
	 * 颜色(true:红,false:绿)
	 */
	private boolean red = true;
	/**
	 * 成交量
	 */
	private int dealVol;
	/**
	 * 持仓量
	 */
	private int vol;
	/**
	 * 成交量坐标
	 */
	private float dealf;
	/**
	 * 持仓量坐标
	 */
	private float volf;
	/**
	 * 横轴坐标
	 */
	private float x1, x2, x3;
	/**
	 * 简化画法
	 */
	private boolean simple;

	/**
	 * @param display
	 * @param dealVol
	 * @param vol
	 */
	public GLCJL(GLDisplay display, int dealVol, int vol) {
		this.display = display;
		this.dealVol = dealVol;
		this.vol = vol;
	}

	/**
	 * @param x
	 * @param span
	 * @param deal
	 * @param mid
	 * @param del
	 * @param red
	 */
	public void refresh(float x, float span, int deal, float mid, float del, boolean red) {
		this.simple = display.getSpan() < 2.0;
		this.x1 = x - span / 2.0f;
		this.x2 = x;
		this.x3 = x + span / 2.0f;
		this.dealf = 1.9f * dealVol / deal - 1.0f;
		this.volf = (vol - mid) / del * 1.80f;
		this.red = red;

	}

	/**
	 * 屏幕刷新
	 * 
	 * @param gl2
	 */
	public void refresh(GL2 gl2) {
		if (simple) {
			if (red) {
				gl2.glColor3f(0.7f, 0, 0);
			} else {
				// gl2.glColor3f(0.0f, 1.0f, 0.0f);
				gl2.glColor3f(0.0f, 1.0f, 1.0f);
			}
			gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(x2, -1.0f);
			gl2.glVertex2f(x2, dealf);
			gl2.glEnd();
		} else {
			if (red) {
				gl2.glColor3f(0.7f, 0, 0);
				gl2.glBegin(GL2.GL_LINE_STRIP);
				gl2.glVertex2f(x1, -1.0f);
				gl2.glVertex2f(x1, dealf);
				gl2.glVertex2f(x3, dealf);
				gl2.glVertex2f(x3, -1.0f);
				gl2.glEnd();
			} else {
				// gl2.glColor3f(0.0f, 1.0f, 0.0f);
				gl2.glColor3f(0.0f, 1.0f, 1.0f);
				gl2.glBegin(GL2.GL_POLYGON);
				gl2.glVertex2f(x1, -1.0f);
				gl2.glVertex2f(x1, dealf);
				gl2.glVertex2f(x3, dealf);
				gl2.glVertex2f(x3, -1.0f);
				gl2.glEnd();
			}

		}
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public int getDealVol() {
		return dealVol;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public int getVol() {
		return vol;
	}

	/**
	 * 获取持仓量坐标
	 * 
	 * @return
	 */
	public float[] getVolf() {
		float[] v = { x2, volf };
		return v;
	}
}
