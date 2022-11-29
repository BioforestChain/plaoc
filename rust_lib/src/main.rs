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
use deno_core::error::AnyError;
// mod android;
mod js_bridge;
// #[cfg(target_os = "android")]
 fn main() -> Result<(), AnyError> {
    Ok(())
}
