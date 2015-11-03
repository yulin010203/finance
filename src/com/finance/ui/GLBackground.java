package com.finance.ui;

import java.util.List;

import com.finance.core.Bar;
import com.finance.core.Bound;
import com.finance.core.Constants;
import com.finance.util.MathUtil;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * 背景图(按左正解(0,0)起始)
 * 
 * @author 陈霖 2015-9-19
 */
public class GLBackground extends PlotBase implements GLEventListener {

	/**
	 * 是否图区大小初始化
	 */
	private Bound barBound;
	private Bound dealBound;
	private Bound macdBound;
	private Bound detailBound;
	/**
	 * 主图显示
	 */
	private GLDisplay display;
	/**
	 * 字符串显示工具
	 */
	private GLUT glut = new GLUT();

	/**
	 * @param display
	 */
	public GLBackground(GLDisplay display) {
		this.display = display;
		this.barBound = display.getBarBound();
		this.dealBound = display.getDealBound();
		this.macdBound = display.getMacdBound();
		this.detailBound = display.getDetailBound();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// gl.glClearColor(0.1f, 0.1f, 0f, 0.1f);
		gl.glClearColor(0.0f, 0.0f, 0f, 0.0f);
		// 初始化区域
		init(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		// 画区域分割线
		drawPart(gl, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// gl.glClearColor(0.1f, 0.1f, 0f, 0.1f);
		gl.glClearColor(0.0f, 0.0f, 0f, 0.0f);
		// 画区域分割线
		drawPart(gl, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		drawX(gl);
		drawY(gl);

		// K线图区域
		gl.glViewport(barBound.getX(), barBound.getY(), barBound.getWidth(), barBound.getHeight());
		drawLineStipple(gl, 3);

		// 成交量图区域
		gl.glViewport(dealBound.getX(), dealBound.getY(), dealBound.getWidth(), dealBound.getHeight());
		drawLineStipple(gl, 1);

		// MACD图区域
		gl.glViewport(macdBound.getX(), macdBound.getY(), macdBound.getWidth(), macdBound.getHeight());
		drawLineStipple(gl, 1);

		// MACD图区域
		gl.glViewport(detailBound.getX(), detailBound.getY(), detailBound.getWidth(), detailBound.getHeight());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		init(width, height);
	}

	/**
	 * 画X轴
	 * 
	 * @param gl
	 */
	private void drawX(GL2 gl) {
		float y = toxf(2 * Constants.SHOW_VALUE_SPAN);
		float x1, x2, x3, x4, x5;
		int del = barBound.getWidth() / 6;
		int x = Constants.LFET_SPAN + del;
		drawline(gl, x);
		x1 = toxf(x - Constants.DATE_VALUE_WIDTH / 2);
		x += del;
		x2 = toxf(x - Constants.DATE_VALUE_WIDTH / 2);
		drawline(gl, x);
		x += del;
		x3 = toxf(x - Constants.DATE_VALUE_WIDTH / 2);
		drawline(gl, x);
		x += del;
		x4 = toxf(x - Constants.DATE_VALUE_WIDTH / 2);
		drawline(gl, x);
		x += del;
		x5 = toxf(x - Constants.DATE_VALUE_WIDTH / 2);
		drawline(gl, x);

		int dcount = display.getCount() / 6;
		List<Bar> bars = display.getBars();
		int index = display.getHead() + dcount;
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glRasterPos2f(x1, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, bars.get(index).time2Str());
		gl.glRasterPos2f(x2, y);
		index += dcount;
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, bars.get(index).time2Str());
		gl.glRasterPos2f(x3, y);
		index += dcount;
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, bars.get(index).time2Str());
		gl.glRasterPos2f(x4, y);
		index += dcount;
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, bars.get(index).time2Str());
		gl.glRasterPos2f(x5, y);
		index += dcount;
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, bars.get(index).time2Str());
	}

	/**
	 * @param gl
	 * @param x
	 */
	private void drawline(GL2 gl, int x) {
		float xf = toxf(x);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0.6f, 0, 0);
		gl.glVertex2f(xf, toyf(Constants.DOWN_SPAN));
		gl.glVertex2f(xf, toyf(Constants.DOWN_SPAN - Constants.SHOW_VALUE_SPAN));
		gl.glEnd();
		// gl.glLineStipple(1, (short) 3855);
		// gl.glEnable(GL2.GL_LINE_STIPPLE);
		// gl.glBegin(GL2.GL_LINES);
		// gl.glColor3f(0.4f, 0.4f, 0.4f);
		// gl.glVertex2f(xf, toyf(barBound.getY()));
		// gl.glVertex2f(xf, toyf(height - Constants.UP_SPAN));
		// gl.glDisable(GL2.GL_LINE_STIPPLE);
		// gl.glEnd();
	}

	/**
	 * 画Y轴
	 * 
	 * @param gl
	 */
	private void drawY(GL2 gl) {
		float x1 = toxf(Constants.BAR_LEFT_SPAN);
		float y1, y2, y3, y4, y5;
		int del = barBound.getHeight() / 4;
		int y = barBound.getY() + del - Constants.SHOW_VALUE_SPAN;
		y1 = toyf(y);
		y += del;
		y2 = toyf(y);
		y += del;
		y3 = toyf(y);
		y4 = toyf(dealBound.getY() + dealBound.getHeight() / 2 - Constants.SHOW_VALUE_SPAN);
		y5 = toyf(Constants.DOWN_SPAN + macdBound.getHeight() / 2 - Constants.SHOW_VALUE_SPAN);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glRasterPos2f(x1, y1);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Double.toString(MathUtil.round(getYValue(-0.5), 1)));
		gl.glRasterPos2f(x1, y2);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Double.toString(MathUtil.round(display.getMid(), 1)));
		gl.glRasterPos2f(x1, y3);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Double.toString(MathUtil.round(getYValue(0.5), 1)));
		gl.glRasterPos2f(x1, y4);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Integer.toString(display.getDeal() / 2));
		gl.glRasterPos2f(x1, y5);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "0.0");

	}

	/**
	 * @param y
	 * @return
	 */
	private double getYValue(double y) {
		return display.getMid() + y * display.getDel() * 0.5;
	}

	/**
	 * 画区域分割线
	 * 
	 * @param gl
	 * @param width
	 * @param height
	 */
	private void drawPart(GL2 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glColor3f(0.6f, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2f(-1.0f, toyf(height - Constants.UP_SPAN));
		gl.glVertex2f(toxf(width - Constants.RIGHT_SPAN), toyf(height - Constants.UP_SPAN));

		gl.glVertex2f(toxf(barBound.getX()), toyf(barBound.getY()));
		gl.glVertex2f(toxf(width - Constants.RIGHT_SPAN), toyf(barBound.getY()));

		gl.glVertex2f(toxf(dealBound.getX()), toyf(dealBound.getY()));
		gl.glVertex2f(toxf(width - Constants.RIGHT_SPAN), toyf(dealBound.getY()));

		gl.glVertex2f(toxf(macdBound.getX()), toyf(macdBound.getY()));
		gl.glVertex2f(1.0f, toyf(macdBound.getY()));

		gl.glVertex2f(toxf(Constants.LFET_SPAN), toyf(height - Constants.UP_SPAN));
		gl.glVertex2f(toxf(Constants.LFET_SPAN), toyf(Constants.DOWN_SPAN));

		gl.glVertex2f(toxf(detailBound.getX()), 1.0f);
		gl.glVertex2f(toxf(detailBound.getX()), toyf(detailBound.getY()));
		gl.glEnd();
	}

	/**
	 * 画虚线
	 * 
	 * @param gl
	 * @param num
	 */
	private void drawLineStipple(GL2 gl, int num) {
		float del = 2.0f / (num + 1);
		gl.glLineStipple(1, (short) 3855);
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glColor3f(0.6f, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		for (int i = 1; i <= num; i++) {
			float y = -1.0f + del * i;
			gl.glVertex2f(-1.0f, y);
			gl.glVertex2f(1.0f, y);
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_STIPPLE);
	}

	/**
	 * 初始化图区
	 * 
	 * @param width
	 * @param height
	 */
	protected void init(int width, int height) {
		super.init(width, height);
		// MACD图区域
		macdBound.setX(Constants.LFET_SPAN);
		macdBound.setY(Constants.DOWN_SPAN);
		macdBound.setWidth(width - Constants.LFET_SPAN - Constants.RIGHT_SPAN);
		macdBound.setHeight((height - Constants.UP_SPAN - Constants.DOWN_SPAN) / 6);
		macdBound.setDelw(1.0f / ((macdBound.getWidth() - 1.0f) * 0.5f));
		macdBound.setDelh(1.0f / ((macdBound.getHeight() - 1.0f) * 0.5f));

		// 成交量图区域
		dealBound.setWidth(macdBound.getWidth());
		dealBound.setHeight(macdBound.getHeight());
		dealBound.setX(Constants.LFET_SPAN);
		dealBound.setY(Constants.DOWN_SPAN + macdBound.getHeight());
		dealBound.setDelw(1.0f / ((dealBound.getWidth() - 1.0f) * 0.5f));
		dealBound.setDelh(1.0f / ((dealBound.getHeight() - 1.0f) * 0.5f));

		// K线图区域
		barBound.setWidth(macdBound.getWidth());
		barBound.setHeight(macdBound.getHeight() * 4);
		barBound.setX(Constants.LFET_SPAN);
		barBound.setY(Constants.DOWN_SPAN + macdBound.getHeight() + dealBound.getHeight());
		barBound.setDelw(1.0f / ((barBound.getWidth() - 1.0f) * 0.5f));
		barBound.setDelh(1.0f / ((barBound.getHeight() - 1.0f) * 0.5f));

		// MACD图区域
		detailBound.setWidth(Constants.RIGHT_SPAN);
		detailBound.setHeight(height);
		detailBound.setX(Constants.LFET_SPAN + barBound.getWidth());
		detailBound.setY(Constants.DOWN_SPAN);
		detailBound.setDelw(1.0f / ((detailBound.getWidth() - 1.0f) * 0.5f));
		detailBound.setDelh(1.0f / ((detailBound.getHeight() - 1.0f) * 0.5f));

		display.refresh();
	}

}
