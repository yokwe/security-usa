package yokwe.security.usa.iex.data;

import java.io.StringReader;
import java.util.List;

import javax.json.JsonObject;

import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.Format;
import yokwe.util.CSVUtil;
import yokwe.util.HttpUtil;
import yokwe.util.json.JSONBase;

public class Symbols extends JSONBase implements Comparable<Symbols> {
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
}
