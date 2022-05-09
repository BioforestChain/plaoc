import { Evt } from "./EvtOut";

export const defineHtmlElement = <T extends {}>(
  names: { tag_name: string; tagName: string },
  apis: T,
  lifecycle: { onCreate: () => unknown }
) => {
  const evt_hubs = new Set<Evt>();
  const func_name_list: string[] = [];
  const readonly_name_list: string[] = [];
  const prop_name_list: string[] = [];
  const define_list: [p: PropertyKey, attributes: PropertyDescriptor][] = [];

  /// 获取所有的属性
  const propDescMap = new Map<string, PropertyDescriptor>();
  let value: any = apis;
  do {
    for (const [prop, desc] of Object.entries(
      Object.getOwnPropertyDescriptors(value)
    )) {
      /// 忽略私有的
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

  /// 遍历所有的属性，进行动态生产
  const handlePropDesc = (prop: string, desc: PropertyDescriptor) => {
    if (prop.startsWith("on") && typeof desc.get === "function") {
      const eventName = prop.substring(2).toLowerCase();

      let on_event: Function | null = null;
      let on_evt: Evt | undefined;
      define_list.push([
        `on${eventName}`,
        {
          get() {
            return on_event || null;
          },
          set(cb: Function) {
            if (typeof cb === "function") {
              on_event = cb;
              if (on_evt === undefined) {
                (async () => {
                  const evt = (on_evt = desc.get!.call(apis) as unknown as Evt);
                  evt_hubs.add(evt);
                  for await (const data of evt) {
                    const event = new CustomEvent(eventName, { detail: data });
                    on_event?.(event);
                  }
                })();
              }
            } else {
              on_event = null;
              if (on_evt !== undefined) {
                on_evt.return();
                evt_hubs.delete(on_evt);
              }
            }
          },
        },
      ]);
    } else if (typeof desc.value === "function") {
      func_name_list.push(prop);
    } else if ("value" in desc) {
      prop_name_list.push(prop);
    } else if ("set" in desc) {
      if (desc.set === undefined) {
        readonly_name_list.push(prop);
      } else {
        prop_name_list.push(prop);
      }
    }
  };
  for (const [prop, desc] of propDescMap) {
    handlePropDesc(prop, desc);
  }

  /// build html elemnet constructor
  const HTMLContructor = Function(
    "apis",
    "evt_hubs",
    "lifecycle",
    `return class HTML${names.tagName} extends HTMLElement {
        constructor(){
            super()
            lifecycle.onCreate && lifecycle.onCreate(this)
        }
        disconnctedCallback() {
          for (const evt of evt_hubs) {
            evt.return();
          }
        }
        ${func_name_list
          .map(
            (func_name) =>
              `${func_name}(...args) { return apis.${func_name}(...args) }`
          )
          .join("\n")}
        ${readonly_name_list
          .map(
            (readonly_name) =>
              `get ${readonly_name}() { return apis.${readonly_name} }`
          )
          .join("\n")}
        ${prop_name_list
          .map(
            (prop_name) =>
              `get ${prop_name}() { return apis.${prop_name} }\n` +
              `set ${prop_name}(v) { return apis.${prop_name}(v) }`
          )
          .join("\n")}
      }`
  )(apis, evt_hubs) as typeof HTMLElement;
  customElements.define(names.tag_name, HTMLContructor);
  return HTMLContructor;
};
