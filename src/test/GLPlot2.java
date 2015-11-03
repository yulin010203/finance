package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.finance.core.Bar;
import com.finance.core.MD;
import com.finance.core.Pointf;
import com.finance.ui.bean.GLBar;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * @author 陈霖 2015-8-31
 */
public class GLPlot2 implements GLEventListener, MouseListener, MouseMoveListener, KeyListener {

	/**
	 * 默认展示K线条数
	 */
	private int count = 100;
	/**
	 * 窗口
	 */
	private Shell shell;
	/**
	 * 画布
	 */
	private GLCanvas canvas;
	/**
	 * 刷新线程
	 */
	private FPSAnimator animator;
	/**
	 * 
	 */
//	private GLBackground background = new GLBackground();
	/**
	 * 未完成的K线数据
	 */
	private Bar bar;
	/**
	 * 完成的K线数据
	 */
	private List<Bar> bars = new ArrayList<Bar>();
	/**
	 * K线画图数据
	 */
	private List<GLBar> glbars = new ArrayList<GLBar>();
	/**
	 * K线提示框
	 */
	private GLBarTip bartip = new GLBarTip();
	/**
	 * 对应K线位置
	 */
	private int index;
	/**
	 * plot中K线开始位置
	 */
	private int head;
	/**
	 * plot中K线结束位置
	 */
	private int tail;

	/**
	 * @param display
	 */
	public GLPlot2(Display display) {
		shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = GLCanvas.create(shell, SWT.NONE, capabilities, null);
		animator = new FPSAnimator(canvas, 30);

		addListener();
	}

	/**
	 * add listener
	 */
	private void addListener() {
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMoveListener(this);
		canvas.addKeyListener(this);
		shell.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					stop();
				}
			}
		});
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = false;
				stop();
			}
		});
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClearColor(0.1f, 0.1f, 0f, 0.1f); // Black Background
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		animator.stop();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		// 清除画面
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		// 刷新K线
		for (int i = head; i < tail; i++) {
			glbars.get(i).refresh(gl2);
		}
		if (!bars.isEmpty()) {
			bartip.refresh(gl2, bars.get(index));
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	/**
	 * @param bar
	 */
	public void add(Bar bar) {
		if (bar.isFinished()) {
			bars.add(bar);
		} else {

		}
	}

	/**
	 * @param bars
	 */
	public void addAll(Collection<Bar> bars) {
		this.bars.addAll(bars);
	}

	/**
	 * @param md
	 */
	public void add(MD md) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.keyCode) {
		case SWT.ARROW_UP:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}
			break;
		case SWT.ARROW_DOWN:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}

			break;
		case SWT.ARROW_LEFT:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}

			break;
		case SWT.ARROW_RIGHT:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

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
		bartip.setVisible(!bartip.isVisible());
		if (bartip.isVisible()) {
			bartip.setPoint(toPointf(e.x, e.y));
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
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private Pointf toPointf(int x, int y) {
		return new Pointf(x, y);
	}

	/**
	 * start
	 */
	public void start() {
		animator.start();
		shell.open();
	}

	/**
	 * stop
	 */
	public void stop() {
		animator.stop();
		canvas.dispose();
		shell.dispose();
		System.exit(0);
	}
}
