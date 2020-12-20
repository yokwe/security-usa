package yokwe.security.usa.iex.data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.Format;
import yokwe.util.StringUtil;
import yokwe.util.http.HttpUtil;
import yokwe.util.json.JSON;

public class Previous implements Comparable<Previous> {	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Previous.class);

	public static final int    DATA_WEIGHT = 2; // 2 per return records
	
//	{
//	    "date": "2019-07-05",
//	    "open": 32.72,
//	    "close": 33.01,
//	    "high": 33.15,
//	    "low": 32.62,
//	    "volume": 223015,
//	    "uOpen": 32.72,
//	    "uClose": 33.01,
//	    "uHigh": 33.15,
//	    "uLow": 32.62,
//	    "uVolume": 223015,
//	    "change": 0,
//	    "changePercent": 0,
//	    "changeOverTime": 0,
//	    "symbol": "TRTN"
//	}
	
//	{
//		"close":125.85,
//		"high":126.4,
//		"low":124.97,
//		"open":125.59,
//		"symbol":"IBM",
//		"volume":7552845,
//		"id":"HISTORICAL_PRICES",
//		"key":"IBM",
//		"subkey":"",
//		"date":"2020-12-18",
//		"updated":1608344177000,
//		"changeOverTime":0,
//		"marketChangeOverTime":0,
//		"uOpen":125.59,
//		"uClose":125.85,
//		"uHigh":126.4,
//		"uLow":124.97,
//		"uVolume":7552845,
//		"fOpen":125.59,
//		"fClose":125.85,
//		"fHigh":126.4,
//		"fLow":124.97,
//		"fVolume":7552845,
//		"label":"Dec 18, 20",
//		"change":0,
//		"changePercent":0
//	}
	
	public double        close;
	public double        high;
	public double        low;
	public double        open;
	public String        symbol;
	public long          volume;
	public String        id;
	public String        key;
	public String        subkey;
	public String        date;
	public LocalDateTime updated;
	public double        changeOverTime;
	public double        marketChangeOverTime;
	public double        uOpen;
	public double        uClose;
	public double        uHigh;
	public double        uLow;
	public long          uVolume;
	public double        fOpen;
	public double        fClose;
	public double        fHigh;
	public double        fLow;
	public long          fVolume;
	public String        label;
	public double        change;
	public double        changePercent;
	
	public Previous() {
		close                = 0;
		high                 = 0;
		low                  = 0;
		open                 = 0;
		symbol               = null;
		volume               = 0;
		id                   = null;
		key                  = null;
		subkey               = null;
		date                 = null;
		updated              = null;
		changeOverTime       = 0;
		marketChangeOverTime = 0;
		uOpen                = 0;
		uClose               = 0;
		uHigh                = 0;
		uLow                 = 0;
		uVolume              = 0;
		fOpen                = 0;
		fClose               = 0;
		fHigh                = 0;
		fLow                 = 0;
		fVolume              = 0;
		label                = null;
		change               = 0;
		changePercent        = 0;
	}
	
	@Override
	public int compareTo(Previous that) {
		return this.symbol.compareTo(that.symbol);
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

	public static final String METHOD      = "stock/%s/previous";
	public static Previous getInstance(Context context, String symbol) {
		String url = context.getURL(String.format(METHOD, StringUtil.urlEncode(symbol)));
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		logger.info("result {}", result.result);
		return JSON.unmarshal(Previous.class, result.result);
	}
	
	public static final int    MAX_PARAM     = 100;
	public static final String METHOD_MARKET = "/stock/market/previous";
	public static List<Previous> getInstance(Context context, String... symbols) {
		// Sanity check
		if (symbols.length == 0) {
			logger.error("symbols.length == 0");
			throw new UnexpectedException("symbols.length == 0");
		}
		if (MAX_PARAM < symbols.length) {
			logger.error("symbols.length exceeds limit");
			logger.error("  symbols.length {}", symbols.length);
			throw new UnexpectedException("symbols.length exceeds limit");
		}
		
		Map<String, String> paramMap = new TreeMap<>();
		paramMap.put("symbols", Arrays.stream(symbols).map(o -> StringUtil.urlEncode(o)).collect(Collectors.joining(",")));
		
		String url  = context.getURL(METHOD_MARKET, Format.JSON, paramMap);
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		List<Previous> ret = JSON.getList(Previous.class, result.result);
		
		context.setTokenUsed(result, DATA_WEIGHT * ret.size());
		return ret;
	}

}