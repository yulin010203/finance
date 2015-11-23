package test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finance.core.Bar;
import com.finance.core.BarComputer;
import com.finance.core.BarCycle;
import com.finance.core.MD;
import com.finance.listener.BarListener;
import com.finance.util.BufferUtil;

public class T {
	private static final Log log = LogFactory.getLog(T.class);
	private static BarComputer computer = new BarComputer();

	public static void main(String[] args) {
		// List<Bar> bars = BarReadTest.parse(new File("IF0002.txt"), 60 * 1000);
		// BufferUtil.write(new File("IF0002.dat"), bars);
		// List<Bar> bars = BufferUtil.read(new File("IF0000.dat"));
		// for (Bar bar : bars) {
		// System.out.println(bar2Str(bar));
		// }
		BarListener listener = new BarListener() {
			public void onBar(Bar bar) {
				log.info(BufferUtil.bar2Str(bar));
			}

			@Override
			public void onBarInside(Bar bar) {
			}
		};
		computer.addBarListener(new BarCycle("IF1311", 60 * 1000), listener);
		new Thread(new Runnable() {

			@Override
			public void run() {
				DataInputStream dis = null;
				try {
					dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("E:/data/data/20131104#IF1311.dat"))));
					byte[] data = new byte[BufferUtil.MD_BUFFER_NUM];
					while (dis.read(data) != -1) {
						final MD md = BufferUtil.bytes2MD(data);
						computer.update(md);
					}
					dis.close();
				} catch (Exception e) {
				}
			}
		}).start();
		// final List<Bar> bars = BarReadTest.parse(new File("IF0002.txt"), 60 * 1000);
		// for (int i = 0; i < 270; i++){
		// log.info(BufferUtil.bar2Str(bars.get(i)));
		// }
	}

	private static String bar2Str(Bar bar) {
		StringBuffer sb = new StringBuffer();
		sb.append("Bar[code=").append(bar.getCode()).append(",");
		sb.append("time=").append(bar.date2Str()).append(",");
		sb.append("open=").append(bar.getOpen()).append(",");
		sb.append("close=").append(bar.getClose()).append(",");
		sb.append("high=").append(bar.getHigh()).append(",");
		sb.append("low=").append(bar.getLow()).append(",");
		sb.append("deal=").append(bar.getDealVol()).append(",");
		sb.append("vol=").append(bar.getVol()).append("]");
		return sb.toString();
	}
}
