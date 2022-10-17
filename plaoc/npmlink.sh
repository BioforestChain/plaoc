#!/bin/bash

# 快速npm link 方便测试

dirctory=("core" "plugin" "metadata" "typings" "vfs" "gateway")

for dir in ${dirctory[@]}
do
    cd ./build/$dir/ && pwd && npm link && cd ../../
done


cd ../test/vue3/ && npm link @bfsx/plugin && npm link @bfsx/gateway

cd ./bfsa-service && pwd && npm link @bfsx/core && npm link @bfsx/metadata && npm link @bfsx/typings
