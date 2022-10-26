import inquirer from "inquirer";
import * as fs from "node_fs";
import * as process from "node_process";
import { path } from "path";
import { genBfsAppId, checkSign } from "check";

import type { IProblemConfig } from "../types/problem.type.ts";

const { existsSync } = fs;

/**
 * 打包项目问题流程
 * @returns
 */
export async function bundleProblemsFlow(): Promise<IProblemConfig> {
  const selectAnswer = await inquirer.prompt([
    {
      type: "list",
      name: "appType",
      message: "请选择APP类型",
      default: 0,
      choices: [
        {
          key: "GUI",
          name: "图形化APP",
          value: 0,
        },
        {
          key: "TUI",
          name: "终端APP",
          value: 1,
        },
      ],
    },
  ]);

  let frontPath = "";
  if (selectAnswer.appType === 0) {
    frontPath = await promptInputFrontPath();
  }

  const backPath = await promptInputBackPath();
  const bfsAppId = await promptBfsAppId();

  return {
    frontPath,
    backPath,
    bfsAppId,
  };
}

/**
 * 交互获取前端项目地址
 * @returns
 */
async function promptInputFrontPath() {
  let frontPath = "";
  do {
    const frontAnswer = await inquirer.prompt([
      {
        type: "input",
        name: "frontPath",
        message: `请输入前端项目地址: `,
      },
    ]);

    if (!frontAnswer.frontPath) {
      console.error(`未输入前端项目地址`);
      continue;
    }

    const _frontPath = path.resolve(process.cwd(), frontAnswer.frontPath);
    if (!existsSync(_frontPath)) {
      console.error(`输入的前端项目地址未找到: ${_frontPath}`);
      continue;
    }

    frontPath = _frontPath;
  } while (!frontPath);

  return frontPath;
}

/**
 * 交互获取后端项目地址
 * @returns
 */
async function promptInputBackPath() {
  let backPath = "";
  do {
    const backAnswer = await inquirer.prompt([
      {
        type: "input",
        name: "backPath",
        message: `请输入后端项目地址: `,
      },
    ]);

    if (!backAnswer.backPath) {
      console.error(`未输入后端项目地址`);
      continue;
    }

    const _backPath = path.resolve(process.cwd(), backAnswer.backPath);
    if (!existsSync(_backPath)) {
      console.error(`输入的后端项目地址未找到: ${_backPath}`);
      continue;
    }

    backPath = _backPath;
  } while (!backPath);

  return backPath;
}

/**
 * 交互获取bfsAppId
 * @returns
 */
async function promptBfsAppId() {
  const confirmAnswer = await inquirer.prompt([
    {
      type: "confirm",
      name: "result",
      message: "是否是新bfsApp?",
      default: true,
    },
  ]);

  let bfsAppId = "";
  if (!confirmAnswer.result) {
    do {
      const appIdAnswer = await inquirer.prompt([
        {
          type: "input",
          name: "bfsAppId",
          message: "请输入已存在项目bfsAppId: ",
        },
      ]);

      if (appIdAnswer.bfsAppId) {
        // 校验bfsAppId是否合法
        const suc = await checkSign(appIdAnswer.bfsAppId);

        if (!suc) {
          console.error("bfsAppId不合法，请输入正确的bfsAppId");
          continue;
        }

        bfsAppId = appIdAnswer.bfsAppId;
      } else {
        console.error("未输入bfsAppId");
        continue;
      }
    } while (!bfsAppId);
  } else {
    bfsAppId = await genBfsAppId();
  }

  return bfsAppId;
}
