import { Directive, Input, ElementRef } from "@angular/core";

import { Keyboard } from "./bfsxPluginType";

@Directive({ selector: "dweb-keyboard" })
export class DwebKeyboardDirective {
  protected element: Keyboard.DwebKeyboard;

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
