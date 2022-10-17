import "./bfsa-metadata.ts";
import { NOTIFICATION_MESSAGE_QUEUE } from "./src/constants.ts";
import { asyncCallDenoFunction } from "./src/message_source.ts";
import { messageToQueue } from "./src/message_queue.ts";

export async function messagePush() {
  const messageInfo = await asyncCallDenoFunction(1000);

  messageToQueue(messageInfo);
}

messagePush();
