package yokwe.security.usa.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.util.CSVUtil;

public class Price implements Comparable<Price> {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Price.class);

	public static final String PATH_DIR_DATA = "tmp/data/price";
	public static String getPath(String stockCode) {
		return String.format("%s/%s.csv", PATH_DIR_DATA, stockCode);
	}
	public static final String PATH_DIR_DATA_DELIST = "tmp/data/price-delist";


	public static void save(String stockCode, Collection<Price> collection) {
		save(stockCode, new ArrayList<>(collection));
	}
	public static void save(String stockCode, List<Price> list) {
		String path = getPath(stockCode);
		
		// Sort before save
		Collections.sort(list);
		CSVUtil.write(Price.class).file(path, list);
	}
	
	public static Map<String, Price> getPriceMap(String stockCode) {
		//            date		
		Map<String, Price> ret = new TreeMap<>();

		for(Price price: getList(stockCode)) {
			String date = price.date;
			if (ret.containsKey(date)) {
				logger.error("duplicate date {}!", date);
				logger.error("old {}", ret.get(date));
				logger.error("new {}", date);
				throw new UnexpectedException("duplicate date");
			} else {
				ret.put(date, price);
			}
		}
		return ret;
	}
	
	public static List<Price> getList(String stockCode) {
		String path = getPath(stockCode);
		List<Price> ret = CSVUtil.read(Price.class).file(path);
		return ret == null ? new ArrayList<>() : ret;
	}
	private static Map<String, Map<String, Price>> map = new TreeMap<>();
	//                 stockCode   date
	public static Price getPrice(String stockCode, String date) {
		Map<String, Price> priceMap;
		if (map.containsKey(stockCode)) {
			priceMap = map.get(stockCode);
		} else {
			priceMap = getList(stockCode).stream().collect(Collectors.toMap(Price::getDate, Function.identity()));
			map.put(stockCode, priceMap);
		}
		if (priceMap.containsKey(date)) {
			return priceMap.get(date);
		} else {
			return null;
		}
	}
	

	public String date;      // YYYY-MM-DD
	public String stockCode; // normalized ticker symbol
	public double open;
	public double high;
	public double low;
	public double close;
	public long   volume;
	
	public Price(String date, String stockCode, double open, double high, double low, double close, long volume) {
		this.date      = date;
		this.stockCode = stockCode;
		this.open      = open;
		this.high      = high;
		this.low       = low;
		this.close     = close;
		this.volume    = volume;
	}
	public Price() {
		this(null, null, 0, 0, 0, 0, 0);
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getStockCode() {
		return this.stockCode;
	}

	@Override
	public String toString() {
		return String.format("%s %s %.1f %.1f %.1f %.1f %d", date, stockCode, open, high, low, close, volume);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof Price) {
			Price that = (Price)o;
			return
				this.date.equals(that.date) &&
				this.stockCode.equals(that.stockCode) &&
				this.open   == that.open &&
				this.high   == that.high &&
				this.low    == that.low &&
				this.close  == that.close &&
				this.volume == that.volume;
		} else {
			return false;
		}
	}
	
	@Override
	public int compareTo(Price that) {
		int ret = this.date.compareTo(that.date);
		if (ret == 0) ret = this.stockCode.compareTo(that.stockCode);
		return ret;
	}
}
