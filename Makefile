##############################################################################
# Makefile for the tammymakesthings.com blog
#
# This is a shortcut for running the leiningen targets.
##############################################################################

LEIN="$(HOME)/bin/lein"
@$(GIT)="/usr/bin/git"

SRC_FILES=$(wildcard src/tammymakesthings/*.clj)
SPEC_FILES=$(wildcard spec/tammymakesthings/*.clj)
CONFIG_FILES=project.clj content/config.edn
CONTENT_FILES=$(shell find content/md -type f -print)

post : ;
	@$(LEIN) run new-post

page : ;
	@$(LEIN) run new-page

project : ;
	@$(LEIN) run new-project

build : ;
	@$(LEIN) run build

version : ;
	@$(LEIN) run tool-version

@$(GIT)snap : $(CONTENT_FILES) ;
	@$(GIT) add content
	@$(GIT) commit -m "Snapshot commit at `date`"

repl : $(SRC_FILES) $(SPEC_FILES) $(CONFIG_FILES) ;
	@$(LEIN) repl

spec : $(SRC_FILES) $(SPEC_FILES) $(CONFIG_FILES) ;
	@$(LEIN) spec

speca : $(SRC_FILES) $(SPEC_FILES) $(CONFIG_FILES) ;
	@$(LEIN) spec -a

help : ;
	@echo ""
	@echo "******************************************************************************"
	@echo "*    tammymakesthings.com cryogen helper v0.2 - tammymakesthings@gmail.com   *"
	@echo "******************************************************************************"
	@echo ""
	@echo "Makefile commands:"
	@echo ""
	@echo "    make post     Create a new post in content/md/posts"
	@echo "    make page     Create a new page in content/md/pages"
	@echo "    make project  Create a new project in content/md/pages/projects"
	@echo "    make build    Rebuild the static site from the cryogen tree"
	@echo "    make version  Display the tool version string."
	@echo "    make gitsnap  Take a git snapshot commit of the content repo"
	@echo ""
	@echo "Development targets:"
	@echo ""
	@echo "    make repl     Launch the leiningen REPL"
	@echo "    make spec     Run the speclj test suite once"
	@echo "    make speca    Run the speclj test watcher"
	@echo ""


