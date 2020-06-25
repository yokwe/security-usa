package yokwe.security.usa.iex;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.util.FileUtil;
import yokwe.util.json.JSON;


public class Token {
	static final Logger logger = LoggerFactory.getLogger(Token.class);

	public static final String PATH_DATA_DIR = "tmp/iex/token";
	
	public static final String NAME_PRODUCTION = "production";
	public static final String NAME_SANDBOX    = "sandbox";
	
	private static String getPath(String name) {
		return String.format("%s/%s", PATH_DATA_DIR, name);
	}
	public static Token load(String name) {
		File file = new File(getPath(name));
		if (!file.canRead()) {
			logger.error("Cannot read file");
			logger.error("  file  {}", file.getPath());
			throw new UnexpectedException("Cannot read file");
		}
		String jsonString = FileUtil.read().file(file);
		return JSON.unmarshal(Token.class, jsonString);
	}
	public static void save(String name, Token token) {
		File file = new File(getPath(name));
		String jsonString = JSON.toJSONString(token);
		FileUtil.write().file(file, jsonString);
	}

	private static Map<Type, Token> map = new TreeMap<>();
	static {
		Token production = load(NAME_PRODUCTION);
		Token sandbox    = load(NAME_SANDBOX);
		
		map.put(Type.PRODUCTION, production);
		map.put(Type.SANDBOX,    sandbox);
	}
	public static Token get(Type type) {
		return map.get(type);
	}
	
		
	public String publishable;
	public String secret;
	
	public Token(String publishable, String secret) {
		this.publishable = publishable;
		this.secret      = secret;
	}
	public Token() {
		this(null, null);
	}

	@Override
	public String toString() {
		return String.format("{%s %s}", publishable, secret);
	}
	
	public static void main(String[] args) {
		logger.info("START");
		
//		{
//			Token token = new Token("pk_5e5ae9b9eb994607b7004420cdbb8276", "sk_88235a3aba0b4dd4a9da4d282f41a015");
//			writeFile(NAME_PRODUCTION, token);
//		}
//		{
//			Token token = new Token("Tpk_dbb6f1de7eff420da948e909329d748f", "Tsk_4eda401a15b642b9bba7fb855c9d3318");
//		}
		
		{
			logger.info("production  {}", get(Type.PRODUCTION));
			logger.info("sandbox     {}", get(Type.SANDBOX));
		}
		
		logger.info("STOP");
	}
}