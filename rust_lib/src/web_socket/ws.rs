use crate::web_socket::{Client, Clients};
use android_logger::Config;
use core::fmt::Debug;
use futures::{FutureExt, StreamExt};
use serde::Deserialize;
use serde_json::from_str;
use std::any::Any;
use std::fmt;
use tokio::sync::mpsc;
use tokio_stream::wrappers::UnboundedReceiverStream;
use warp::ws::{Message, WebSocket};

#[derive(Deserialize, Debug)]
pub struct TopicsRequest {
    pub function: Vec<String>,
    pub public_key: String,
    pub data: String,
}
/// 实现一个toString的trait
impl fmt::Display for TopicsRequest {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(
            f,
            "{{public_key:{},function:{:?},data:{}}}",
            self.public_key, self.function, self.data
        )
    }
}

#[allow(dead_code)]
pub async fn client_connection(ws: WebSocket, id: String, clients: Clients, mut client: Client) {
    let (client_ws_sender, mut client_ws_rcv) = ws.split();
    let (client_sender, client_rcv) = mpsc::unbounded_channel();
    let client_rcv = UnboundedReceiverStream::new(client_rcv); // <-- this

    tokio::task::spawn(client_rcv.forward(client_ws_sender).map(|result| {
        if let Err(e) = result {
            eprintln!("error sending websocket msg: {}", e);
        }
    }));

    client.sender = Some(client_sender);
    clients.write().await.insert(id.clone(), client);

    println!("{} connected", id);

    while let Some(result) = client_ws_rcv.next().await {
        let msg = match result {
            Ok(msg) => msg,
            Err(e) => {
                eprintln!("error receiving ws message for id: {}): {}", id.clone(), e);
                break;
            }
        };
        client_msg(&id, msg, &clients).await;
    }

    clients.write().await.remove(&id);
    println!("{} disconnected", id);
}
#[allow(dead_code)]
async fn client_msg(id: &str, msg: Message, clients: &Clients) {
    let message = match msg.to_str() {
        Ok(v) => v,
        Err(_) => return,
    };
    if message == "ping" || message == "ping\n" {
        return;
    }
    log::info!("message2: {:?} ", message); // 生产环境记得删除
    let topics_req: TopicsRequest = match from_str(message) {
        Ok(v) => {
            log::info!("vvvvvv: {:?} ", &v); // 生产环境记得删除
            v
        }
        Err(e) => {
            log::info!("eeeeee: {:?} ", e); // 生产环境记得删除
            eprintln!("error while parsing message to function request: {}", e);
            return;
        }
    };
    // topics_req: TopicsRequest { public_key: "",function: ["openScanner"],data: ""}
    log::info!("&topics_req.function[0]: {:?} ", &topics_req); // 生产环境记得删除

    // call_android_function::call_android(&topics_req); // 通知FFI函数

    let mut locked = clients.write().await;
    if let Some(v) = locked.get_mut(id) {
        v.function = topics_req.function;
    }
}
