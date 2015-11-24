package test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.finance.core.Bar;
import com.finance.core.BarCycle;
import com.finance.core.MD;
import com.finance.ui.GLBackground;
import com.finance.ui.GLBarPlot;
import com.finance.ui.GLCJLPlot;
import com.finance.ui.GLDisplay;
import com.finance.ui.GLMACDPlot;
import com.finance.ui.GLToolTip;
import com.finance.util.BufferUtil;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author Chen Lin 2015-10-28
 */
public class Test {

	public static void main(String[] args) {
		// Display display = Display.getDefault();
		// GLPlot plot = new GLPlot(display);
		// plot.start();
		// while (!display.isDisposed()) {
		// if (display.readAndDispatch()) {
		// display.sleep();
		// }
		// }

		Display display = Display.getDefault();
		final GLDisplay gl = new GLDisplay(display);
		gl.addGLEventListener(new GLBackground(gl));
		gl.addGLEventListener(new GLBarPlot(gl));
		gl.addGLEventListener(new GLCJLPlot(gl));
		gl.addGLEventListener(new GLMACDPlot(gl));
		GLToolTip tip = new GLToolTip(gl);
		long start = System.currentTimeMillis();
		// final List<Bar> bars = BarReadTest.parse(new File("IF0002.txt"), 60 *
		// 1000);
//		final List<Bar> bars = BufferUtil.read(new File("IF0002.dat"));
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		// gl.addAll(bars);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					for (int i = 0; i < bars.size(); i++) {
//						Bar bar = bars.get(i);
//						gl.add(bar);
//						Thread.sleep(1000);
//					}
//				} catch (InterruptedException e) {
//					System.out.println(e);
//				}
//			}
//		}).start();
		gl.addBarListener(new BarCycle("IF0000", 60000));
		new Thread(new Runnable() {

			@Override
			public void run() {
				DataInputStream dis = null;
				try {
					dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("E:/data/IF0000.dat"))));
					byte[] data = new byte[BufferUtil.MD_BUFFER_NUM];
					while (dis.read(data) != -1) {
						final MD md = BufferUtil.bytes2MD(data);
						md.setCode("IF0000");
						gl.add(md);
						Thread.sleep(5);
					}
					dis.close();
				} catch (Exception e) {
					System.out.println(e);
					throw new RuntimeException(e);
				}
			}
		}).start();
		gl.addGLEventListener(tip);
		gl.addKeyListener(tip);
		gl.addMouseListener(tip);
		gl.addMouseMoveListener(tip);
		gl.start();
		while (!display.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}

		// GLUT glut = new GLUT();
		// int len = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, "39000");
		// int len = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, " 09:47:48
		// 1420.0 2 0 ");
		// System.out.println(len);
	}
}
