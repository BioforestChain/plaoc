use futures::future::ok;

pub struct PromiseOut<T> {
    value: T,
    status: futures,
}

pub impl PromiseOut {
    fn new(value: T) -> futures::pending!() {
        Self {
            status: futures::pending!(),
            value: T,
        }
    }
    fn resolve(&self, value: T) {
        &self::status::ready(ok(value))
    }
}
