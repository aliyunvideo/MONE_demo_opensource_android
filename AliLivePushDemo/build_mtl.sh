#! /bin/sh
#
# build_mtl.sh
#
# Created by keria on 2022/08/30
# Copyright (C) 2022 keria <runchen.brc@alibaba-inc.com>
#

# build live push demo + aio sdk
#./build_mtl.sh -p aio

# build live push demo + live push sdk
#./build_mtl.sh

# build type, build live push demo, interactive push demo or aio demo.
build_type='basic'
#build_type='interactive'
#build_type='aio'

log() {
  echo "keria@197362 [$(date +'%Y/%m/%d %H:%M:%S')]: ${1}"
}

prepare_mtl_build_env() {
  log 'prepare mtl build env...'
  export JAVA_HOME=/usr/alibaba/jdk1.8.0_25
  export PATH=$JAVA_HOME/bin:$PATH
  export ANDROID_NDK_HOME=/home/admin/software/android-ndk-r18b
  log 'prepare mtl build env over!'
}

build_demo() {
  if [ "$build_type" = "aio" ]; then
    build_demo_aio
  elif [ "$build_type" = "interactive" ]; then
    build_demo_interactive
  else
    build_demo_basic
  fi
}

build_demo_with_packaging() {
  log 'package source code into zip...'
  ./gradlew clean -Pczip=1
  ./gradlew assembleRelease --refresh-dependencies --stacktrace --info
}

build_demo_interactive() {
  log 'build interactive demo...'

  # shellcheck disable=SC2039
  if [[ $(uname) == 'Darwin' ]]; then
    sed -i '' 's/allInOne=.*/allInOne=false/g' gradle.properties
    sed -i '' 's/buildInteractive=.*/buildInteractive=true/g' gradle.properties
  fi
  # shellcheck disable=SC2039
  if [[ $(uname) == 'Linux' ]]; then
    sed -i 's/allInOne=.*/allInOne=false/g' gradle.properties
    sed -i 's/buildInteractive=.*/buildInteractive=true/g' gradle.properties
  fi

  build_demo_with_packaging

  log 'build interactive demo over!'
}

build_demo_aio() {
  log 'build aio demo...'

  # shellcheck disable=SC2039
  if [[ $(uname) == 'Darwin' ]]; then
    sed -i '' 's/allInOne=.*/allInOne=true/g' gradle.properties
  fi
  # shellcheck disable=SC2039
  if [[ $(uname) == 'Linux' ]]; then
    sed -i 's/allInOne=.*/allInOne=true/g' gradle.properties
  fi

  build_demo_with_packaging

  log 'build aio demo over!'
}

build_demo_basic() {
  log 'build basic demo...'

  # shellcheck disable=SC2039
  if [[ $(uname) == 'Darwin' ]]; then
    sed -i '' 's/allInOne=.*/allInOne=false/g' gradle.properties
    sed -i '' 's/buildInteractive=.*/buildInteractive=false/g' gradle.properties
  fi
  # shellcheck disable=SC2039
  if [[ $(uname) == 'Linux' ]]; then
    sed -i 's/allInOne=.*/allInOne=false/g' gradle.properties
    sed -i 's/buildInteractive=.*/buildInteractive=false/g' gradle.properties
  fi

  build_demo_with_packaging

  log 'build basic demo over!'
}

start=$(date +%s)
log '*** start mtl build ***'

while getopts 'p:hv' optname; do
  case "$optname" in
  'p')
    log "get option -p, value is $OPTARG"
    build_type="$OPTARG"
    ;;
  'h')
    log 'build live push demo + aio sdk eg: ./build_mtl.sh -p aio'
    log 'build live push demo + interactive live push sdk eg: ./build_mtl.sh -p interactive'
    log 'build live push demo + live push sdk eg: ./build_mtl.sh'
    ;;
  'v')
    # shellcheck disable=SC2046
    log "current version is "$(git rev-parse --short HEAD)
    ;;
  ?)
    log 'Unknown error while processing options'
    ;;
  esac
done

prepare_mtl_build_env || exit
build_demo || exit 1

end=$(date +%s)
time=$(echo "$start" "$end" | awk '{print $2-$1}')

log "*** end mtl build, cost: ${time}s. ***"
