# plaoc network

Here is the gateway of all DwebView-js responsible for sending messages to the mobile terminal and calling the system API.

## Quick Start

register serviceWorker for sys folder path

```typescript
import { registerServiceWorker } from "@bfsx/gateway";
registerServiceWorker();
```


### setUi

```typescript
await getCallNativeUi(param);
```

### setVfs

```typescript
await asyncCallDenoFunction(param);
```
