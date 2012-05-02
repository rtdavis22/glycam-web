JSDOC_ROOT = ../deps/jsdoc-toolkit

# TODO: add the template files as dependencies
all: gems.js
	java -Djsdoc.template.dir=./template -jar $(JSDOC_ROOT)/jsrun.jar $(JSDOC_ROOT)/app/run.js -d=../../projects/gems/docs -s -t=template $<

clean:
	rm -rf files.html index.html symbols
