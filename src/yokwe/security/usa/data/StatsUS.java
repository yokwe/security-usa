package yokwe.security.usa.data;

import java.util.Collections;
import java.util.List;

import yokwe.util.CSVUtil;

public class StatsUS implements Comparable<StatsUS> {
	public static final String PATH_FILE        = "tmp/data/stats-us.csv";

	public static void save(List<StatsUS> list) {
		// Sort before save
		Collections.sort(list);
		CSVUtil.write(StatsUS.class).file(PATH_FILE, list);
	}

	public String stockCode;
	public String type;
	public String name;
	public String date;
	
	// current price and volume
	public int    pricec;
	public double price;
	
	// last price
	public double last;
	public double lastPCT;
	
	// stats - sd hv rsi
	//  30 < pricec
	public double sd;
	public double hv;
	// 15 <= pricec
	public double rsi;
	
	// min max
	public double min;
	public double max;
	public double minPCT;
	public double maxPCT;
	
	// volume
	public long   vol;
	// 5 <= pricec
	public long   vol5;
	// 20 <= pricec
	public long   vol20;
	

	public StatsUS() {
		stockCode = null;
		type      = null;
		name      = null;
		date      = null;
		pricec    = -1;
		price     = -1;
		
		last      = -1;
		lastPCT   = -1;
		
		sd        = -1;
		hv        = -1;
		
		rsi       = -1;
		
		min       = -1;
		max       = -1;
		minPCT    = -1;
		maxPCT    = -1;
		
		vol       = -1;
		vol5      = -1;
		vol20     = -1;
		
	}

	@Override
	public int compareTo(StatsUS that) {
		return this.stockCode.compareTo(that.stockCode);
	}
}
