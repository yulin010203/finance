package com.finance.ui;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finance.core.Bound;
import com.finance.core.indicator.MA;
import com.finance.ui.bean.GLBar;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/**
 * K线图
 * 
 * @author Chen Lin 2015-10-28
 */
public class GLBarPlot implements GLEventListener {
	private static final Log log = LogFactory.getLog(GLBarPlot.class);

	/**
	 * 画布
	 */
	private GLDisplay display;
	/**
	 * 
	 */
	private Bound barBound;
	/**
	 * K线画图数据
	 */
	private List<GLBar> glbars;
	/**
	 * MA移动平均线
	 */
	private Map<Integer, List<MA>> mas;
	/**
	 * MA均线颜色
	 */
	private Map<Integer, float[]> colors;

	/**
	 * @param display
	 */
	public GLBarPlot(GLDisplay display) {
		this.display = display;
		this.glbars = display.getGlbars();
		this.mas = display.getMas();
		this.colors = display.getColors();
		this.barBound = display.getBarBound();
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
		// K线图区域
		gl.glViewport(barBound.x, barBound.y, barBound.width, barBound.height);
		if (glbars.isEmpty()) {
			return;
		}
		// 刷新K线
		int head = display.getHead();
		int tail = display.getTail();
		for (int i = head; i <= tail; i++) {
			GLBar bar = glbars.get(i);
			if (bar == null) {
				continue;
			}
			bar.refresh(gl);
		}
		// if (display.isShowMA()) {
		refresh(gl, head, tail);
		// }
		gl.glFlush();
	}

	/**
	 * @param gl
	 * @param head
	 * @param tail
	 */
	private void refresh(GL2 gl, int head, int tail) {
		for (int n : mas.keySet()) {
			List<MA> list = mas.get(n);
			float[] color = colors.get(n);
			gl.glColor3f(color[0], color[1], color[2]);
			gl.glBegin(GL2.GL_LINE_STRIP);
			for (int i = head; i <= tail; i++) {
				MA ma = list.get(i);
				if (ma == null) {
					continue;
				}
				float[] v = ma.getMaf();
				gl.glVertex2f(v[0], v[1]);
			}
			gl.glEnd();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

}
