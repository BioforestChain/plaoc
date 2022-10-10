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
    tar -cf KEJPMHLA.bfsa ./dist
else
    echo "you need install @bfsx/install run  npm install -g @bfsx/install ,then try again."
fi

mv KEJPMHLA.bfsa ./dist && mv appversion.json ./dist


