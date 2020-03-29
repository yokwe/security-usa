package yokwe.security.usa.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.usa.data.Stock;
import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.data.Metadata;
import yokwe.security.usa.iex.data.Status;
import yokwe.security.usa.iex.data.Symbols;
import yokwe.util.CSVUtil;

public class UpdateStock {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdateStock.class);

	public static final String SYMBOLS_PATH_FILE = "tmp/data/symbols.csv";

	private static void update(Context context) {
		List<Symbols> symbolsList = Symbols.getInstance(context);
		// save symbolsList for debug
		logger.info("symbolsList {} {}", symbolsList.size(), SYMBOLS_PATH_FILE);
		CSVUtil.write(Symbols.class).file(SYMBOLS_PATH_FILE, symbolsList);

		List<Stock> stockList = new ArrayList<>();
		for(Symbols symbols: symbolsList) {
			if (!symbols.isEnabled) continue;
			
			// String date, String stockCode, String exchange, String type, String name
			Stock stock = new Stock(symbols.date, symbols.symbol, symbols.exchange, symbols.type, symbols.name);
			if (!stock.isOridinary()) continue;
			
			stockList.add(stock);
		}
		
		// save stockList
		logger.info("stockList   {} {}", stockList.size(), Stock.PATH_FILE);
		Stock.save(stockList);
	}
	
	public static void main(String[] args) {
		logger.info("START");
		
		Context context = Context.getInstance(Context.NAME_DATA);
		logger.info("context {}", context);
		{
			Status status = Status.getInstance(context);
			logger.info("status {}", status);
			if (!status.isUp()) {
				logger.error("iex cloud is down");
				throw new UnexpectedException("iex cloud is down");
			}
		}
		
		{
			Metadata metadata = Metadata.getInstance(context);
			logger.info("message usage {} / {}  {}%", metadata.messagesUsed, metadata.messageLimit, Math.round(((double)metadata.messagesUsed *100) / (double)metadata.messageLimit));
		}
		
		update(context);
		logger.info("massage used  {}", context.tokenUsedTotal);

		{
			Metadata metadata = Metadata.getInstance(context);
			logger.info("message usage {} / {}  {}%", metadata.messagesUsed, metadata.messageLimit, Math.round(((double)metadata.messagesUsed *100) / (double)metadata.messageLimit));
		}
		
		logger.info("STOP");		
	}
}
