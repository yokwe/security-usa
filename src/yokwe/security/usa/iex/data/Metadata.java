package yokwe.security.usa.iex.data;

import java.time.LocalDateTime;

import yokwe.security.usa.iex.Context;
import yokwe.util.StringUtil;
import yokwe.util.StringUtil.TimeZone;
import yokwe.util.StringUtil.UseTimeZone;
import yokwe.util.http.HttpUtil;
import yokwe.util.json.JSON;


public class Metadata {
	public static final int    DATA_WEIGHT = 0; // FREE
	public static final String METHOD      = "account/metadata";

	// {"payAsYouGoEnabled":false,"effectiveDate":1551225868000,"subscriptionTermType":"annual","tierName":"start","messageLimit":500000,"messagesUsed":0,"circuitBreaker":null}
	public boolean        payAsYouGoEnabled;
	@UseTimeZone(TimeZone.LOCAL)
	public LocalDateTime  effectiveDate;
	public String         subscriptionTermType;
	public String         tierName;
	public long           messageLimit;
	public long           messagesUsed;
	public long           circuitBreaker;
	
	public Metadata() {
		payAsYouGoEnabled    = false;
		effectiveDate        = null;
		subscriptionTermType = null;
		tierName             = null;
		messageLimit         = 0;
		messagesUsed         = 0;
		circuitBreaker       = 0;
	}

	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

	public static Metadata getInstance(Context context) {
		String url = context.getURL(METHOD);
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		return JSON.unmarshal(Metadata.class, result.result);
	}
}