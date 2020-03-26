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

save-ods:
	cp ~/Dropbox/Trade/dividend-etf.ods      ~/Dropbox/Trade/SAVE/dividend-etf_$$(date +%Y%m%d).ods
	cp ~/Dropbox/Trade/TEMPLATE_STATS_JP.ods ~/Dropbox/Trade/SAVE/TEMPLATE_STATS_JP_$$(date +%Y%m%d).ods

