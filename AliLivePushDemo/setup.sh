#! /bin/sh
#
# setup.sh
#
# Created by keria on 2022/05/30
# Copyright (C) 2022 baorunchen <runchen.brc@alibaba-inc.com>
#

log() {
  echo "keria [$(date +'%Y/%m/%d %H:%M:%S')]: ${1}"
}

prepare_code_repo() {
  log 'prepare code repo...'

  # MTL do submodule initial automatically
  git submodule init && git submodule update

  prepare_aio_code_repo

  log 'prepare code repo over!'
}

# Here we need to import aio common modules
prepare_aio_code_repo() {
  log 'prepare aio code repo...'
  # Attention!!!
  # We've put `AndroidThirdParty` into the gitignore in order to make it always the latest,
  # so, we need to `git pull` the latest commits on master branch at the beginning.
  git clone --depth 1 --branch master git@gitlab.alibaba-inc.com:CodeBaseOne/AndroidThirdParty.git
  log 'prepare aio code repo over!'
}

# TODO keria: How to make it open-source to make external developers easy access to run???
prepare_code_repo
