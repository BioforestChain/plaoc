import "./polyfills";
import { enableProdMode } from "@angular/core";
import { platformBrowserDynamic } from "@angular/platform-browser-dynamic";
import "@bfsx/plugin";

import { AppModule } from "./app/app.module";

if (import.meta.env.PROD) {
  enableProdMode();
}

platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch((err) => console.error(err));
