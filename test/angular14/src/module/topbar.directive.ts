import { Directive, Input, ElementRef } from "@angular/core";

import { TopBar, Color } from "./bfsxPluginType";

@Directive({ selector: "dweb-top-bar" })
export class DwebTopBarDirective {
  protected element: TopBar.DwebTopBar;

  @Input() set title(value: string) {
    this.element.title = value;
  }

  @Input() set "background-color"(value: Color.RGBAHex) {
    this.element["background-color"] = value;
  }

  @Input() set "foreground-color"(value: Color.RGBAHex) {
    this.element["foreground-color"] = value;
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

@Directive({ selector: "dweb-top-bar-button" })
export class DwebTopBarButtonDirective {
  protected element: TopBar.DwebTopBarButton;

  @Input() set disabled(value: string | boolean) {
    this.element.disabled = value === "" ? true : value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}
