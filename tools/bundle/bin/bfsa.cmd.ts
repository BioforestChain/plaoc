#! /usr/bin/env node

import * as process from "node_process";
import { Command } from "commander";
import { bundle } from "cmd_bundle";

const npmConfig = (await import("../npm.json", { assert: { type: "json" } }))
  .default;

const program = new Command();
program
  .name("bfsa")
  .description(npmConfig.description)
  .version(npmConfig.version);

// 打包application为.bfsa到指定位置
program
  .command("bundle")
  .description("bfsa bundle project to .bfsa")
  // 无界面应用必须包含后端
  .requiredOption("-b, --back-path <string>", "backend application path.")
  .option("-f, --front-path <string>", "frontend application path.")
  .option(
    "-i, --bfs-appid <string>",
    "bfsAppId: app unique identification，new app ignore."
  )
  .action((options: any) => {
    bundle({
      bfsAppId: options.bfsAppid,
      frontPath: options.frontPath,
      backPath: options.backPath,
    });
  });

program.parse(process.argv);
