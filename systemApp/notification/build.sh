#!/bin/bash

bfs_id="HE74YAAL"

echo "start build app: $bfs_id"

echo "编译后端"

deno task build

mkdir -p ./dist

check_results=`bfsa -V`

if [[ $check_results =~ "0." ]]
then
    bfsa bundle -b ./.npm/ -i $bfs_id
    rm -rf ./dist/*
    tar -xvf HE74YAAL.bfsa -C ./dist
else
    echo "you need install @bfsx/bundle run  npm install -g @bfsx/bundle ,then try again."
fi

mv HE74YAAL.bfsa ./dist && mv appversion.json ./dist


