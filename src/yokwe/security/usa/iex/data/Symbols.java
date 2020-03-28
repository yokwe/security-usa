package yokwe.security.usa.iex.data;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.json.JsonObject;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.Format;
import yokwe.util.CSVUtil;
import yokwe.util.HttpUtil;
import yokwe.util.json.JSONBase;

public class Symbols extends JSONBase implements Comparable<Symbols> {	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Symbols.class);

	public static final int    DATA_WEIGHT = 100; // 100 per call

	// symbol,exchange,name,date,type,iexId,region,currency,isEnabled
	public String  symbol;
	public String  exchange;
	public String  name;
	public String  date;
	public String  type;
	public String  iexId;
	public String  region;
	public String  currency;
	public boolean isEnabled;
	public String  figi;
	public String  cik;

	public Symbols() {
		symbol    = null;
		exchange  = null;
		name      = null;
		date      = null;
		type      = null;
		iexId     = null;
		region    = null;
		currency  = null;
		isEnabled = false;
		figi      = null;
		cik       = null;
	}
	
	public Symbols(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public int compareTo(Symbols that) {
		return this.symbol.compareTo(that.symbol);
	}
	
	public static final String METHOD = "ref-data/symbols";
	public static List<Symbols> getInstance(Context context) {
		String url = context.getURL(METHOD, Format.CSV);
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		return CSVUtil.read(Symbols.class).file(new StringReader(result.result));
	}
	
	
	//
	// save and getList()
	//
	public static final String FILE_NAME = "symbols.csv";
	
	public static void save(Context context, Collection<Symbols> collection) {
		save(context, new ArrayList<>(collection));
	}
	public static void save(Context context, List<Symbols> list) {
		// Sort before save
		Collections.sort(list);
		String path = context.getFilePath(FILE_NAME);
		CSVUtil.write(Symbols.class).file(path, list);
	}
	
	public static List<Symbols> getList(Context context) {
		String path = context.getFilePath(FILE_NAME);
		List<Symbols> ret = CSVUtil.read(Symbols.class).file(path);
		if (ret == null) {
			ret = new ArrayList<>();
		}
		return ret;
	}

	public static Map<String, Symbols> getMap(Context context) {
		Map<String, Symbols> ret = new TreeMap<>();
		for(Symbols e: getList(context)) {
			String symbol = e.symbol;
			if (ret.containsKey(symbol)) {
				logger.error("duplicate symbol {}!", symbol);
				logger.error("old {}", ret.get(symbol));
				logger.error("new {}", e);
				throw new UnexpectedException("duplicate stockCode");
			} else {
				ret.put(e.symbol, e);
			}
		}
		return ret;
	}

}
