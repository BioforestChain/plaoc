import { build, emptyDir } from "dnt";

export const doBuild = async (npmFile: string) => {
  const config = (await import(npmFile, { assert: { type: "json" } })).default;
  const { buildToRootDir, importMap, name, version, buildFromRootDir } = config;

  await emptyDir(buildToRootDir);

  await build({
    importMap: importMap,
    entryPoints: [
      {
        kind: "export",
        name: ".",
        path: "./index.ts",
      },
    ],
    outDir: buildToRootDir,
    scriptModule: false,
    /**
     * @TODO should ignore errors:
     * 1. TS2691
     */
    typeCheck: true,
    shims: {
      // see JS docs for overview and more options
      deno: "dev",
    },
    compilerOptions: {
      target: "Latest",
      importHelpers: true,
      isolatedModules: true,
      experimentalDecorators: true,
      emitDecoratorMetadata: true,
    },
    packageManager: "yarn",
    package: {
      // package.json properties
      name: name,
      version: version,
      description: `plaoc system runtime`,
      license: "MIT",
      repository: {
        type: "git",
        url: "git+https://github.com/BioforestChain/plaoc.git",
      },
      bugs: {
        url: "https://github.com/BioforestChain/plaoc/issues",
      },
      dependencies: {
        "@bfsx/core": "^0.0.7",
        "@bfsx/metadata": "^0.0.7",
        "@bfsx/gateway": "^0.0.7",
      },
      devDependencies: {
        "@bfsx/typings": "^0.0.7",
      },
    },
  });

  // post build steps
  for (const base of ["README.md", "LICENSE"]) {
    const fromFilename = `${buildFromRootDir}/${base}`;
    const toFilename = `${buildToRootDir}/${base}`;
    try {
      Deno.copyFileSync(fromFilename, toFilename);
    } catch (err) {
      if (err instanceof Deno.errors.NotFound === false) {
        throw err;
      }
    }
  }
};

if (import.meta.main) {
  await doBuild(import.meta.resolve("./npm.json"));
}
