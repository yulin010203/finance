package com.finance.ui;

import java.util.List;

import com.finance.core.Bound;
import com.finance.ui.bean.GLCJL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/**
 * 成交量图
 * 
 * @author Chen Lin 2015-10-28
 */
public class GLCJLPlot implements GLEventListener {
	/**
	 * 画布
	 */
	private GLDisplay display;
	/**
	 * 
	 */
	private Bound dealBound;
	/**
	 * K线画图数据
	 */
	private List<GLCJL> glcjls;

	/**
	 * @param display
	 */
	public GLCJLPlot(GLDisplay display) {
		this.display = display;
		this.glcjls = display.getGlcjls();
		this.dealBound = display.getDealBound();

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
		gl.glViewport(dealBound.x, dealBound.y, dealBound.width, dealBound.height);
		if (glcjls.isEmpty()) {
			return;
		}
		// 成交量
		int head = display.getHead();
		int tail = display.getTail();
		refresh(gl, head, tail);
		for (int i = head; i <= tail; i++) {
			glcjls.get(i).refresh(gl);
		}
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
	private void refresh(GL2 gl, int head, int tail) {
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = head; i <= tail; i++) {
			GLCJL cjl = glcjls.get(i);
			if (cjl == null) {
				continue;
			}
			float[] v = cjl.getVolf();
			gl.glVertex2f(v[0], v[1]);
		}
		gl.glEnd();
	}
}