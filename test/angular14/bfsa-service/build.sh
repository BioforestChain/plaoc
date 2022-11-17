#!/bin/bash

bfs_id="6FTMG2WJ"

echo "start build bfsa_service: $bfs_id"

echo "编译后端"

deno task build

check_results=`bfsa -V`

if [[ $check_results =~ "0." ]]
then
    bfsa bundle   -b ./build -i $bfs_id
    rm -rf ./dist/*
    tar -xvf 6FTMG2WJ.bfsa -C ./dist
else
    echo "you need install @bfsx/bundle run  npm install -g @bfsx/bundle ,then try again."
fi

mv 6FTMG2WJ.bfsa ./dist && mv appversion.json ./dist


