import builder from "#builder";
import { defineHtmlElement } from "./defineHtmlElement";

export const HTMLDWebNavigatorElement = defineHtmlElement(
  { tagName: "DWebNavigator", tag_name: "dweb-navigator" },
  builder(),
  {
    onCreate() {},
  }
);
