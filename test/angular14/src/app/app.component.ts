import { Component, OnInit } from "@angular/core";
import { BfcsTopBar, OpenScanner } from "@bfsx/plugin";

@Component({
  selector: "app-root",
  template: `
    <div>
      <a href="https://angular.io/" target="_blank">
        <img
          alt="Angular Logo"
          class="logo angular"
          src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNTAgMjUwIj4KICAgIDxwYXRoIGZpbGw9IiNERDAwMzEiIGQ9Ik0xMjUgMzBMMzEuOSA2My4ybDE0LjIgMTIzLjFMMTI1IDIzMGw3OC45LTQzLjcgMTQuMi0xMjMuMXoiIC8+CiAgICA8cGF0aCBmaWxsPSIjQzMwMDJGIiBkPSJNMTI1IDMwdjIyLjItLjFWMjMwbDc4LjktNDMuNyAxNC4yLTEyMy4xTDEyNSAzMHoiIC8+CiAgICA8cGF0aCAgZmlsbD0iI0ZGRkZGRiIgZD0iTTEyNSA1Mi4xTDY2LjggMTgyLjZoMjEuN2wxMS43LTI5LjJoNDkuNGwxMS43IDI5LjJIMTgzTDEyNSA1Mi4xem0xNyA4My4zaC0zNGwxNy00MC45IDE3IDQwLjl6IiAvPgogIDwvc3ZnPg=="
        />
      </a>
    </div>

    <h1>Vite + Angular</h1>

    <div class="card">
      <button type="button" (click)="increment()">Count {{ count }}</button>
    </div>

    <p>
      Check out
      <a href="https://github.com/analogjs/analog#readme" target="_blank"
        >Analog</a
      >, the fullstack meta-framework for Angular powered by Vite!
    </p>

    <p class="read-the-docs">
      Click on the Vite and Angular logos to learn more.
    </p>

    <dweb-top-bar
      id="topbar"
      title="Ar angular 扫雷"
      background-color="#eee"
      foreground-color="#000"
      overlay="0.4"
    >
      <dweb-top-bar-button id="aaa" disabled>
        <dweb-icon source="Filled.AddCircle"></dweb-icon>
      </dweb-top-bar-button>
      <dweb-top-bar-button id="ccc">
        <dweb-icon
          source="https://objectjson.waterbang.top/test-vue3/vite.svg"
          type="AssetIcon"
        ></dweb-icon>
      </dweb-top-bar-button>
    </dweb-top-bar>
    <dweb-bottom-bar
      id="bottombar"
      background-color="#D0BCFF"
      foreground-color="#1C1B1F"
      height="70"
      overlay="0.2"
    >
      <dweb-bottom-bar-button id="ddd" selected>
        <dweb-bottom-bar-icon
          [source]="bottomBarImg.one"
          type="AssetIcon"
        ></dweb-bottom-bar-icon>
        <dweb-bottom-bar-text
          color="#938F99"
          selected-color="#1C1B1F"
          value="土地"
        ></dweb-bottom-bar-text>
      </dweb-bottom-bar-button>
      <dweb-bottom-bar-button id="eee" diSelectable (click)="openQrScanner()">
        <dweb-bottom-bar-icon
          [source]="bottomBarImg.two"
          type="AssetIcon"
        ></dweb-bottom-bar-icon>
        <dweb-bottom-bar-text value="扫码"></dweb-bottom-bar-text>
      </dweb-bottom-bar-button>
      <dweb-bottom-bar-button id="fff" (click)="onBottomBar('three')">
        <dweb-bottom-bar-icon
          [source]="bottomBarImg.three"
          type="AssetIcon"
        ></dweb-bottom-bar-icon>
        <dweb-bottom-bar-text
          color="#938F99"
          selected-color="#1C1B1F"
          value="个人空间"
        ></dweb-bottom-bar-text>
      </dweb-bottom-bar-button>
    </dweb-bottom-bar>
    <dweb-scanner></dweb-scanner>
    <div class="bottombar"></div>
  `,
  styles: [
    `
      :host {
        max-width: 1280px;
        margin: 0 auto;
        padding: 2rem;
        text-align: center;
      }

      .logo {
        height: 6em;
        padding: 1.5em;
        will-change: filter;
      }
      .logo:hover {
        filter: drop-shadow(0 0 2em #646cffaa);
      }
      .logo.angular:hover {
        filter: drop-shadow(0 0 2em #42b883aa);
      }
      .read-the-docs {
        color: #888;
      }
    `,
  ],
})
export class AppComponent implements OnInit {
  count = 0;
  scannerData = {
    value: "DwebView-js ♻️ Deno-js",
  };
  bottomBarImg = {
    one: "https://objectjson.waterbang.top/test-vue3/land.svg",
    two: "https://objectjson.waterbang.top/test-vue3/scanner.svg",
    three: "https://objectjson.waterbang.top/test-vue3/home-not.svg",
  };

  async ngOnInit() {
    const topBar = document.querySelector<BfcsTopBar>("dweb-top-bar")!;
    console.log("getTopBarAlpha: ", await topBar.getTopBarAlpha());
    console.log("getTopBarShow: ", await topBar.getTopBarShow());
    console.log("getTopBarTitle: ", await topBar.getTopBarTitle());
    console.log("getTopBarHeight: ", await topBar.getTopBarHeight());
    console.log(
      "getTopBarBackgroundColor: ",
      await topBar.getTopBarBackgroundColor()
    );
    console.log(
      "getTopBarForegroundColor: ",
      await topBar.getTopBarForegroundColor()
    );
    console.log("getTopBarActions: ", await topBar.getTopBarActions());

    this.getBlockInfo();
  }

  async getBlockInfo() {
    fetch("/getBlockInfo", {
      headers: { "Content-type": "application/json" },
    })
      .then((res) => res.json())
      .then(async (response) => {
        console.log("我是getBlockInfo：", JSON.stringify(response));
      })
      .catch((error) => {
        console.log("Looks like there was a problem: \n", error);
      });
  }

  increment() {
    this.count++;
  }

  async openQrScanner() {
    const scanner = document.querySelector<OpenScanner>("dweb-scanner")!;
    const iter = await scanner.openQrCodeScanner();
    console.log(
      "scannerData.value = await scanner.openQrCodeScanner() -->",
      JSON.stringify(iter)
    );
    this.scannerData.value = iter;
  }

  onBottomBar(serial: string) {
    this.bottomBarImg.one =
      "https://objectjson.waterbang.top/test-vue3/land-not.svg";
    this.bottomBarImg.three =
      "https://objectjson.waterbang.top/test-vue3/home-not.svg";
    if (serial === "one") {
      this.bottomBarImg[serial] =
        "https://objectjson.waterbang.top/test-vue3/land.svg";
    }
    if (serial === "three") {
      this.bottomBarImg[serial] =
        "https://objectjson.waterbang.top/test-vue3/home.svg";
    }
  }
}
