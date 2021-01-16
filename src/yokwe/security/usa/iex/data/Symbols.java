package yokwe.security.usa.iex.data;

import java.io.StringReader;
import java.util.List;

import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.Format;
import yokwe.util.CSVUtil;
import yokwe.util.StringUtil;
import yokwe.util.http.HttpUtil;

public class Symbols implements Comparable<Symbols> {
	public static final int    DATA_WEIGHT = 100; // 100 per call

	// symbol,exchange,exchangeSuffix,exchangeName,name,date,type,iexId,region,currency,isEnabled,figi,cik.lei
	public String  symbol;
	public String  exchange;
	public String  exchangeSuffix;
	public String  exchangeName;
	public String  name;
	public String  date;
	public String  type;
	public String  iexId;
	public String  region;
	public String  currency;
	public boolean isEnabled;
	public String  figi;
	public String  cik;
	public String  lei;

	public Symbols() {
		symbol         = null;
		exchange       = null;
		exchangeSuffix = null;
		exchangeName   = null;
		name           = null;
		date           = null;
		type           = null;
		iexId          = null;
		region         = null;
		currency       = null;
		isEnabled      = false;
		figi           = null;
		cik            = null;
		lei            = null;
	}
	
	@Override
	public int compareTo(Symbols that) {
		return this.symbol.compareTo(that.symbol);
	}

	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

	public static final String METHOD = "ref-data/symbols";
	public static List<Symbols> getInstance(Context context) {
		String url = context.getURL(METHOD, Format.CSV);
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		return CSVUtil.read(Symbols.class).file(new StringReader(result.result));
	}
}
