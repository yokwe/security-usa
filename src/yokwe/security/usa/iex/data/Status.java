package yokwe.security.usa.iex.data;

import java.time.LocalDateTime;

import yokwe.security.usa.iex.Context;
import yokwe.util.StringUtil;
import yokwe.util.StringUtil.TimeZone;
import yokwe.util.StringUtil.UseTimeZone;
import yokwe.util.http.HttpUtil;
import yokwe.util.json.JSON;

public class Status {
	public static final int    DATA_WEIGHT = 0; // FREE
	public static final String METHOD      = "status";
	
	public static final String STATUS_UP = "up";
	
	public String 		 status;
	public String 		 version;
	@UseTimeZone(TimeZone.LOCAL)
	public LocalDateTime time;
	public long          currentMonthAPICalls;
	
	public Status() {
		status               = null;
		version              = null;
		time                 = null;
		currentMonthAPICalls = 0;
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	public boolean isUp() {
		return status.equals(STATUS_UP);
	}
	
	public static Status getInstance(Context context) {
		String url = context.getURL(METHOD);
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		return JSON.unmarshal(Status.class, result.result);
	}
}
