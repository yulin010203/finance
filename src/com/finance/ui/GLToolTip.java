package com.finance.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

import com.finance.core.Bar;
import com.finance.core.Bound;
import com.finance.core.Constants;
import com.finance.core.indicator.MA;
import com.finance.core.indicator.MACD;
import com.finance.ui.bean.GLBar;
import com.finance.ui.bean.GLCJL;
import com.finance.util.MathUtil;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author Chen Lin 2015-10-30
 */
public class GLToolTip extends PlotBase implements GLEventListener, KeyListener, MouseListener, MouseMoveListener {
	private static final Log log = LogFactory.getLog(GLToolTip.class);
	/**
	 * K线图区域
	 */
	private Bound barBound = new Bound();
	/**
	 * 成交量图区域
	 */
	private Bound dealBound = new Bound();
	/**
	 * MACD图区域
	 */
	private Bound macdBound = new Bound();
	/**
	 * 明细图区域
	 */
	private Bound detailBound = new Bound();
	/**
	 * 主图显示
	 */
	private GLDisplay display;
	/**
	 * 显示提示框
	 */
	private boolean draw;
	/**
	 * 鼠标位置的K线
	 */
	private Bar bar;
	/**
	 * K线数据
	 */
	private List<Bar> bars;
	/**
	 * K线画图数据
	 */
	private List<GLBar> glbars;
	/**
	 * MA移动平均线
	 */
	private Map<Integer, List<MA>> mas;
	/**
	 * MA移动平均线颜色
	 */
	private Map<Integer, float[]> colors;
	/**
	 * 成交量画图数据
	 */
	private List<GLCJL> glcjls;
	/**
	 * MACD指标
	 */
	private List<MACD> macds;
	/**
	 * 鼠标位置
	 */
	private Point point;
	/**
	 * K线位置
	 */
	private int index = 0;
	/**
	 * 字符串显示工具
	 */
	private GLUT glut = new GLUT();

