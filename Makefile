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


#
# stock.csv
#
update-stock:
ifneq (,$(wildcard tmp/data/stock.csv))
	rm -f tmp/data/stock-OLD.csv
	cp -p tmp/data/stock.csv     tmp/data/stock-OLD.csv
endif
	ant update-stock


#
# price
#
update-price:
ifneq (,$(wildcard tmp/data/price))
	rm -rf tmp/data/price-OLD.csv
	cp -rp tmp/data/price        tmp/data/price-OLD
endif
	ant update-price


#
# sats-us.csv
#
update-stats-us:
ifneq (,$(wildcard tmp/data/stats-us.csv))
	rm -f tmp/data/stats-us-OLD.csv
	cp -p tmp/data/stats-us.csv     tmp/data/stats-us-OLD.csv
endif
	ant update-stats-us
	cp tmp/data/stats-us.csv ~/Dropbox/Trade/stats-us.csv

