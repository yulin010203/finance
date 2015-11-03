package com.finance.ui.bean;

import com.finance.core.Bar;
import com.finance.ui.GLDisplay;
import com.jogamp.opengl.GL2;

/**
 * @author 陈霖 2015-9-9
 */
public class GLBar {

	/**
	 * 主图画布
	 */
	private GLDisplay display;
	/**
	 * K线数据
	 */
	private Bar bar;
	/**
	 * 图区内中间价位
	 */
	private double mid;
	/**
	 * 图区内价差
	 */
	private double del;
	/**
	 * K线对应矩阵
	 */
	private K k = new K();
	/**
	 * K线宽度过小时，不显示宽度
	 */
	private boolean simple;

	/**
	 * @param display
	 * @param bar
	 * 
	 */
	public GLBar(GLDisplay display, Bar bar) {
		this.display = display;
		this.bar = bar;
	}

	/**
	 * K线更新
	 * 
	 * @param x
	 * @param span
	 * @param mid
	 * @param del
	 */
	public void refresh(float x, float span, double mid, double del) {
		if (del <= 0) {
			return;
		}
		this.mid = mid;
		this.del = del;
		this.simple = display.getSpan() < 2.0;
		refresh();
		k.x1 = x - span / 2.0f;
		k.x2 = x;
		k.x3 = x + span / 2.0f;
	}

	/**
	 * 刷新K线值
	 */
	private void refresh() {
		k.open = (float) ((bar.getOpen() - mid) / del) * 1.80f;
		k.close = (float) ((bar.getClose() - mid) / del) * 1.80f;
		k.high = (float) ((bar.getHigh() - mid) / del) * 1.80f;
		k.low = (float) ((bar.getLow() - mid) / del) * 1.80f;
	}

	/**
	 * 屏幕刷新
	 * 
	 * @param gl2
	 */
	public void refresh(GL2 gl2) {
		if (!bar.isFinished()) {
			refresh();
		}
		refresh(gl2, k);
	}

	/**
	 * 已经完成的K线只刷新矩阵
	 * 
	 * @param gl2
	 * @param k
	 */
	private void refresh(GL2 gl2, K k) {
		// K线过多，只显示最高最低图
		if (simple) {
			if (k.open < k.close) {
				gl2.glColor3f(0.7f, 0, 0);
			} else if (k.open > k.close) {
				// gl2.glColor3f(0.0f, 1.0f, 0.0f);
				gl2.glColor3f(0.0f, 1.0f, 1.0f);
			} else {
				gl2.glColor3f(0.5f, 0.5f, 0.5f);
			}
			gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(k.x2, k.high);
			gl2.glVertex2f(k.x2, k.low);
			gl2.glEnd();
		} else {
			if (k.open < k.close) {
				// 阳线
				gl2.glColor3f(0.7f, 0, 0);
				if (k.high > k.close) {
					gl2.glBegin(GL2.GL_LINES);
					gl2.glVertex2f(k.x2, k.high);
					gl2.glVertex2f(k.x2, k.close);
					gl2.glEnd();
				}
				gl2.glBegin(GL2.GL_LINE_STRIP);
				gl2.glVertex2f(k.x1, k.open);
				gl2.glVertex2f(k.x3, k.open);
				gl2.glVertex2f(k.x3, k.close);
				gl2.glVertex2f(k.x1, k.close);
				gl2.glVertex2f(k.x1, k.open);
				gl2.glEnd();
				if (k.low < k.open) {
					gl2.glBegin(GL2.GL_LINES);
					gl2.glVertex2f(k.x2, k.open);
					gl2.glVertex2f(k.x2, k.low);
					gl2.glEnd();
				}
			} else if (k.open > k.close) {
				// 阴线
				// gl2.glColor3f(0.0f, 1.0f, 0.0f);
				gl2.glColor3f(0.0f, 1.0f, 1.0f);
				gl2.glBegin(GL2.GL_LINES);
				gl2.glVertex2f(k.x2, k.high);
				gl2.glVertex2f(k.x2, k.low);
				gl2.glEnd();
				gl2.glBegin(GL2.GL_POLYGON);
				gl2.glVertex2f(k.x1, k.open);
				gl2.glVertex2f(k.x3, k.open);
				gl2.glVertex2f(k.x3, k.close);
				gl2.glVertex2f(k.x1, k.close);
				gl2.glEnd();
			} else {
				// 方便记录的趋势
				// if (k.red) {
				// gl2.glColor3f(0.7f, 0, 0);
				// } else {
				// gl2.glColor3f(0.0f, 0.7f, 0.0f);
				// }
				gl2.glColor3f(0.5f, 0.5f, 0.5f);
				gl2.glBegin(GL2.GL_LINES);
				gl2.glVertex2f(k.x1, k.close);
				gl2.glVertex2f(k.x3, k.close);
				if (bar.getHigh() > bar.getLow()) {
					gl2.glVertex2f(k.x2, k.high);
					gl2.glVertex2f(k.x2, k.low);
				}
				gl2.glEnd();
			}
		}
		// gl2.glFlush();
	}

	/**
	 * K线对应矩阵
	 */
	private class K {
		// 颜色(true:红,false:绿)
		// private boolean red;
		// 3个横坐标(左中右)
		private float x1, x2, x3;
		// 4个纵坐标
		private float open, close, high, low;
	}
}
