package yokwe.security.usa.iex;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.util.FileUtil;
import yokwe.util.StringUtil;
import yokwe.util.http.HttpUtil;
import yokwe.util.json.JSON;
import yokwe.util.json.JSON.Ignore;

public class Context {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(Context.class);
	
	public static final String PATH_DATA_DIR = "tmp/iex/context";
	
	public static final String NAME_DATA = "data";
	public static final String NAME_TEST = "test";
	
	private static Map<String, Context> map = null;
	public static Context getInstance(String name) {
		if (map == null) {
			map = new TreeMap<>();
			for(File file: FileUtil.listFile(PATH_DATA_DIR)) {
				Context context = load(file);
				String contextName = context.name;
				if (map.containsKey(contextName)) {
					logger.error("Duplicate name");
					logger.error("  name {}", contextName);
					logger.error("  old  {}", map.get(contextName));
					logger.error("  new  {}", context);
					throw new UnexpectedException("Duplicate name");
				} else {
					map.put(context.name, context);
//					logger.info("context {} {} {}", context.name, context.version, context.type);
				}
			}
		}
		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			logger.error("Unexpected name");
			logger.error("  name {}", name);
			throw new UnexpectedException("Unexpected name");
		}
	}

	public static Context load(File file) {
		if (!file.canRead()) {
			logger.error("Cannot read file");
			logger.error("  file  {}", file.getPath());
			throw new UnexpectedException("Cannot read file");
		}
		String jsonString = FileUtil.read().file(file);
		Context ret = JSON.unmarshal(Context.class, jsonString);
		ret.token = Token.get(ret.type);
		return ret;
	}
	public static void save(Context context) {
		String path = String.format("%s/%s", PATH_DATA_DIR, context.name);
		File file = new File(path);
		String jsonString = JSON.toJSONString(context);
		FileUtil.write().file(file, jsonString);
	}
	
	public String  name;
	public Version version;
	public Type    type;
	@Ignore
	public Token   token;
	@Ignore
	public int     tokenUsed;
	@Ignore
	public int     tokenUsedTotal;
	
	public Context() {
		this.name           = null;
		this.version        = null;
		this.type           = null;
		this.token          = null;
		this.tokenUsed      = 0;
		this.tokenUsedTotal = 0;
	}
	public Context(String name, Version version, Type type) {
		this.name           = name;
		this.version        = version;
		this.type           = type;
		this.token          = Token.get(type);
		this.tokenUsed      = 0;
		this.tokenUsedTotal = 0;
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
//		return String.format("{%s %s %s}", name, version.toString(), type.toString());
	}
	
	//
	// tokenUsed
	//
	// Support method for iex-clound
	private static final String HEADER_IEXCLOUD_MESSAGES_USED = "iexcloud-messages-used";
	public static int getTokenUsed(HttpUtil.Result result) {
		if (result.headerMap.containsKey(HEADER_IEXCLOUD_MESSAGES_USED)) {
			String value = result.headerMap.get(HEADER_IEXCLOUD_MESSAGES_USED);
			return Integer.valueOf(value);
		} else {
			return -1;
		}
	}
	
	public void setTokenUsed(HttpUtil.Result result, int dataWeight) {
		if (result.headerMap.containsKey(HEADER_IEXCLOUD_MESSAGES_USED)) {
			String stringValue = result.headerMap.get(HEADER_IEXCLOUD_MESSAGES_USED);
			Integer value = Integer.valueOf(stringValue);
			
			tokenUsed       = value;
			tokenUsedTotal += value;
		} else {
			tokenUsed       = 0;
		}
		
		if (tokenUsed != dataWeight) {
			logger.warn("Unexpected token usage");
			logger.warn("  expected {}", dataWeight);
			logger.warn("  actual   {}", tokenUsed);
		}
	}
	public int getTokenUsed() {
		return tokenUsed;
	}
	public int getTokenUsedTotal() {
		return tokenUsedTotal;
	}
	
	//
	// getURL
	//
	public String getURL(String endPoint) {
		return String.format("%s/%s/%s?token=%s", type.url, version.url, endPoint, token.secret);
	}
	public String getURL(String endPoint, Format format) {
		return String.format("%s/%s/%s?token=%s&format=%s", type.url, version.url, endPoint, token.secret, format.value);
	}
	public String getURL(String endPoint, Format format, Map<String, String> paramMap) {
		// Sanity check
		if (paramMap == null) {
			logger.error("paramMap == null");
			throw new UnexpectedException("paramMap == null");
		}
		
		StringBuilder ret = new StringBuilder(getURL(endPoint, format));
		
		for(Map.Entry<String, String> entry: paramMap.entrySet()) {
			ret.append(String.format("&%s=%s", StringUtil.urlEncode(entry.getKey()), StringUtil.urlEncode(entry.getValue())));
		}
		return ret.toString();
	}
}