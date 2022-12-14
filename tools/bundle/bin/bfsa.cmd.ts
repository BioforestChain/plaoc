import * as process from "node_process";
import { Command } from "commander";
import { bundle } from "cmd_bundle";
import { bundleProblemsFlow } from "problem";
import { checkSign, genBfsAppId } from "check";
import npmConfig from "../npm.json" assert { type: "json" };

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
  .action(async (options: any) => {
    /**
     * bfsAppId存在，则校验，否则生成
     */
    let bfsAppId = "";
    if (options.bfsAppid) {
      // 校验bfsAppId是否合法
      const suc = await checkSign(options.bfsAppid);

      if (!suc) {
        throw new Error("bfsAppId不合法，请输入正确的bfsAppId");
      }

      bfsAppId = options.bfsAppid;
    } else {
      bfsAppId = await genBfsAppId();
    }

    await bundle({
      bfsAppId: bfsAppId,
      frontPath: options.frontPath,
      backPath: options.backPath,
    });
  });

// 使用交互模式
program
  .command("interactive")
  .description("bfsa bundle project to .bfsa by interactive command line")
  .action(async () => {
    const problemConfig = await bundleProblemsFlow();
    await bundle(problemConfig);
  });

program.parse(process.argv);
