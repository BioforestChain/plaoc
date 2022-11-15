import { Directive, Input, ElementRef } from "@angular/core";

import { Icon } from "./bfsxPluginType";

@Directive({ selector: "dweb-icon" })
export class DwebIconDirective {
  protected element: Icon.IPlaocIcon;

  @Input() set source(value: string) {
    this.element.source = value;
  }

  @Input() set description(value: string) {
    this.element.description = value;
  }

  @Input() set type(value: Icon.IconType) {
    this.element.type = value;
  }

  @Input() set size(value: number) {
    this.element.size = value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}
