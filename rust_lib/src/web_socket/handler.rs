use crate::web_socket::{ws, Client, Clients, Result};
use android_logger::Config;
use log::Level;
use serde::{Deserialize, Serialize};
use uuid::Uuid;
use warp::{http::StatusCode, reply::json, ws::Message, Reply};

#[derive(Deserialize, Debug)]
pub struct RegisterRequest {
    public_key: String, // 区块链公钥
}

#[derive(Serialize, Debug)]
pub struct RegisterResponse {
    url: String,
}

#[derive(Deserialize, Debug)]
pub struct Event {
    pub function: String,
    pub public_key: Option<String>,
    pub message: String,
}

///向连接的客户端广播消息的能力
pub async fn publish_handler(body: Event, clients: Clients) -> Result<impl Reply> {
    clients
        .read()
        .await
        .iter()
        .filter(|(_, client)| match &body.public_key {
            Some(v) => &client.public_key == v,
            None => true,
        })
        .filter(|(_, client)| client.function.contains(&body.function))
        .for_each(|(_, client)| {
            if let Some(sender) = &client.sender {
                let _ = sender.send(Ok(Message::text(body.message.clone())));
            }
        });

    Ok(StatusCode::OK)
}
/// 注册客户端，默认给了启动DwebView的方法
pub async fn register_handler(body: RegisterRequest, clients: Clients) -> Result<impl Reply> {
    let public_key = body.public_key;
    let uuid = Uuid::new_v4().simple().to_string();

    register_client(uuid.clone(), public_key, clients).await;
    Ok(json(&RegisterResponse {
        url: format!("ws://127.0.0.1:8000/ws/{}", uuid),
    }))
}

async fn register_client(id: String, public_key: String, clients: Clients) {
    clients.write().await.insert(
        id,
        Client {
            public_key,
            function: vec![String::from("openDWebView")],
            sender: None,
        },
    );
}
/// 销毁客户端
pub async fn unregister_handler(id: String, clients: Clients) -> Result<impl Reply> {
    clients.write().await.remove(&id);
    Ok(StatusCode::OK)
}

pub async fn ws_handler(ws: warp::ws::Ws, id: String, clients: Clients) -> Result<impl Reply> {
    let client = clients.read().await.get(&id).cloned();
    match client {
        Some(c) => Ok(ws.on_upgrade(move |socket| ws::client_connection(socket, id, clients, c))),
        None => Err(warp::reject::not_found()),
    }
}

pub async fn health_handler() -> Result<impl Reply> {
    Ok(StatusCode::OK)
}
