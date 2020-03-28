package yokwe.security.usa.iex.test;

import java.util.List;

import org.slf4j.LoggerFactory;

import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.Type;
import yokwe.security.usa.iex.Version;
import yokwe.security.usa.iex.data.Metadata;
import yokwe.security.usa.iex.data.Previous;
import yokwe.security.usa.iex.data.Status;
import yokwe.security.usa.iex.data.Symbols;
import yokwe.security.usa.iex.data.Usage;

public class T001 {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(T001.class);

	public static void main(String[] args) {
		logger.info("START");
		
		{
			Context context = new Context(Type.PRODUCTION, Version.V1, "tmp/data");
			Context.save(Context.NAME_DATA, context);
		}
		{
			Context context = new Context(Type.SANDBOX, Version.V1, "tmp/test");
			Context.save(Context.NAME_TEST, context);
		}

		{
			logger.info("{} {}", Context.NAME_DATA, Context.load(Context.NAME_DATA).toString());
			logger.info("{} {}", Context.NAME_TEST, Context.load(Context.NAME_TEST).toString());
		}

//		Context context = Context.load(Context.NAME_DATA);
		Context context = Context.load(Context.NAME_TEST);
		logger.info("context  {}", context);
		
		logger.info("status    {}", Status.getInstance(context));
		logger.info("usage     {}", Usage.getInstance(context));
		logger.info("metadata  {}", Metadata.getInstance(context));
		
		{
			List<Symbols> list = Symbols.getInstance(context);
			Symbols.save(context, list);
			logger.info("symbols   {}", list.size());
			Symbols.save(context, list);
		}
		{
			List<Symbols> list = Symbols.getList(context);
			logger.info("symbols   {}", list.size());
		}
	
		{
			Previous previous = Previous.getInstance(context, "IBM");
			logger.info("previous {}", previous);
		}
			
		{
			List<Previous> list = Previous.getInstance(context, "IBM", "NYT");
			logger.info("previous size {}", list.size());
			for(int i = 0; i < list.size(); i++) {
				logger.info("previous {} {}", i, list.get(i));
			}
		}
			
		logger.info("STOP");
	}

}
