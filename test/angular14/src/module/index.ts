import { NgModule } from "@angular/core";
import {
  DwebTopBarDirective,
  DwebTopBarButtonDirective,
} from "./topbar.directive";
import {
  DwebBottomBarDirective,
  DwebBottomBarButtonDirective,
  DwebBottomBarIconDirective,
  DwebBottomBarTextDirective,
} from "./bottombar.directive";
import { DwebStatusBarDirective } from "./statusbar.directive";
import {
  DwebDialogAlertDirective,
  DwebDialogPromptDirective,
  DwebDialogConfirmDirective,
  DwebDialogWarningDirective,
} from "./dialog.directive";
import { DwebKeyboardDirective } from "./keyboard.directive";
import { DwebIconDirective } from "./icon.directive";

const directives = [
  DwebTopBarDirective,
  DwebTopBarButtonDirective,
  DwebBottomBarDirective,
  DwebBottomBarButtonDirective,
  DwebBottomBarIconDirective,
  DwebBottomBarTextDirective,
  DwebStatusBarDirective,
  DwebDialogAlertDirective,
  DwebDialogPromptDirective,
  DwebDialogConfirmDirective,
  DwebDialogWarningDirective,
  DwebKeyboardDirective,
  DwebIconDirective,
];

@NgModule({
  declarations: [...directives],
  exports: [...directives],
})
export class BfsxPluginModule {}
