import { Directive, Input, ElementRef } from "@angular/core";

import { BottomBar, Color, Icon } from "./bfsxPluginType";

@Directive({ selector: "dweb-bottom-bar" })
export class DwebBottomBarDirective {
  protected element: BottomBar.DwebBottomBar;

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

  @Input() set height(value: string) {
    this.element.height = value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}

@Directive({ selector: "dweb-bottom-bar-button" })
export class DwebBottomBarButtonDirective {
  protected element: BottomBar.DwebBottomBarButton;

  @Input() set disabled(value: string | boolean) {
    this.element.disabled = value === "" ? true : value;
  }

  @Input() set selected(value: string | boolean) {
    this.element.selected = value === "" ? true : value;
  }

  @Input() set diSelectable(value: string | boolean) {
    this.element.diSelectable = value === "" ? true : value;
  }

  @Input() set "indicator-color"(value: Color.RGBAHex) {
    this.element["indicator-color"] = value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}

@Directive({ selector: "dweb-bottom-bar-icon" })
export class DwebBottomBarIconDirective {
  protected element: BottomBar.DwebBottomBarIcon;

  @Input() set source(value: string) {
    this.element.source = value;
  }

  @Input() set type(value: Icon.IconType) {
    this.element.type = value;
  }

  @Input() set description(value: string) {
    this.element.description = value;
  }

  @Input() set size(value: string) {
    this.element.size = value;
  }

  @Input() set color(value: Color.RGBAHex) {
    this.element.color = value;
  }

  @Input() set "selected-color"(value: Color.RGBAHex) {
    this.element["selected-color"] = value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}

@Directive({ selector: "dweb-bottom-bar-text" })
export class DwebBottomBarTextDirective {
  protected element: BottomBar.DwebBottomBarText;

  @Input() set value(value: string) {
    this.element.value = value;
  }

  @Input() set color(value: Color.RGBAHex) {
    this.element.color = value;
  }

  @Input() set "selected-color"(value: Color.RGBAHex) {
    this.element["selected-color"] = value;
  }

  @Input() set "hide-value"(value: Color.RGBAHex) {
    this.element["hide-value"] = value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}
