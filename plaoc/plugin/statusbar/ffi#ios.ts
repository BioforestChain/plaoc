export class StatusBarFFI implements Plaoc.IStatusBarFFI {
  private _ffi = (window as any).webkit
    .messageHandlers as Plaoc.StatusBarIosFFI;

  async setStatusBarColor(
    color?: Plaoc.RGBAHex,
    barStyle?: Plaoc.StatusBarStyle
  ): Promise<void> {
    if (color) {
      this._ffi.updateStatusBackgroundColor.postMessage(color);
    }

    if (barStyle) {
      let darkIcons: Plaoc.StatusBarIosStyle;

      switch (barStyle) {
        case "light-content":
          darkIcons = "lightContent" as Plaoc.StatusBarIosStyle;
          break;
        default:
          darkIcons = "default" as Plaoc.StatusBarIosStyle;
      }

      await this.setStatusBarStyle(darkIcons);
    }

    return;
  }

  async getStatusBarColor(): Promise<Plaoc.RGBAHex> {
    const backgroundColor = await this._ffi.statusBackgroundColor.postMessage(
      null
    );

    return backgroundColor;
  }

  async getStatusBarVisible(): Promise<boolean> {
    const isVisible = await this._ffi.getStatusBarVisible.postMessage(null);

    return isVisible;
  }

  async toggleStatusBarVisible(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    this._ffi.updateStatusHidden.postMessage(isVisible ? "1" : "0");

    return;
  }

  async setStatusBarHidden(): Promise<void> {
    const isVisible = await this.getStatusBarVisible();

    if (isVisible) {
      await this.toggleStatusBarVisible();
    }

    return;
  }

  async getStatusBarOverlay(): Promise<boolean> {
    const overlay = await this._ffi.getStatusBarOverlay.postMessage(null);

    return overlay;
  }

  async toggleStatusBarOverlay(): Promise<void> {
    const overlay = await this.getStatusBarOverlay();

    this._ffi.updateStatusBarOverlay.postMessage(overlay ? 0 : 1);

    return;
  }

  async setStatusBarOverlay(): Promise<void> {
    const overlay = await this.getStatusBarOverlay();

    if (!overlay) {
      await this.toggleStatusBarOverlay();
    }

    return;
  }

  async getStatusBarStyle(): Promise<Plaoc.StatusBarStyle> {
    const barStyle: Plaoc.StatusBarIosStyle =
      await this._ffi.statusBarStyle.postMessage(null);

    let darkIcons: Plaoc.StatusBarStyle;

    switch (barStyle) {
      case "lightcontent" as Plaoc.StatusBarIosStyle:
        darkIcons = "light-content" as Plaoc.StatusBarStyle;
        break;
      default:
        darkIcons = "dark-content" as Plaoc.StatusBarStyle;
    }

    return darkIcons;
  }

  private setStatusBarStyle(barStyle: Plaoc.StatusBarIosStyle): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.updateStatusStyle.postMessage(barStyle);

      resolve();
    });
  }
}
