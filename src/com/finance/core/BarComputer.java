package com.finance.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finance.listener.BarListener;
import com.finance.listener.MDListener;

/**
 * K线计算器
 * 
 * @author YuLin Nov 12, 2015
 */
public class BarComputer {
	private static final Log log = LogFactory.getLog(BarComputer.class);
	/**
	 * 行情监听器(key:code,value:listener)
	 */
	private Map<String, MDListener> mdlisteners = new ConcurrentHashMap<String, MDListener>();
	/**
	 * 合约与K线同期映射
	 */
	private Map<String, Set<BarCycle>> mdcache = new ConcurrentHashMap<String, Set<BarCycle>>();
	/**
	 * K线监听器(key:barCycle,value:listener)
	 */
	private Map<BarCycle, BarListener> barlisteners = new ConcurrentHashMap<BarCycle, BarListener>();
	/**
	 * K线生成上下文(key:barCycle,value:context)
	 */
	private Map<BarCycle, Context> contexts = new ConcurrentHashMap<BarCycle, BarComputer.Context>();
	/**
	 * K线缓存
	 */
	private List<Bar> bars = new ArrayList<Bar>();

	/**
	 * @param md
	 */
	public void update(final MD md) {
		if (md == null) {
			return;
		}
		Set<BarCycle> set = mdcache.get(md.getCode());
		if (set != null && !set.isEmpty()) {
			for (BarCycle cycle : set) {
				Context context = contexts.get(cycle);
				Bar bar = null;
				if (context == null) {
					context = new Context(cycle, md);
					bar = context.bar;
					contexts.put(cycle, context);
				} else {
					bar = context.upate(md);
				}
				if (bar != null) {
					notifyAllBarListeners(bar);
				}
			}
		}
	}

	/**
	 * @param code
	 * @param listener
	 */
	public void addMDListener(String code, MDListener listener) {
		if (code == null) {
			return;
		}
		mdlisteners.put(code, listener);
	}

	/**
	 * @param code
	 * @param listener
	 */
	public void remove(String code, MDListener listener) {
		if (code == null) {
			return;
		}
		mdlisteners.remove(code);
	}

	/**
	 * @param cycle
	 * @param listener
	 */
	public void addBarListener(BarCycle cycle, BarListener listener) {
		if (cycle == null) {
			return;
		}
		Set<BarCycle> set = mdcache.get(cycle.getCode());
		if (set == null) {
			set = new HashSet<BarCycle>();
			mdcache.put(cycle.getCode(), set);
		}
		set.add(cycle);
		barlisteners.put(cycle, listener);
	}

	/**
	 * @param cycle
	 * @param listener
	 */
	public void remove(BarCycle cycle, BarListener listener) {
		if (cycle == null) {
			return;
		}
		Set<BarCycle> set = mdcache.get(cycle.getCode());
		if (set != null && !set.isEmpty()) {
			set.remove(listener);
			if (set.isEmpty()) {
				mdcache.remove(cycle.getCode());
			}
		}
		barlisteners.remove(cycle);
	}

	/**
	 * @param bar
	 */
	public void notifyAllBarListeners(Bar bar) {
		BarCycle cycle = new BarCycle(bar.getCode(), bar.getPeriod());
		BarListener listener = barlisteners.get(cycle);
		if (listener != null) {
			if (bar.isEnd()) {
				listener.onBar(bar);
			} else {
				listener.onBarInside(bar);
			}
		}
	}

	/**
	 * K线生成上下文
	 */
	private class Context {
		// K线周期
		private BarCycle cycle;
		// 开始时间
		private long start;
		// 缓存K线
		private Bar bar;
		// 缓存tick
		private MD last;

		/**
		 * @param cycle
		 * @param md
		 */
		public Context(BarCycle cycle, MD md) {
			this.cycle = cycle;
			this.start = getTime(md.getTimeStamp());
			this.bar = createBar(md);
			this.bar.setStartTime(start);
			if (last == null) {
				last = md;
			}
		}

		/**
		 * @param md
		 * @return bar
		 */
		public Bar upate(MD md) {
			long current = getTime(md.getTimeStamp());
			if (start == current) {
				bar.setStatus(1);
				bar.setClose(md.getPrice());
				bar.setHigh(Math.max(bar.getHigh(), md.getPrice()));
				bar.setLow(Math.min(bar.getLow(), md.getPrice()));
				if (last != null) {
					// 最新价不能完全正确反映最高价，比如500毫秒内先后以2600、2558成交2笔，行情最新价是2558，最高价应该是2600
					if (md.getHigh() > last.getHigh()) {
						bar.setHigh(Math.max(bar.getHigh(), md.getHigh()));
					}
					if (md.getLow() < last.getLow()) {
						bar.setLow(Math.min(bar.getLow(), md.getLow()));
					}
					bar.setDealVol(bar.getDealVol() + md.getDealVol() - last.getDealVol()); // bar内成交量
				}
				bar.updatePriceChange();
			} else if (current > start) {
				start = current;
				bar.setEndTime(current);
				bar.updatePriceChange();
				bar.setStatus(2);
				// 通知K线生成完成
				notifyAllBarListeners(bar);

				Bar newBar = createBar(md);
				newBar.setStartTime(current);
				bar = newBar;
			}
			last = md;
			return bar;
		}

		/**
		 * @param md
		 * @return
		 */
		private Bar createBar(MD md) {
			Bar bar = new Bar();
			bar.setCode(md.getCode());
			bar.setEndTime(md.getTimeStamp());
			bar.setOpen(md.getPrice());
			bar.setClose(md.getPrice());
			bar.setHigh(md.getPrice());
			bar.setLow(md.getPrice());
			bar.setPeriod(cycle.getPeriod());
			bar.setVol(md.getVol());
			return bar;
		}

		/**
		 * @param time
		 * @return
		 */
		private long getTime(long time) {
			return time - time % cycle.getPeriod();
		}
	}
}
