import { useState, useEffect } from "react";
import reactLogo from "./assets/react.svg";
import "./App.css";
import {
  BfcsTopBar,
  BfcsBottomBarButton,
  OpenScanner,
  BfcsDialogButton,
  BfcsTopBarButton,
} from "@bfsx/plugin";
import "@bfsx/plugin-typed-react";

function App() {
  const [count, setCount] = useState(0);
  const [visible, setVisible] = useState(false);
  const [scannerData, setScannerData] = useState({
    value: "DwebView-js ♻️ Deno-js",
  });

  useEffect(() => {
    const eee = document.querySelector<BfcsBottomBarButton>("#eee")!;
    eee.addEventListener("click", async () => {
      await openQrScanner();
    });

    const ddb = document.querySelector<BfcsDialogButton>("#ddb")!;
    ddb.addEventListener("click", () => {
      console.log("弹窗关闭");
    });

    const aaa = document.querySelector<BfcsTopBarButton>("#aaa")!;
    aaa.addEventListener("click", async () => {
      await openBarScanner();
    });

    const ddd = document.querySelector<BfcsBottomBarButton>("#ddd")!;
    ddd.addEventListener("click", () => {
      onBottomBar("one");
    });
  });

  const openQrScanner = async () => {
    const scanner = document.querySelector<OpenScanner>("dweb-scanner")!;
    const iter = await scanner.openQrCodeScanner();

    console.log(
      "scannerData.value = await scanner.openQrCodeScanner() -->",
      iter
    );
    // scannerData.value = iter;
    setScannerData({ value: iter });
  };

  async function openBarScanner() {
    const scanner = document.querySelector<OpenScanner>("dweb-scanner")!;
    const iter = await scanner.openBarCodeScanner();
    console.log(
      "scannerData.value = await scanner.openBarCodeScanner() -->",
      iter
    );
    setScannerData({ value: iter });
  }

  async function init() {
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
  }

  init();

  const bottomBarImg = {
    one: "https://objectjson.waterbang.top/test-vue3/land.svg",
    two: "https://objectjson.waterbang.top/test-vue3/scanner.svg",
    three: "https://objectjson.waterbang.top/test-vue3/home-not.svg",
  };

  function onBottomBar(serial: string) {
    bottomBarImg.one =
      "https://objectjson.waterbang.top/test-vue3/land-not.svg";
    bottomBarImg.three =
      "https://objectjson.waterbang.top/test-vue3/home-not.svg";
    if (serial === "one") {
      bottomBarImg[serial] =
        "https://objectjson.waterbang.top/test-vue3/land.svg";
    }
    if (serial === "three") {
      bottomBarImg[serial] =
        "https://objectjson.waterbang.top/test-vue3/home.svg";
    }
  }

  getBlockInfo();
  async function getBlockInfo() {
    fetch("/getBlockInfo", {
      headers: { "Content-type": "application/json" },
    })
      .then((res) => res.json())
      .then((response) => {
        console.log({ response });
      })
      .catch((error) => {
        console.log("Looks like there was a problem: \n", error);
      });
  }

  function pop() {
    console.log("冒泡");
  }

  return (
    <div className="App">
      <div>
        <a href="https://reactjs.org" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <p>
          Edit <code>src/App.tsx</code> and save to test HMR
        </p>
      </div>
      <p className="read-the-docs" onClick={() => setVisible(!visible)}>
        Click on the Vite and React logos to learn more
      </p>
      <dweb-dialog-alert
        visible={visible}
        title="810975"
        content="密码忘了怎么办？"
        disOnClickOutside
        disOnBackPress
      >
        <dweb-dialog-button id="ddb">确定</dweb-dialog-button>
      </dweb-dialog-alert>
      <dweb-top-bar
        id="topbar"
        title="Ar react 扫雷"
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
            source={bottomBarImg.one}
            type="AssetIcon"
          ></dweb-bottom-bar-icon>
          <dweb-bottom-bar-text
            color="#938F99"
            selected-color="#1C1B1F"
            value="土地"
          ></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="eee" diSelectable>
          <dweb-bottom-bar-icon
            source={bottomBarImg.two}
            type="AssetIcon"
          ></dweb-bottom-bar-icon>
          <dweb-bottom-bar-text value="扫码"></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="fff" onClick={() => onBottomBar("three")}>
          <dweb-bottom-bar-icon
            source={bottomBarImg.three}
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
      <div className="bottombar"></div>
    </div>
  );
}

export default App;
