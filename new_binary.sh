#!/bin/bash
# Copyright 2019 The Hush developers
# Released under the GPLv3 license

set -e

VERSION=$1
FILENAME="SilentDragonAndroid-$VERSION.apk"

if [ -z $VERSION ]; then
    echo "# Must set version number!"
    echo "Usage: $0 1.2.3"
    exit 1
fi

./gradlew clean assembleRelease
#mv $FILENAME $FILENAME.back.$$
cp app/build/outputs/apk/release/app-release.apk SilentDragonAndroid-$VERSION.apk
sha256sum $FILENAME
