package yokwe.security.usa.iex;

import java.io.File;
import java.util.Map;

import javax.json.JsonObject;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.util.FileUtil;
import yokwe.util.HttpUtil;
import yokwe.util.StringUtil;
import yokwe.util.json.JSONBase;

public class Context extends JSONBase {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(Context.class);
	
	public static final String PATH_DATA_DIR = "tmp/iex/context";
	
	public static final String NAME_DATA = "data";
	public static final String NAME_TEST = "test";

	private static String getPath(String name) {
		return String.format("%s/%s", PATH_DATA_DIR, name);
	}
	public static Context load(String name) {
		File file = new File(getPath(name));
		if (!file.canRead()) {
			logger.error("Cannot read file");
			logger.error("  file  {}", file.getPath());
			throw new UnexpectedException("Cannot read file");
		}
		String jsonString = FileUtil.read().file(file);
		return JSONBase.getInstance(Context.class, jsonString);
	}
	public static void save(String name, Context context) {
		File file = new File(getPath(name));
		String jsonString = context.toJSONString();
		FileUtil.write().file(file, jsonString);
	}
	
	public Type    type;
	public Version version;
	public String  basePath;
	@IgnoreField
	public Token   token;
	@IgnoreField
	public int     tokenUsed;
	@IgnoreField
	public int     tokenUsedTotal;
	
	public Context() {
		this.type           = null;
		this.version        = null;
		this.basePath       = null;
		this.token          = null;
		this.tokenUsed      = 0;
		this.tokenUsedTotal = 0;
	}
	public Context(Type type, Version version, String basePath) {
		this.type           = type;
		this.version        = version;
		this.basePath       = basePath;
		this.token          = Token.get(type);
		this.tokenUsed      = 0;
		this.tokenUsedTotal = 0;
	}
	
	public Context(JsonObject jsonObject) {
		super(jsonObject);
		token = Token.get(type);
	}
	
	@Override
	public String toString() {
		return String.format("{%s %s %s %s %d %d}", type.toString(), version.toString(), basePath, token, tokenUsed, tokenUsedTotal);
	}
	
	public String getBasePath() {
		return basePath;
	}
	public String getFilePath(String path) {
		return String.format("%s/%s", basePath, path);
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