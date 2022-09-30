# plaoc network

Here is the gateway of all DwebView-js responsible for sending messages to the mobile terminal and calling the system API.

## Quick Start

register serviceWorker for sys folder path

```typescript
import { registerServerWorker } from "@bfsx/gateway";
registerServerWorker();
```


### setUi

```typescript
await netCallNativeUi(param);
```

### setVfs

```typescript
await netCallNativeVfs(param);
```