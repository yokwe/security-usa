package yokwe.security.usa.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import yokwe.security.usa.data.Price;
import yokwe.security.usa.data.StatsUS;
import yokwe.security.usa.data.Stock;
import yokwe.util.DoubleUtil;
import yokwe.util.Market;
import yokwe.util.stats.DoubleArray;
import yokwe.util.stats.DoubleStreamUtil;
import yokwe.util.stats.HV;
import yokwe.util.stats.MA;
import yokwe.util.stats.RSI;

public class UpdateStatsUS {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdatePrice.class);

	private static List<StatsUS> getStatsList(Set<String> dateSet) {
		List<StatsUS> statsList = new ArrayList<>();
		for(Stock stock: Stock.getList()) {
			List<Price> priceList = Price.getList(stock.stockCode).stream().filter(o -> dateSet.contains(o.date)).collect(Collectors.toList());
			if (priceList.isEmpty()) continue;

			int pricec = priceList.size();
			double[] closeArray  = priceList.stream().mapToDouble(o -> o.close).toArray();
			double[] volumeArray = priceList.stream().mapToDouble(o -> o.volume).toArray();
			
			Price price = priceList.get(pricec - 1);
			StatsUS statsUS = new StatsUS();
			
			statsUS.stockCode = stock.stockCode;
			statsUS.type      = stock.type.toString();
			statsUS.name      = stock.name;
			statsUS.date      = stock.date;
			
			statsUS.pricec    = pricec;
			statsUS.price     = price.close;
			
			// last
			if (2 <= pricec) {
				Price last = priceList.get(pricec - 2);
				statsUS.last    = last.close;
				statsUS.lastPCT = DoubleUtil.round((statsUS.price - statsUS.last) / statsUS.last, 3) ;
			} else {
				statsUS.last    = -1;
				statsUS.lastPCT = -1;
			}
			
			// stats - sd hv rsi
			if (30 <= pricec) {
				double logReturn[] = DoubleArray.logReturn(closeArray);
				DoubleStreamUtil.Stats stats = new DoubleStreamUtil.Stats(logReturn);
				
				double sd = stats.getStandardDeviation();
				statsUS.sd = Double.isNaN(sd) ? -1 : DoubleUtil.round(sd, 4);

				HV hv = new HV(closeArray);
				statsUS.hv = Double.isNaN(hv.getValue()) ? -1 : DoubleUtil.round(hv.getValue(), 4);
			} else {
				statsUS.sd = -1;
				statsUS.hv = -1;
			}
			if (RSI.DEFAULT_PERIDO <= pricec) {
				RSI rsi = new RSI();
				Arrays.stream(closeArray).forEach(rsi);
				statsUS.rsi = DoubleUtil.round(rsi.getValue(), 1);
			} else {
				statsUS.rsi = -1;
			}
			
			// min max
			statsUS.min       = priceList.stream().mapToDouble(o -> o.low).min().getAsDouble();
			statsUS.max       = priceList.stream().mapToDouble(o -> o.high).max().getAsDouble();
			statsUS.minPCT    = DoubleUtil.round((statsUS.price - statsUS.min) / statsUS.price, 3);
			statsUS.maxPCT    = DoubleUtil.round((statsUS.max - statsUS.price) / statsUS.price, 3);
			
			// volume
			statsUS.vol       = price.volume;
			if (5 <= pricec) {
				MA vol5 = MA.sma(5, volumeArray);
				statsUS.vol5 = (long)vol5.getValue();
			} else {
				statsUS.vol5 = -1;
			}
			if (20 <= pricec) {
				MA vol21 = MA.sma(21, volumeArray);
				statsUS.vol21 = (long)vol21.getValue();
			} else {
				statsUS.vol21 = -1;
			}

			statsList.add(statsUS);
		}
		return statsList;
	}
	private static Set<String> getDateSet() {
		Set<String> dateSet = new TreeSet<>();
		{
			LocalDate lastDate = Market.getLastTradingDate();
			LocalDate firstDate = lastDate.minusYears(1).plusDays(1);
			while (Market.isClosed(firstDate)) {
				firstDate = firstDate.plusDays(1);
			}
			LocalDate date = firstDate;
			for(;;) {
				if (date.isAfter(lastDate)) break;
				dateSet.add(date.toString());
				
				date = date.plusDays(1);
				while (Market.isClosed(date)) {
					date = date.plusDays(1);
				}
			}
		}
		return dateSet;
	}
	public static void main(String[] args) {
		logger.info("START");
		
		Set<String> dateSet = getDateSet();
		logger.info("date {}  {} - {}", dateSet.size(), dateSet.stream().min(String::compareTo).get(), dateSet.stream().max(String::compareTo).get());
		
//		int dateCount = dateSet.size();
		List<StatsUS> statsList = getStatsList(dateSet);
		
		logger.info("save {} {}", statsList.size(), StatsUS.PATH_FILE);
		StatsUS.save(statsList);
		
		logger.info("STOP");
	}
}
