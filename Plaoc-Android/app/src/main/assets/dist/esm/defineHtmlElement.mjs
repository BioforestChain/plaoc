const defineHtmlElement = (names, apis, lifecycle) => {
  const evt_hubs = /* @__PURE__ */ new Set();
  const func_name_list = [];
  const readonly_name_list = [];
  const prop_name_list = [];
  const propDescMap = /* @__PURE__ */ new Map();
  let value = apis;
  do {
    for (const [prop, desc] of Object.entries(Object.getOwnPropertyDescriptors(value))) {
      if (prop.startsWith("_")) {
        continue;
      }
      if (propDescMap.has(prop)) {
        continue;
      }
      propDescMap.set(prop, desc);
    }
    value = Object.getPrototypeOf(apis);
    if (value === Object.prototype) {
      break;
    }
  } while (true);
  const handlePropDesc = (prop, desc) => {
    if (prop.startsWith("on") && typeof desc.get === "function") {
      prop.substring(2).toLowerCase();
    } else if (typeof desc.value === "function") {
      func_name_list.push(prop);
    } else if ("value" in desc) {
      prop_name_list.push(prop);
    } else if ("set" in desc) {
      if (desc.set === void 0) {
        readonly_name_list.push(prop);
      } else {
        prop_name_list.push(prop);
      }
    }
  };
  for (const [prop, desc] of propDescMap) {
    handlePropDesc(prop, desc);
  }
  const HTMLContructor = Function("apis", "evt_hubs", "lifecycle", `return class HTML${names.tagName} extends HTMLElement {
        constructor(){
            super()
            lifecycle.onCreate && lifecycle.onCreate(this)
        }
        disconnctedCallback() {
          for (const evt of evt_hubs) {
            evt.return();
          }
        }
        ${func_name_list.map((func_name) => `${func_name}(...args) { return apis.${func_name}(...args) }`).join("\n")}
        ${readonly_name_list.map((readonly_name) => `get ${readonly_name}() { return apis.${readonly_name} }`).join("\n")}
        ${prop_name_list.map((prop_name) => `get ${prop_name}() { return apis.${prop_name} }
set ${prop_name}(v) { return apis.${prop_name}(v) }`).join("\n")}
      }`)(apis, evt_hubs);
  customElements.define(names.tag_name, HTMLContructor);
  return HTMLContructor;
};
export { defineHtmlElement };
//# sourceMappingURL=defineHtmlElement.mjs.map
