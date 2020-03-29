package yokwe.security.usa.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import yokwe.security.usa.data.Price;
import yokwe.security.usa.data.StatsUS;
import yokwe.security.usa.data.Stock;

public class UpdateStatsUS {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdatePrice.class);

	
	public static void main(String[] args) {
		logger.info("START");
		
		List<StatsUS> statsList = new ArrayList<>();
		for(Stock stock: Stock.getList()) {
			List<Price> priceList = Price.getList(stock.stockCode);
			if (priceList.isEmpty()) continue;

			Price price = priceList.get(priceList.size() - 1);

			StatsUS statsUS = new StatsUS();
			
			statsUS.stockCode = stock.stockCode;
			statsUS.type      = stock.type.toString();
			statsUS.name      = stock.name;
			statsUS.date      = stock.date;
			
			statsUS.pricec = priceList.size();

			statsUS.price = price.close;
			statsUS.vol   = price.volume;

			statsList.add(statsUS);
		}
		logger.info("save {} {}", statsList.size(), StatsUS.PATH_FILE);
		StatsUS.save(statsList);
		
		logger.info("STOP");
	}
}
