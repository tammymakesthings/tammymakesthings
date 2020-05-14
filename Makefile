##############################################################################
# Makefile for the tammymakesthings.com blog
#
# This is a shortcut for running the leiningen targets.
##############################################################################

LEIN="$(HOME)/bin/lein"
GIT="/usr/bin/git"

SRC_FILES=$(wildcard src/tammymakesthings/*.clj)
SPEC_FILES=$(wildcard spec/tammymakesthings/*.clj)
CONFIG_FILES=project.clj content/config.edn
CONTENT_FILES=$(shell find content/md -type f -print)
DEPLOY_HOST=tmtlab.tammymakesthings.com
DEPLOY_DIR=/var/www/tammymakesthings

changed_files=$(shell git status -s | grep 'content/md' | grep '.md' | cut -d' ' -f2)
changed_all=$(shell git status -s | grep 'content/md' | grep '.md' | cut -d' ' -f2)

default: help

post:
	@$(LEIN) run new-post
	make edit

page:
	@$(LEIN) run new-page
	make edit

project:
	@$(LEIN) run new-project
	make edit

edit: $(changed_files)
	$(EDITOR) $(changed_files)

zapnew: $(changed_files)
	rm $(changed_files)

changed: $(changed_files)
	@if /bin/test "\"$(changed_files)\"" \!\= ""; \
	then \
		for file in $(changed_files); do \
			echo -n \"$${file}\"; \
			echo -n ' '; \
		done; \
	fi

build:
	@$(LEIN) run build

publish: build

version:
	@$(LEIN) run tool-version

gitadd:
	@if /bin/test "\"$(changed_files)\"" \!\= ""; \
	then \
		for file in $(changed_files); do \
			echo "* Adding $${file} to git repo"; \
			$(GIT) add $${file}; \
			$(GIT) commit -m "Added $${file} (from 'make add')"; \
		done; \
	fi
	@if /bin/test "\"$(changed_files)\"" \!\= "\"$(changed_all)\""; \
	then \
		echo "* Adding additional content to git repo"; \
		$(GIT) commit -a -m "Added additional content resources (from 'make add')"; \
	fi

publish-remote: gitadd
	git push
	ssh $(DEPLOY_HOST) "cd ~/blog ; git pull ; lein run build"

gitsnap: $(CONTENT_FILES)
	@$(GIT) add content
	@$(GIT) commit -m "Snapshot commit at `date`"

repl:
	@$(LEIN) repl

spec:
	@$(LEIN) spec

speca:
	@$(LEIN) spec -a

clean:
	rm -fr $(DEPLOY_DIR)

help:
	@echo ""
	@echo "\e[31m\e[1m******************************************************************************\e[0m"
	@echo "\e[31m\e[1m*    tammymakesthings.com cryogen helper v0.2 - tammymakesthings@gmail.com   *\e[0m"
	@echo "\e[31m\e[1m******************************************************************************\e[0m"
	@echo ""
	@echo "\e[36m\e[4mContent Generation:\e[0m"
	@echo ""
	@echo "    make \e[1mpost\e[0m     Create a new post in \e[92mcontent/md/posts\e[0m"
	@echo "    make \e[1mpage\e[0m     Create a new page in \e[92mcontent/md/pages\e[0m"
	@echo "    make \e[1mproject\e[0m  Create a new project in \e[92mcontent/md/pages/projects\e[0m"
	@echo ""
	@echo "\e[35m\e[4mContent Management:\e[0m"
	@echo ""
	@echo "    make \e[1medit\e[0m     Open new content files with \e[92m$(shell basename $(EDITOR))\e[0m"
	@echo "    make \e[1mzapnew\e[0m   Delete new posts/pages/projects from \e[92mcontent/md/\e[0m"
	@echo "    make \e[1mchanged\e[0m  Print paths of new content from \e[92mcontent/md/\e[0m"
	@echo "    make \e[1mbuild\e[0m    Rebuild the static site from the cryogen tree"
	@echo "                  (\e[1mmake publish\e[0m is a synonym for \e[1mmake build \e[0m)"
	@echo "    make \e[1mclean\e[0m    Erase the Website deployment tree \e[92m$(DEPLOY_DIR)\e[0m"
	@echo "    make \e[1mgitadd\e[0m   Add new content in \e[92mcontent/md/\e[0m to the repo"
	@echo "    make \e[1mgitsnap\e[0m  Take a git snapshot commit of the content tree"
	@echo ""
	@echo "\e[33m\e[4mOther Targets:\e[0m"
	@echo ""
	@echo "    make \e[1mversion\e[0m  Display the tool version string."
	@echo ""
	@echo "\e[32m\e[4mDevelopment targets:\e[0m"
	@echo ""
	@echo "    make \e[1mrepl\e[0m     Run the \e[94mleiningen\e[0m REPL"
	@echo "    make \e[1mspec\e[0m     Run the \e[94mspeclj\e[0m test suite once"
	@echo "    make \e[1mspeca\e[0m    Run the \e[94mspeclj\e[0m test watcher"
	@echo ""

.PHONY: post page project build version gitsnap repl spec speca zapnew channged edit clean build


