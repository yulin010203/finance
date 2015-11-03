package com.finance.ui;

import java.util.List;

import com.finance.core.Bound;
import com.finance.core.indicator.MACD;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/**
 * MACD图
 * 
 * @author Chen Lin 2015-10-28
 */
public class GLMACDPlot implements GLEventListener {

	/**
	 * 主图显示
	 */
	private GLDisplay display;
	/**
	 * MACD指标
	 */
	private List<MACD> macds;
	/**
	 * MACD图区
	 */
	private Bound macdBound;

	/**
	 * @param display
	 */
	public GLMACDPlot(GLDisplay display) {
		this.display = display;
		this.macds = display.getMacds();
		this.macdBound = display.getMacdBound();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// 成交量图区域
		gl.glViewport(macdBound.getX(), macdBound.getY(), macdBound.getWidth(), macdBound.getHeight());
		if (macds.isEmpty()) {
			return;
		}
		// 成交量
		int head = display.getHead();
		int tail = display.getTail();
		refresDif(gl, head, tail);
		refresDea(gl, head, tail);
		refresDel(gl, head, tail);
		gl.glFlush();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	/**
	 * @param gl
	 * @param head
	 * @param tail
	 */
	private void refresDif(GL2 gl, int head, int tail) {
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = head; i <= tail; i++) {
			float[] v = macds.get(i).getDiff();
			gl.glVertex2f(v[0], v[1]);
		}
		gl.glEnd();
	}

	/**
	 * @param gl
	 * @param head
	 * @param tail
	 */
	private void refresDea(GL2 gl, int head, int tail) {
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = head; i <= tail; i++) {
			float[] v = macds.get(i).getDeaf();
			gl.glVertex2f(v[0], v[1]);
		}
		gl.glEnd();
	}

	/**
	 * @param gl
	 * @param head
	 * @param tail
	 */
	private void refresDel(GL2 gl, int head, int tail) {
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_LINES);
		for (int i = head; i <= tail; i++) {
			float[] v = macds.get(i).getDelf();
			if (v[1] >= 0) {
				gl.glColor3f(0.7f, 0, 0);
			} else {
				// gl.glColor3f(0.0f, 1.0f, 0.0f);
				gl.glColor3f(0.0f, 1.0f, 1.0f);
			}
			gl.glVertex2f(v[0], 0.0f);
			gl.glVertex2f(v[0], v[1]);
		}
		gl.glEnd();
	}

}
