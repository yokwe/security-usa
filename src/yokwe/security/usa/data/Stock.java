package yokwe.security.usa.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.util.CSVUtil;
import yokwe.util.EnumUtil;

public class Stock implements Comparable<Stock> {	
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(Stock.class);

	public static final String PATH_FILE = "tmp/data/stock.csv";
	
	private static List<Stock> list = null;
	public static List<Stock> getList() {
		if (list == null) {
			list = CSVUtil.read(Stock.class).file(PATH_FILE);
			if (list == null) {
				list = new ArrayList<>();
			}
		}
		return list;
	}
	private static Map<String, Stock> map = null;
	public static Map<String, Stock> getMap() {
		if (map == null) {
			map = new TreeMap<>();
			for(Stock e: getList()) {
				String stockCode = e.stockCode;
				if (map.containsKey(stockCode)) {
					logger.error("Duplicate stockCode {}", stockCode);
					throw new UnexpectedException("Duplicate stockCode");
				} else {
					map.put(stockCode, e);
				}
			}
		}
		return map;
	}
	public static void save(Collection<Stock> collection) {
		save(new ArrayList<>(collection));
	}
	public static void save(List<Stock> list) {
		if (list.isEmpty()) return;
		
		// Sort before save
		Collections.sort(list);
		CSVUtil.write(Stock.class).file(PATH_FILE, list);
	}
	
	public enum Type {
		ADR("ad"),     // ADR
		CEF("cef"),    // Closed End Fund
		COM("cs"),     // Common Share
		ETF("et"),     // ETF
		OEF("oef"),    // Open Ended Fund
		PRF("ps"),     // Preferred Share
		RGT("rt"),     // Right
		STR("struct"), // Structure
		TMP("temp"),   // ??
		UNT("ut"),     // Unit
		WHI("wi"),     // When Issued
		WRT("wt");     // Warrant
		
		public final String value;
		Type(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return value;
		}
	}

	public enum Exchange {
	   NAS,     // NASDAQ
	   NYS,     // New York Stock Exchange
	   POR,     // PORTAL
	   USAMEX,  // NYSE MKT LLC
	   USBATS,  // CBOE BZX U.S. EQUITIES EXCHANGE
	   USPAC,   // NYSE ARCA
	}

	public String   date;
	public String   stockCode;
	public String   symbol;
	public Exchange exchange;
	public Type     type;
	public String   name;
	
	public Stock(String date, String symbol, String exchange, String type, String name) {
		this.date      = date;
		this.stockCode = normalizeSymbol(symbol);
		this.symbol    = symbol;
		this.exchange  = EnumUtil.getInstance(Exchange.class, exchange);
		this.type      = EnumUtil.getInstance(Type.class, type);
		this.name      = name;
	}
	public Stock() {
		this.date      = null;
		this.stockCode = null;
		this.symbol    = null;
		this.exchange  = null;
		this.type      = null;
		this.name      = null;
	}
	
	@Override
	public String toString() {
		return String.format("{%s %s %s %s %s %s}", date, stockCode, symbol, exchange, type, name);
	}
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof Stock) {
			Stock that = (Stock)o;
			return this.date.equals(that.date) &&
					this.stockCode.equals(that.stockCode) &&
					this.symbol.equals(that.symbol) &&
					this.exchange.equals(that.exchange) &&
					this.type.equals(that.type) &&
					this.name.equals(that.name);
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Stock that) {
		int ret = this.date.compareTo(that.date);
		if (ret == 0) ret = this.stockCode.compareTo(that.stockCode);
		return ret;
	}
	
	public static String normalizeSymbol(String symbol) {
		String stockCode;
		if (isCalled(symbol) || isWhenIssued(symbol) || isWhenDistributed(symbol)) {
			// Remove suffix * -- called
			// Remove suffix # -- when issued
			// Remove suffix $ -- when distributed
			stockCode = symbol.substring(0, symbol.length() - 1);
//			logger.info("remove suffix  {}  {}", stockCode, stockCodeString);
		} else {
			stockCode = symbol;
		}
		return stockCode;
	}
	
	public boolean isOridinary() {
		switch(type) {
		case ADR:
		case CEF:
		case COM:
		case ETF:
		case OEF:
		case PRF:
		case STR:
		case WHI:
		case TMP: // FIXME
			return true;
		case RGT:
		case UNT:
		case WRT:
			return false;
		default:
			logger.error("Unexpected type");
			logger.error("  type {}", type);
			throw new UnexpectedException("Unexpected type");
		}
	}

	// See CQS Symbol Conversion
	//  https://www.nasdaqtrader.com/trader.aspx?id=CQSsymbolconvention
	// static methods
	public static boolean isPreferred(String symbol) {
		return symbol.contains("-");
	}
	public static boolean isConvertible(String symbol) {
		return symbol.contains("%");
	}
	public static boolean isWarrant(String symbol) {
		return symbol.contains("+");
	}
	public static boolean isRight(String symbol) {
		return symbol.contains("^");
	}
	public static boolean isUnit(String symbol) {
		return symbol.contains("=");
	}
	public static boolean isCalled(String symbol) {
		return symbol.endsWith("*");
	}
	public static boolean isWhenIssued(String symbol) {
		return symbol.endsWith("#");
	}
	public static boolean isWhenDistributed(String symbol) {
		return symbol.endsWith("$");
	}

}
