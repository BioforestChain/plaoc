#!/bin/bash

bfs_id="KEJPMHLA"

echo "start build app: $bfs_id"

echo "编译后端"

cd bfsa-service && deno task build && cd ..

echo "编译前端"

yarn build

check_results=`bfsa -V`

if [[ $check_results =~ "0." ]]
then
    bfsa bundle -f ./build/  -b ./bfsa-service/dist/ -i $bfs_id
    rm -rf ./dist/*
    tar -xvf KEJPMHLA.bfsa -C ./dist
else
    echo "you need bundle @bfsx/bundle run  npm install -g @bfsx/bundle ,then try again."
fi

mv KEJPMHLA.bfsa ./dist && mv appversion.json ./dist