	/**
	 * @param display
	 */
	public GLToolTip(GLDisplay display) {
		this.display = display;
		this.bars = display.getBars();
		this.glbars = display.getGlbars();
		this.mas = display.getMas();
		this.colors = display.getColors();
		this.glcjls = display.getGlcjls();
		this.macds = display.getMacds();
		this.barBound = display.getBarBound();
		this.dealBound = display.getDealBound();
		this.macdBound = display.getMacdBound();
		this.detailBound = display.getDetailBound();
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
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		if (draw) {
			drawLine(gl);
			drawDate(gl);
			drawSquare(gl);
			gl.glFlush();
		}
		drawBarMessage(gl);
		drawCJLMessage(gl);
		drawMACDMessage(gl);
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.keyCode) {
		case SWT.ARROW_UP:
			// 单按上键
			if (draw && e.stateMask != SWT.CTRL) {
				Event event = createEvent(point.x, height - point.y, e);
				mouseMove(new MouseEvent(event));
			}
			break;
		case SWT.ARROW_DOWN:
			// 单按下键
			if (draw && e.stateMask != SWT.CTRL) {
				Event event = createEvent(point.x, height - point.y, e);
				mouseMove(new MouseEvent(event));
			}
			break;
		case SWT.ARROW_LEFT:
			// 单按左键
			if (draw && e.stateMask != SWT.CTRL) {
				try {
					if (this.index == display.getHead()) {
						if (display.getHead() <= 0) {
							log.info("已经到最左边，不进行右翻页:" + index);
							break;
						}
						log.info("当前十字线在最左边，进行左翻页:" + index);
						display.left(e);
						this.index = display.getTail() + 1;
					}
					GLBar glBar = glbars.get(this.index - 1);
					if (glBar == null) {
						break;
					}
					float[] v = glBar.getClosef();
					Point p = display.getLocation();
					int x = (int) ((v[0] + 1.0f) / barBound.delw + Constants.LFET_SPAN + p.x) - 1;
					int y = (int) (barBound.height - (v[1] + 1.0f) / barBound.delh + Constants.UP_SPAN + p.y) + 2;
					new Robot().mouseMove(x, y);
				} catch (AWTException e1) {
				}
			}
			break;
		case SWT.ARROW_RIGHT:
			// 单按右键
			if (draw && e.stateMask != SWT.CTRL) {
				try {
					if (this.index == display.getTail()) {
						if (display.getTail() >= bars.size() - 1) {
							log.info("已经到最右边，不进行右翻页:" + index);
							break;
						}
						log.info("当前十字线在最右边，进行右翻页:" + index);
						display.right(e);
						this.index = display.getHead() - 1;
					}
					GLBar glBar = glbars.get(this.index + 1);
					if (glBar == null) {
						break;
					}
					float[] v = glBar.getClosef();
					Point p = display.getLocation();
					int x = (int) ((v[0] + 1.0f) / barBound.delw + Constants.LFET_SPAN + p.x);
					int y = (int) (barBound.height - (v[1] + 1.0f) / barBound.delh + Constants.UP_SPAN + p.y) + 2;
					new Robot().mouseMove(x, y);
				} catch (AWTException e1) {
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		draw = !draw;
		if (draw && !bars.isEmpty()) {
			// 转化为左下角起始坐标(0,0)
			int len = bars.size() - 1;
			point = new Point(e.x, height - e.y);
			int index = display.getHead() + (int) ((point.x - Constants.LFET_SPAN) / display.getSpan());
			this.index = index > len ? len : index;
			this.bar = bars.get(this.index);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (bars.isEmpty()) {
			return;
		}
		// 此时还未转变为左下角原点
		if (e.x < barBound.x || e.x > detailBound.x || e.y < Constants.UP_SPAN || height - e.y <= Constants.DOWN_SPAN) {
			return;
		}
		int len = bars.size() - 1;
		if (!draw) {
			this.index = len;
			return;
		}
		// 转化为左下角起始坐标(0,0)
		point = new Point(e.x, height - e.y);
		int index = display.getHead() + (int) ((point.x - Constants.LFET_SPAN) / display.getSpan());
		this.index = index > len ? len : index;
		this.bar = bars.get(this.index);
	}

	/**
	 * 创造事件
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Event createEvent(int x, int y, KeyEvent e) {
		Event event = new Event();
		event.widget = e.widget;
		event.display = e.display;
		event.data = e.data;
		event.type = SWT.MouseMove;
		event.x = x;
		event.y = y;
		return event;
	}

	/**
	 * 画提示框
	 * 
	 * @param gl
	 */
	private void drawSquare(GL2 gl) {
		if (point != null && bar != null) {

			int x1, x2, y1, y2;
			x1 = point.x + Constants.TIP_BAR_VALUE_SPAN;
			x2 = point.x + Constants.TIP_BAR_VALUE_WIDTH + Constants.TIP_BAR_VALUE_SPAN;
			// x轴超出界限
			if (x2 > detailBound.x) {
				x1 = point.x - Constants.TIP_BAR_VALUE_WIDTH - Constants.TIP_BAR_VALUE_SPAN;
				x2 = point.x - Constants.TIP_BAR_VALUE_SPAN;
			}
			y1 = point.y - Constants.TIP_BAR_VALUE_SPAN;
			y2 = point.y - Constants.TIP_BAR_VALUE_HEIGHT - Constants.TIP_BAR_VALUE_SPAN;
			// y轴超出界限
			if (y2 < Constants.DOWN_SPAN) {
				y1 = point.y + Constants.TIP_BAR_VALUE_HEIGHT + Constants.TIP_BAR_VALUE_SPAN;
				y2 = point.y + Constants.TIP_BAR_VALUE_SPAN;
			}
			float xf1, xf2, yf1, yf2;
			xf1 = toxf(x1);
			xf2 = toxf(x2);
			yf1 = toyf(y1);
			yf2 = toyf(y2);

			// 实体矩形框
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glVertex2f(xf1, yf1);
			gl.glVertex2f(xf2, yf1);
			gl.glVertex2f(xf2, yf2);
			gl.glVertex2f(xf1, yf2);
			gl.glEnd();
			// 边界线
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3f(0.5f, 0.5f, 0.5f);
			gl.glVertex2f(xf1, yf1);
			gl.glVertex2f(xf2, yf1);
			gl.glVertex2f(xf2, yf2);
			gl.glVertex2f(xf1, yf2);
			gl.glVertex2f(xf1, yf1);
			gl.glEnd();

			if (bar.getOpen() > bar.getClose()) {
				gl.glColor3f(0.0f, 1.0f, 0.0f);
			} else if (bar.getOpen() < bar.getClose()) {
				gl.glColor3f(0.7f, 0, 0);
			} else {
				gl.glColor3f(0.5f, 0.5f, 0.5f);
			}
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "open = " + bar.getOpen());
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glColor3f(0.7f, 0, 0);
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "high = " + bar.getHigh());
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "low = " + bar.getLow());
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			if (bar.getOpen() > bar.getClose()) {
				gl.glColor3f(0.0f, 1.0f, 0.0f);
			} else if (bar.getOpen() < bar.getClose()) {
				gl.glColor3f(0.7f, 0, 0);
			} else {
				gl.glColor3f(0.5f, 0.5f, 0.5f);
			}
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "close = " + bar.getClose());
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "change = " + MathUtil.round(bar.getPriceChange(), 2));
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "rate = " + MathUtil.round(bar.getPriceChangeRate(), 2) + "%");
			gl.glColor3f(1.0f, 1.0f, 0.0f);
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "dealVol = " + bar.getDealVol());
			y1 -= 4 * Constants.SHOW_VALUE_SPAN;
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(y1));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "vol = " + bar.getVol());
			// gl.glFlush();
		}
	}

	/**
	 * 画十字线
	 * 
	 * @param gl
	 */
	private void drawLine(GL2 gl) {
		if (point != null && bar != null) {

			gl.glBegin(GL2.GL_LINES);
			gl.glColor3f(0.5f, 0.5f, 0.5f);
			// 横线
			gl.glVertex2f(toxf(Constants.LFET_SPAN), toyf(point.y));
			gl.glVertex2f(toxf(detailBound.x), toyf(point.y));
			// 竖线
			gl.glVertex2f(toxf(point.x), toyf(height - Constants.UP_SPAN));
			gl.glVertex2f(toxf(point.x), -1.0f);
			gl.glEnd();

			// gl.glFlush();
		}
	}

	/**
	 * 画时间
	 * 
	 * @param gl
	 */
	private void drawDate(GL2 gl) {
		if (point != null && bar != null) {
			int x1, x2, y1, y2;
			x1 = point.x;
			x2 = x1 + Constants.TIP_DATE_WIDTH;
			// x轴超出界限
			if (x2 > detailBound.x) {
				x2 = detailBound.x;
				x1 = x2 - Constants.TIP_DATE_WIDTH;
			}
			y1 = point.y;
			y2 = y1 + Constants.TIP_YVALUE_HEIGHT;
			// y轴超出界限
			if (y2 > height - Constants.UP_SPAN) {
				y2 = height - Constants.UP_SPAN;
				y1 = y2 - Constants.TIP_YVALUE_HEIGHT;
			}
			float xf11, xf12, yf11, yf12, xf21, xf22, yf21, yf22;
			xf11 = toxf(x1);
			xf12 = toxf(x2);
			yf11 = toyf(Constants.TIP_DATE_HEIGHT);
			yf12 = -1.0f;

			xf21 = toxf(detailBound.x - Constants.TIP_YVALUE_WIDTH);
			xf22 = toxf(detailBound.x);
			yf21 = toyf(y2);
			yf22 = toyf(y1);

			// 横轴时间提示框
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glVertex2f(xf11, yf11);
			gl.glVertex2f(xf12, yf11);
			gl.glVertex2f(xf12, yf12);
			gl.glVertex2f(xf11, yf12);
			gl.glEnd();
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3f(0.5f, 0.5f, 0.5f);
			gl.glVertex2f(xf11, yf11);
			gl.glVertex2f(xf12, yf11);
			gl.glVertex2f(xf12, yf12);
			gl.glVertex2f(xf11, yf12);
			gl.glVertex2f(xf11, yf11);
			gl.glEnd();

			// 纵轴Y值提示框
			gl.glBegin(GL2.GL_POLYGON);
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glVertex2f(xf21, yf21);
			gl.glVertex2f(xf22, yf21);
			gl.glVertex2f(xf22, yf22);
			gl.glVertex2f(xf21, yf22);
			gl.glEnd();
			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glColor3f(0.5f, 0.5f, 0.5f);
			gl.glVertex2f(xf21, yf21);
			gl.glVertex2f(xf22, yf21);
			gl.glVertex2f(xf22, yf22);
			gl.glVertex2f(xf21, yf22);
			gl.glVertex2f(xf21, yf21);
			gl.glEnd();

			gl.glColor3f(1.0f, 1.0f, 1.0f);
			float xf = toxf(detailBound.x - Constants.TIP_YVALUE_WIDTH + Constants.SHOW_VALUE_SPAN);
			if (point.y > barBound.y) {
				// 显示Y值
				gl.glRasterPos2f(xf, toyf(y1 + Constants.SHOW_VALUE_SPAN));
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Double.toString(MathUtil.round(getBarValue(point.y - barBound.y), 2)));
			} else if (point.y > dealBound.y) {
				gl.glRasterPos2f(xf, toyf(y1 + Constants.SHOW_VALUE_SPAN));
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Integer.toString(getCJLValue(point.y - dealBound.y)));
			} else if (point.y > Constants.DOWN_SPAN) {
				gl.glRasterPos2f(xf, toyf(y1 + Constants.SHOW_VALUE_SPAN));
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Double.toString(MathUtil.round(getMACDValue(point.y - Constants.DOWN_SPAN), 3)));
			}
			gl.glRasterPos2f(toxf(x1 + Constants.SHOW_VALUE_SPAN), toyf(Constants.SHOW_VALUE_SPAN));
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, bar.date2Str());
		}
	}

	/**
	 * 获取十字线对应的K线值
	 * 
	 * @param y
	 * @return y
	 */
	private double getBarValue(int y) {
		return display.getMid() + dealBound.toyf(y) * display.getDel() * 0.5;
	}

	/**
	 * 获取十字线对应的成交量
	 * 
	 * @param y
	 * @return y
	 */
	private int getCJLValue(int y) {
		return (int) (display.getDeal() * (dealBound.toyf(y) + 1.0f) * 0.5);
	}

	/**
	 * 获取十字线对应的macd值
	 * 
	 * @param y
	 * @return y
	 */
	private double getMACDValue(int y) {
		return display.getMacdMax() * macdBound.toyf(y);
	}

	/**
	 * K线图区域
	 * 
	 * @param gl
	 */
	private void drawBarMessage(GL2 gl) {
		if (bars.isEmpty() || mas.isEmpty()) {
			return;
		}
		gl.glViewport(barBound.x, barBound.y, barBound.width, barBound.height);
		StringBuffer sb = new StringBuffer();
		sb.append("MA(");
		for (int n : GLDisplay.MA_N) {
			sb.append(n).append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append(")");
		float y = barBound.toyf(barBound.height - 4 * Constants.SHOW_VALUE_SPAN);
		int x = Constants.SHOW_VALUE_SPAN;
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glRasterPos2f(barBound.toxf(Constants.SHOW_VALUE_SPAN), y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, sb.toString());
		int len = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, sb.toString());
		x += len + Constants.SHOW_VALUE_SPAN;
		for (int n : GLDisplay.MA_N) {
			float[] color = colors.get(n);
			gl.glColor3f(color[0], color[1], color[2]);
			String msg = "MA" + n + " ";
			List<MA> list = mas.get(n);
			if (list.isEmpty()) {
				msg += "0.00";
			} else {
				MA ma = list.get(index);
				if (ma == null) {
					msg += "0.00";
				} else {
					msg += MathUtil.round(ma.getMa(), 2);
				}
			}
			gl.glRasterPos2f(barBound.toxf(x), y);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, msg);
			x += glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, sb.toString());
		}

	}

	/**
	 * 成交量图区域
	 * 
	 * @param gl
	 */
	private void drawCJLMessage(GL2 gl) {
		if (bars.isEmpty() || glcjls.isEmpty()) {
			return;
		}
		gl.glViewport(dealBound.x, dealBound.y, dealBound.width, dealBound.height);
		float y = dealBound.toyf(dealBound.height - 4 * Constants.SHOW_VALUE_SPAN);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glRasterPos2f(dealBound.toxf(Constants.SHOW_VALUE_SPAN), y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "CJL ");
		GLCJL cjl = glcjls.get(index);
		String msg = " " + cjl.getDealVol() + "  OPID  " + cjl.getVol();
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glRasterPos2f(dealBound.toxf(7 * Constants.SHOW_VALUE_SPAN), y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, msg);
	}

	/**
	 * 成交量图区域
	 * 
	 * @param gl
	 */
	private void drawMACDMessage(GL2 gl) {
		if (bars.isEmpty() || macds.isEmpty()) {
			return;
		}
		gl.glViewport(macdBound.x, macdBound.y, macdBound.width, macdBound.height);
		float y = macdBound.toyf(macdBound.height - 4 * Constants.SHOW_VALUE_SPAN);
		MACD macd = macds.get(index);
		int[] n = macd.getN();
		String msg = "MACD(" + n[0] + "," + n[1] + "," + n[2] + ") ";
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glRasterPos2f(macdBound.toxf(Constants.SHOW_VALUE_SPAN), y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, msg);

		int x = Constants.SHOW_VALUE_SPAN + glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, msg);
		msg = "DIFF  " + MathUtil.round(macd.getDif(), 2) + "  ";
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glRasterPos2f(macdBound.toxf(x), y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, msg);

		x += glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, msg);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glRasterPos2f(macdBound.toxf(x), y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, msg);
	}

}
