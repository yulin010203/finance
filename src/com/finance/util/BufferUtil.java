package com.finance.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.finance.core.Bar;
import com.finance.core.MD;

/**
 * @author YuLin Nov 4, 2015
 */
public class BufferUtil {
	/**
	 * 字符编码
	 */
	private static final Charset charset = Charset.forName("UTF-8");
	/**
	 * K线数据二进制字节数
	 */
	public static final int BAR_BUFFER_NUM = 63;
	/**
	 * tick数据二进制字节数
	 */
	public static final int MD_BUFFER_NUM = 95;

	/**
	 * 字符串转为char*("\0"结束)
	 * 
	 * @param str
	 * @return char*
	 */
	public static byte[] str2c(String str) {
		if (str == null) {
			return null;
		}
		byte[] d = str.getBytes(charset);
		byte[] r = new byte[d.length + 1];
		System.arraycopy(d, 0, r, 0, d.length);
		return r;
	}

	/**
	 * char*转为字符串("\0"结束)
	 * 
	 * @param bytes
	 * @return string
	 */
	public static String c2str(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return c2str(bytes, 0, bytes.length);
	}

	/**
	 * char*转为字符串("\0"结束)
	 * 
	 * @param bytes
	 * @param index
	 * @param len
	 * @return string
	 */
	public static String c2str(byte[] bytes, int index, int len) {
		if (bytes == null || len == 0) {
			return null;
		}
		if (bytes[index] == 0) {
			return "";
		}
		int max = len;
		for (int i = 0; i < len; i++) {
			if (bytes[i + index] == 0) {
				max = i;
				break;
			}
		}
		return new String(bytes, index, max, charset);
	}

