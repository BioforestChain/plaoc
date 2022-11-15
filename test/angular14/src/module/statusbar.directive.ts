import { Directive, Input, ElementRef } from "@angular/core";

import { StatusBar, Color } from "./bfsxPluginType";

@Directive({ selector: "dweb-status-bar" })
export class DwebStatusBarDirective {
  protected element: StatusBar.DwebStatusBar;

  @Input() set "background-color"(value: Color.RGBAHex) {
    this.element["background-color"] = value;
  }

  @Input() set "bar-style"(value: StatusBar.StatusBarStyle) {
    this.element["bar-style"] = value;
  }

  @Input() set overlay(value: string | boolean) {
    this.element.overlay = value === "" ? true : value;
  }

  @Input() set hidden(value: string | boolean) {
    this.element.hidden = value === "" ? true : value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}
