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
	public int    pricec;
	public double price;
	public long   vol;

	public StatsUS() {
		stockCode = null;
		type      = null;
		name      = null;
		date      = null;
		pricec    = 0;
		price     = 0;
		vol       = 0;
	}

	@Override
	public int compareTo(StatsUS that) {
		return this.stockCode.compareTo(that.stockCode);
	}
}
