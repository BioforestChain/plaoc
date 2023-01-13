// Copyright 2018-2022 the Deno authors. All rights reserved. MIT license.

use crate::js_bridge::call_js_function;
use deno_core::Extension;

pub fn cli_exts() -> Vec<Extension> {
    log::info!("cli_exts 1");
    let ext = Extension::builder("android")
        .ops(vec![
            call_js_function::op_js_to_rust_buffer::decl(),
            call_js_function::op_send_zero_copy_buffer::decl(),
            // call_js_function::op_rust_to_js_hander::decl(),
            call_js_function::op_rust_to_js_buffer::decl(),
            call_js_function::op_rust_to_js_system_buffer::decl(),
            call_js_function::op_rust_to_js_app_notification::decl(),
            call_js_function::op_rust_to_js_set_app_notification::decl(),
        ])
        .build();
    log::info!("cli_exts 2");
    vec![ext]
}
