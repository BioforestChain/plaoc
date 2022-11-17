mod colors;
mod diagnostics;
mod errors;
mod fmt_errors;
mod module_loader;
mod my_deno_runtime;
mod ops;
mod web_socket;

// #[cfg(target_os = "android")]
mod android;

use crate::my_deno_runtime::bootstrap_deno_runtime;
use deno_core::error::AnyError;
use deno_core::FsModuleLoader;
use log::{Level, Metadata, Record};
use std::path::Path;
use std::rc::Rc;
// mod android;
mod js_bridge;

struct SimpleLogger;

impl log::Log for SimpleLogger {
    fn enabled(&self, metadata: &Metadata) -> bool {
        metadata.level() <= Level::Info
    }

    fn log(&self, record: &Record) {
        if self.enabled(record.metadata()) {
            println!("{} - {}", record.level(), record.args());
        }
    }

    fn flush(&self) {}
}

// static LOGGER: SimpleLogger = SimpleLogger;

// #[cfg(target_os = "android")]
#[tokio::main]
async fn main() -> Result<(), AnyError> {
    Ok(())
}
