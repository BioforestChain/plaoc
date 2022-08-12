// Copyright 2018-2022 the Deno authors. All rights reserved. MIT license.

use crate::js_bridge::call_js_function;
use deno_core::Extension;

pub mod errors;

pub fn cli_exts() -> Vec<Extension> {
    let ext = deno_core::Extension::builder()
        .ops(vec![
            call_js_function::op_js_to_rust_buffer::decl(),
            call_js_function::op_eval_js::decl(),
            // call_js_function::op_rust_to_js_hander::decl(),
            call_js_function::op_rust_to_js_buffer::decl(),
        ])
        .build();
    vec![ext, errors::init()]
}
