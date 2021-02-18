#
#
#
SHELL := /bin/bash

all:
	@echo all

clean:
	echo rm -rf tmp/*

#
# misc-lib relate targets
#

# to make project independent from misc-lib, copy files from misc-lib
copy-misc-lib-files:
	cp ../misc-lib/tmp/build/jar/misc-lib.jar data/jar
	cp ../misc-lib/data/jar/*                 data/jar
	cp ../misc-lib/data/market/*              data/market

build-misc-lib:
	pushd ../misc-lib/; ant build ; popd; make copy-misc-lib-files

delete-save:
	find tmp/save -mtime +30 -delete

#
# stock.csv
#
update-stock:
	ant update-stock
	cp -p tmp/data/stock.csv     tmp/save/stock_$$(date +%Y%m%d).csv
	cp -p tmp/data/symbols.csv   tmp/save/symbols_$$(date +%Y%m%d).csv

#
# price
#
update-price:
	ant update-price
	cp -p tmp/data/previous.csv  tmp/save/previous_$$(date +%Y%m%d).csv
	tar cfz tmp/save/price_$$(date +%Y%m%d).taz tmp/data/price


#
# sats-us.csv
#
update-stats-us:
	ant update-stats-us
	cp -p tmp/data/stats-us.csv  tmp/save/stats-us_$$(date +%Y%m%d).csv
	cp -p tmp/data/stats-us.csv  ~/Dropbox/Trade/stats-us.csv

