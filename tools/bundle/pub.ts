export const doPub = async (cwd: string) => {
  const p = Deno.run({
    cwd,
    cmd: ["npm", "publish", "--access", "public"],
    stdout: "inherit",
    stderr: "inherit",
    stdin: "inherit",
  });
  const status = await p.status();
  p.close();
  console.log(status);
  return status.success;
};

export const doPubFromJson = async (inputConfigFile: string) => {
  const npmConfigs = (
    await import(inputConfigFile, { assert: { type: "json" } })
  ).default;

  await doPub(npmConfigs.outDir);
};

if (import.meta.main) {
  await doPubFromJson(import.meta.resolve("./npm.json"));
}
