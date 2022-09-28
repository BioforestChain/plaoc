export namespace Runtime {
  type DwebViewId = string;
  type TModule_fn = {
    // deno-lint-ignore ban-types
    [fn: string]: Function;
  };
}
