import { Directive, Input, ElementRef } from "@angular/core";

import { Dialogs } from "./bfsxPluginType";

@Directive({ selector: "dweb-dialog-alert" })
export class DwebDialogAlertDirective {
  protected element: Dialogs.DwebDialogsAlert;

  @Input() set visible(value: string | boolean) {
    this.element.visible = value === "" ? true : value;
  }

  @Input() set title(value: string) {
    this.element.title = value;
  }

  @Input() set content(value: string) {
    this.element.content = value;
  }

  @Input() set disOnBackPress(value: string | boolean) {
    this.element.disOnBackPress = value === "" ? true : value;
  }

  @Input() set disOnClickOutside(value: string | boolean) {
    this.element.disOnClickOutside = value === "" ? true : value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}

@Directive({ selector: "dweb-dialog-prompt" })
export class DwebDialogPromptDirective {
  protected element: Dialogs.DwebDialogsPrompt;

  @Input() set visible(value: string | boolean) {
    this.element.visible = value === "" ? true : value;
  }

  @Input() set title(value: string) {
    this.element.title = value;
  }

  @Input() set label(value: string) {
    this.element.label = value;
  }

  @Input() set defaultValue(value: string) {
    this.element.defaultValue = value;
  }

  @Input() set disOnBackPress(value: string | boolean) {
    this.element.disOnBackPress = value === "" ? true : value;
  }

  @Input() set disOnClickOutside(value: string | boolean) {
    this.element.disOnClickOutside = value === "" ? true : value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}

@Directive({ selector: "dweb-dialog-confirm" })
export class DwebDialogConfirmDirective {
  protected element: Dialogs.DwebDialogsConfirm;

  @Input() set visible(value: string | boolean) {
    this.element.visible = value === "" ? true : value;
  }

  @Input() set title(value: string) {
    this.element.title = value;
  }

  @Input() set message(value: string) {
    this.element.message = value;
  }

  @Input() set disOnBackPress(value: string | boolean) {
    this.element.disOnBackPress = value === "" ? true : value;
  }

  @Input() set disOnClickOutside(value: string | boolean) {
    this.element.disOnClickOutside = value === "" ? true : value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}

@Directive({ selector: "dweb-dialog-warning" })
export class DwebDialogWarningDirective {
  protected element: Dialogs.DwebDialogsConfirm;

  @Input() set visible(value: string | boolean) {
    this.element.visible = value === "" ? true : value;
  }

  @Input() set title(value: string) {
    this.element.title = value;
  }

  @Input() set message(value: string) {
    this.element.message = value;
  }

  @Input() set disOnBackPress(value: string | boolean) {
    this.element.disOnBackPress = value === "" ? true : value;
  }

  @Input() set disOnClickOutside(value: string | boolean) {
    this.element.disOnClickOutside = value === "" ? true : value;
  }

  constructor(elementRef: ElementRef) {
    this.element = elementRef.nativeElement;
  }
}
