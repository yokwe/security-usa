package yokwe.security.usa.iex.data;

import java.util.Map;

import javax.json.JsonObject;

import yokwe.security.usa.iex.Context;
import yokwe.util.http.HttpUtil;
import yokwe.util.json.JSONBase;

public class Usage extends JSONBase {
	public static final int    DATA_WEIGHT = 0; // FREE
	public static final String METHOD      = "account/usage";
	
//	{
//	    "messages": {
//	        "dailyUsage": {
//	            "20190704": "504",
//	            "20190705": "302",
//	            "20190706": "0"
//	        },
//	        "monthlyUsage": 806,
//	        "monthlyPayAsYouGo": 0,
//	        "tokenUsage": {
//	            
//	        },
//	        "keyUsage": {
//	            "ACCOUNT_USAGE": "0",
//	            "REF_DATA_IEX_SYMBOLS": "0",
//	            "REF_DATA": "300",
//	            "REF_DATA_OTC": "300",
//	            "REF_DATA_EXCHANGES": "6",
//	            "REF_DATA_MUTUAL_FUNDS": "200",
//	            "IEX_DEEP": "0",
//	            "IEX_TOPS": "0",
//	            "IEX_STATS": "0"
//	        }
//	    },
//	    "rules": []
//	}
	
	public static class Messages extends JSONBase {
		public Map<String, Long> dailyUsage;
		public long              monthlyUsage;
		public long              monthlyPayAsYouGo;
		public Map<String, Long> tokenUsage;
		public Map<String, Long> keyUsage;
		
		public Messages() {
			dailyUsage        = null;
			monthlyUsage      = 0;
			monthlyPayAsYouGo = 0;
			tokenUsage        = null;
			keyUsage          = null;
		}
		
		public Messages(JsonObject jsonObject) {
			super(jsonObject);
		}
	}
	
	public static class Rule extends JSONBase {
		public Rule() {
		}
		public Rule(JsonObject jsonObject) {
			super(jsonObject);
		}
	}
	
	public Messages messages;
	public Rule[]   rules;
	
	public Usage() {
		messages = null;
		rules    = null;
	}
	public Usage(JsonObject jsonObject) {
		super(jsonObject);
	}
	
	public static Usage getInstance(Context context) {
		String url = context.getURL(METHOD);
		HttpUtil.Result result = HttpUtil.getInstance().download(url);
		context.setTokenUsed(result, DATA_WEIGHT);
		return JSONBase.getInstance(Usage.class, result.result);
	}
}