package yokwe.security.usa.iex.test;

import org.slf4j.LoggerFactory;

import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.data.Metadata;
import yokwe.security.usa.iex.data.Status;
import yokwe.security.usa.iex.data.Usage;

public class T001 {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(T001.class);

	public static void main(String[] args) {
		logger.info("START");
		
		Context context = Context.load(Context.NAME_DATA);
		logger.info("context {}", context);
		
		logger.info("status   {}", Status.getInstance(context));
		logger.info("usage    {}", Usage.getInstance(context));
		logger.info("metadata {}", Metadata.getInstance(context));
		
		logger.info("STOP");
	}

}