	/**
	 * 二进制数据转成K线
	 * 
	 * @param data
	 * @return bar
	 */
	public static Bar bytes2Bar(byte[] data) {
		// 时间|合约|周期|开盘价|收盘价|最高价|最低价|成交均价|成交量|持仓量
		if (data == null || data.length < BAR_BUFFER_NUM) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());
		Bar bar = new Bar();
		bar.setStartTime(buffer.getLong(0)); // 开始时间
		bar.setCode(c2str(data, 8, 7)); // 合约代码
		bar.setPeriod(buffer.getLong(15)); // K线周期
		bar.setEndTime(bar.getStartTime() + bar.getPeriod()); // 结束时间
		bar.setOpen(buffer.getDouble(23)); // 开盘
		bar.setClose(buffer.getDouble(31)); // 收盘
		bar.setHigh(buffer.getDouble(39)); // 最高
		bar.setLow(buffer.getDouble(47)); // 最低
		bar.setDealVol(buffer.getInt(55)); // 成交量
		bar.setVol(buffer.getInt(59)); // 持仓量
		bar.updatePriceChange(); // 更新价格波动
		bar.setStatus(2);
		return bar;
	}

	/**
	 * K线转成二进制数据
	 * 
	 * @param bar
	 * @return bytes
	 */
	public static byte[] bar2Bytes(Bar bar) {
		ByteBuffer buffer = ByteBuffer.allocate(BAR_BUFFER_NUM).order(ByteOrder.nativeOrder());
		buffer.putLong(0, bar.getStartTime()); // 开始时间
		buffer.position(8);
		buffer.put(str2c(bar.getCode()), 0, 7); // 合约代码
		buffer.putLong(15, bar.getPeriod()); // K线周期
		buffer.putDouble(23, bar.getOpen()); // 开盘
		buffer.putDouble(31, bar.getClose()); // 收盘
		buffer.putDouble(39, bar.getHigh()); // 最高
		buffer.putDouble(47, bar.getLow()); // 最低
		buffer.putInt(55, bar.getDealVol()); // 成交量
		buffer.putInt(59, bar.getVol()); // 持仓量
		return buffer.array();
	}

	/**
	 * K线转成字符流
	 * 
	 * @param bar
	 * @return string
	 */
	public static String bar2Str(Bar bar) {
		StringBuffer sb = new StringBuffer();
		sb.append("Bar[code=").append(bar.getCode()).append(",");
		sb.append("time=").append(bar.date2Str()).append(",");
		sb.append("open=").append(bar.getOpen()).append(",");
		sb.append("close=").append(bar.getClose()).append(",");
		sb.append("high=").append(bar.getHigh()).append(",");
		sb.append("low=").append(bar.getLow()).append(",");
		sb.append("dealVol=").append(bar.getDealVol()).append(",");
		sb.append("vol=").append(bar.getVol()).append("]");
		return sb.toString();
	}

	/**
	 * 从二进制文件读取K线
	 * 
	 * @param file
	 * @return bars
	 */
	public static List<Bar> read(File file) {
		if (file == null || !file.exists()) {
			return new ArrayList<Bar>();
		}
		List<Bar> bars = new ArrayList<Bar>();
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			byte[] data = new byte[BAR_BUFFER_NUM];
			while (dis.read(data) != -1) {
				Bar bar = bytes2Bar(data);
				bars.add(bar);
			}
		} catch (Throwable e) {
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
		}
		return bars;
	}

	/**
	 * 把K线数据写入二进制文件
	 * 
	 * @param file
	 * @param bars
	 */
	public static void write(File file, List<Bar> bars) {
		if (file == null || bars.isEmpty()) {
			return;
		}
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			for (Bar bar : bars) {
				dos.write(bar2Bytes(bar));
			}
		} catch (Throwable e) {
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 二进制数据转成MD
	 * 
	 * @param data
	 * @return MD
	 */
	public static MD bytes2MD(byte[] data) {
		// 时间|合约|最新|开盘|收盘|最高|最低|成交量|持仓量|买1价|买1量|卖1价|卖1量|成交金额
		if (data == null || data.length < MD_BUFFER_NUM) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());
		MD md = new MD();
		md.setTimeStamp(buffer.getLong(0)); // 开始时间
		md.setCode(c2str(data, 8, 7)); // 合约代码
		md.setPrice(buffer.getDouble(15)); // 最新
		md.setOpen(buffer.getDouble(23)); // 开盘
		md.setClose(buffer.getDouble(31)); // 收盘
		md.setHigh(buffer.getDouble(39)); // 最高
		md.setLow(buffer.getDouble(47)); // 最低
		md.setDealVol(buffer.getInt(55)); // 成交量
		md.setVol(buffer.getInt(59)); // 持仓量
		md.setBidPrice(buffer.getDouble(63)); // 买1价
		md.setBidVol(buffer.getInt(71)); // 买1量
		md.setAskPrice(buffer.getDouble(75)); // 卖1价
		md.setAskVol(buffer.getInt(83)); // 卖1量
		md.setTurnover(buffer.getDouble(87)); // 成交金额
		md.setPriceChange(md.getOpen() - md.getPrice());
		if (md.getOpen() != 0) {
			md.setPriceChangeRate(md.getPriceChange() / md.getOpen() * 100d);
		}
		if (md.getDealVol() != 0) {
			md.setAvgPrice(md.getTurnover() / md.getDealVol());
		}
		return md;
	}

	/**
	 * MD转成二进制数据
	 * 
	 * @param md
	 * @return bytes
	 */
	public static byte[] md2Bytes(MD md) {
		ByteBuffer buffer = ByteBuffer.allocate(MD_BUFFER_NUM).order(ByteOrder.nativeOrder());
		buffer.putLong(0, md.getTimeStamp()); // 开始时间
		buffer.position(8);
		buffer.put(str2c(md.getCode()), 0, 7); // 合约代码
		buffer.putDouble(15, md.getPrice()); // 最新
		buffer.putDouble(23, md.getOpen()); // 开盘
		buffer.putDouble(31, md.getClose()); // 收盘
		buffer.putDouble(39, md.getHigh()); // 最高
		buffer.putDouble(47, md.getLow()); // 最低
		buffer.putInt(55, md.getDealVol()); // 成交量
		buffer.putInt(59, md.getVol()); // 持仓量
		buffer.putDouble(63, md.getBidPrice()); // 买1价
		buffer.putInt(71, md.getBidVol()); // 买1量
		buffer.putDouble(75, md.getAskPrice()); // 卖1价
		buffer.putInt(83, md.getAskVol()); // 卖1量
		buffer.putDouble(87, md.getTurnover()); // 成交金额
		return buffer.array();
	}

	/**
	 * MD转成字符流
	 * 
	 * @param md
	 * @return string
	 */
	public static String md2Str(MD md) {
		StringBuffer sb = new StringBuffer();
		sb.append("MD[code=").append(md.getCode()).append(",");
		sb.append("time=").append(md.date2Str()).append(",");
		sb.append("price=").append(md.getPrice()).append(",");
		sb.append("open=").append(md.getOpen()).append(",");
		sb.append("close=").append(md.getClose()).append(",");
		sb.append("high=").append(md.getHigh()).append(",");
		sb.append("low=").append(md.getLow()).append(",");
		sb.append("dealVol=").append(md.getDealVol()).append(",");
		sb.append("vol=").append(md.getVol()).append(",");
		sb.append("bidPrice=").append(md.getBidPrice()).append(",");
		sb.append("bidVol=").append(md.getBidVol()).append(",");
		sb.append("askPrice=").append(md.getAskPrice()).append(",");
		sb.append("askVol=").append(md.getAskVol()).append(",");
		sb.append("turnover=").append(md.getTurnover()).append("]");
		return sb.toString();
	}
}
