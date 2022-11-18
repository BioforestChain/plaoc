use deno_runtime::deno_websocket::tokio_tungstenite::tungstenite::Error;
use futures::future::ok;

pub struct PromiseOut<T> {
    value: T,
    status: Futures,
}

 impl PromiseOut {
    fn new(value: T) -> Self {
        Self {
            status: futures::pending!(),
            value: T,
        }
    }
    fn resolve(&self, value: T) -> Future<Ok<T>>{
        &self::status::ready(ok(value))
    }
    fn reject(&self,err:String)-> Future<Error<String>> {
        &self::status::ready(Err(err))
    }
}
