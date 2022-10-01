#! /usr/bin/env node

import * as process from "node_process";
import { Command } from "commander";
import { bundle } from "../src/bundle.ts";

const npmConfig = (await import("../npm.json", { assert: { type: "json" } }))
  .default;

const program = new Command();
program
  .name("bfsa")
  .description(npmConfig.description)
  .version(npmConfig.version);

// program
//   .command("install")
//   .description("bfsa install service")
//   .action(() => {});

// 打包application为.bfsa到指定位置
program
  .command("bundle")
  .description("bfsa bundle project to .bfsa")
  .option("-f, --front-path <type>", "frontend application path.")
  .option("-b, --back-path <type>", "backend application path.")
  // .option("-d, --dist-path <type>", "publish .bfsa to path")
  .action((options: any) => {
    bundle({
      bfsAppId: "111",
      frontPath: options.frontPath,
      backPath: options.backPath,
    });
  });

// 设置version指令
program.version(npmConfig.version);

program.parse(process.argv);
