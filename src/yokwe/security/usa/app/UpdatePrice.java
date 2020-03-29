package yokwe.security.usa.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.usa.data.Price;
import yokwe.security.usa.data.Stock;
import yokwe.security.usa.iex.Context;
import yokwe.security.usa.iex.data.Metadata;
import yokwe.security.usa.iex.data.Previous;
import yokwe.security.usa.iex.data.Status;
import yokwe.util.CSVUtil;

public class UpdatePrice {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdatePrice.class);

	public static final String PATH_FILE_SYMBOLS  = "tmp/data/symbols.csv";
	public static final String PATH_FILE_PREVIOUS = "tmp/data/previous.csv";

	
	private static final DateTimeFormatter YYYYMMDD_HHMMSS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	public static void delistUnknownFile(List<String> stockCodeList) {
		String timestamp = LocalDateTime.now().format(YYYYMMDD_HHMMSS);

		Set<String> stockCodeSet = stockCodeList.stream().collect(Collectors.toSet());
		
		try {
			File dir = new File(Price.PATH_DIR_DATA);
			if (!dir.exists()) {
				logger.info("create directory  {}", dir.getPath());
				dir.mkdirs();
			}
			File delistDir = new File(Price.PATH_DIR_DATA_DELIST);
			if (!delistDir.exists()) {
				logger.info("create directory  {}", delistDir.getPath());
				delistDir.mkdirs();
			}

			File[] files = dir.listFiles(o -> o.getName().endsWith(".csv"));
			for(File file: files) {
				String fileSymbol = file.getName().replace(".csv", "");
				// Skip if fileSymbol is in valid symbol
				if (stockCodeSet.contains(fileSymbol)) continue;
				
				// delist Unknown file
				String delistFileName = String.format("%s-%s.csv", fileSymbol, timestamp);
				File delistedFile = new File(delistDir, delistFileName);
				logger.info("delist {}  {}", file.getPath(), delistedFile.getPath());
				Files.move(file.toPath(), delistedFile.toPath());
			}
		} catch (IOException e) {
			String exceptionName = e.getClass().getSimpleName();
			logger.error("{} {}", exceptionName, e);
			throw new UnexpectedException(exceptionName, e);
		}
	}

	public static final int    MAX_PARAM     = 100;

	private static List<Previous> getPreviousList(Context context, List<String> symbolList) {
		List<Previous> previousList = new ArrayList<>();
		
		int symbolListSize = symbolList.size();
		for(int i = 0; i < symbolListSize; i += MAX_PARAM) {
			int fromIndex = i;
			int toIndex = Math.min(fromIndex + MAX_PARAM, symbolListSize);
			List<String> getList = symbolList.subList(fromIndex, toIndex);
			if (getList.isEmpty()) continue;
			if (getList.size() == 1) {
				logger.info("{}", String.format("%4d / %4d  %-7s", fromIndex, symbolListSize, getList.get(0)));
			} else {
				logger.info("{}", String.format("%4d / %4d  %-7s - %-7s", fromIndex, symbolListSize, getList.get(0), getList.get(getList.size() - 1)));
			}
			
			List<Previous> list = Previous.getInstance(context, getList.toArray(new String[0]));
			previousList.addAll(list);
		}

		return previousList;
	}

	private static void update(Context context) {
		List<Stock> stockList = Stock.getList();
		logger.info("stockList {}", stockList.size());
		List<String> stockCodeList = stockList.stream().map(o -> o.stockCode).collect(Collectors.toList());
		List<String> symbolList = stockList.stream().map(o -> o.symbol).collect(Collectors.toList());
		
		delistUnknownFile(stockCodeList);
		
		List<Previous> previousList = getPreviousList(context, symbolList);
		logger.info("previous {} {}", previousList.size(), PATH_FILE_PREVIOUS);
		CSVUtil.write(Previous.class).file(PATH_FILE_PREVIOUS, previousList);

		List<Price> priceList = previousList.stream().map(o -> new Price(o.date, Stock.normalizeSymbol(o.symbol), o.open, o.high, o.low, o.close, o.volume)).collect(Collectors.toList());
		int count = 0;
		int countTotal = priceList.size();
		for(Price price: priceList) {
			String date      = price.date;
			String stockCode = price.stockCode;

			if ((count % 1000) == 0) {
				logger.info("{}", String.format("%4d / %4d  %-7s", count, countTotal, stockCode));
			}
			count++;

			Map<String, Price> priceMap = Price.getPriceMap(stockCode);
			//  date
			priceMap.put(date, price);
			Price.save(stockCode, priceMap.values());
		}
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
