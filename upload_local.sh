#!/bin/bash

#定义颜色的变量
RED_COLOR="\033[1;31m"  #红
GREEN_COLOR="\033[1;32m" #绿
YELOW_COLOR="\033[1;33m" #黄
BLUE_COLOR="\033[1;34m"  #蓝
PINK="\033[1;35m"    #粉红
RES="\033[0m"

./gradlew clean
./gradlew :plugin-sentry:assemble --stacktrace
./gradlew :hook-sentry:assembleRelease --stacktrace
./gradlew :privacy-annotation:assemble --stacktrace
./gradlew :privacy-proxy:assembleRelease --stacktrace
./gradlew :privacy-replace:assembleRelease --stacktrace
#publish
./gradlew :plugin-sentry:publish --stacktrace
./gradlew :hook-sentry:publish --stacktrace
./gradlew :privacy-annotation:publish --stacktrace
./gradlew :privacy-proxy:publish --stacktrace
./gradlew :privacy-replace:publish --stacktrace
echo -e "${GREEN_COLOR}本地打包完成！！！${RES}"
