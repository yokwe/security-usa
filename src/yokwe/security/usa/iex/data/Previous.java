package yokwe.security.usa.iex.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.json.JsonObject;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.Format;
import yokwe.util.HttpUtil;
import yokwe.util.StringUtil;
import yokwe.util.json.JSONBase;

public class Previous extends JSONBase implements Comparable<Previous> {	
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
	
//	date,open,close,high,low,volume,uOpen,uClose,uHigh,uLow,uVolume,change,changePercent,changeOverTime,symbol
//	2019-07-05,32.72,33.01,33.15,32.62,223015,32.72,33.01,33.15,32.62,223015,0,0,0,TRTN	
	
	public String date;
	public double open;
	public double close;
	public double high;
	public double low;
	public long   volume;
	public double uOpen;
	public double uClose;
	public double uHigh;
	public double uLow;
	public long   uVolume;
	public double change;
	public double changePercent;
	public double changeOverTime;
	public String symbol;
	
	public Previous() {
		date           = null;
		open           = 0;
		close          = 0;
		high           = 0;
		low            = 0;
		volume         = 0;
		uOpen          = 0;
		uClose         = 0;
		uHigh          = 0;
		uLow           = 0;
		uVolume        = 0;
		change         = 0;
		changePercent  = 0;
		changeOverTime = 0;
		symbol         = null;
	}
	
	public Previous(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public int compareTo(Previous that) {
		return this.symbol.compareTo(that.symbol);
	}
	
	public static final String METHOD      = "stock/%s/previous";
	public static Previous getInstance(Context context, String symbol) {
		String url = context.getURL(String.format(METHOD, StringUtil.urlEncode(symbol)));
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		logger.info("result {}", result.result);
		return JSONBase.getInstance(Previous.class, result.result);
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
		List<Previous> ret = JSONBase.getList(Previous.class, result.result);
		
		context.setTokenUsed(result, DATA_WEIGHT * ret.size());
		return ret;
	}

}