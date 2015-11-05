package test;

import java.io.File;
import java.util.List;

import com.finance.core.Bar;
import com.finance.util.BufferUtil;
import com.finance.util.TimeUtil;

public class T {

	public static void main(String[] args) {
		 List<Bar> bars = BarReadTest.parse(new File("IF0002.txt"), 60 * 1000);
		 BufferUtil.write(new File("IF0002.dat"), bars);
//		List<Bar> bars = BufferUtil.read(new File("IF0000.dat"));
//		for (Bar bar : bars) {
//			System.out.println(bar2Str(bar));
//		}
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
