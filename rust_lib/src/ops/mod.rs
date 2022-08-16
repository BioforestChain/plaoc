// Copyright 2018-2022 the Deno authors. All rights reserved. MIT license.

use crate::js_bridge::call_js_function;
use deno_core::Extension;

pub fn cli_exts() -> Vec<Extension> {
    log::info!("cli_exts 1");
    let ext = Extension::builder()
        .ops(vec![
            call_js_function::op_js_to_rust_buffer::decl(),
            call_js_function::op_eval_js::decl(),
            // call_js_function::op_rust_to_js_hander::decl(),
            call_js_function::op_rust_to_js_buffer::decl(),
        ])
        .build();
    log::info!("cli_exts 2");
    vec![ext]
}


// use crate::proc_state::ProcState;

// pub fn cli_exts(ps: ProcState) -> Vec<Extension> {
//     vec![init_proc_state(ps)]
//   }
  
//   fn init_proc_state(ps: ProcState) -> Extension {
//     Extension::builder()
//         .ops(vec![
//             call_js_function::op_js_to_rust_buffer::decl(),
//             call_js_function::op_eval_js::decl(),
//             // call_js_function::op_rust_to_js_hander::decl(),
//             call_js_function::op_rust_to_js_buffer::decl(),
//         ])
//       .state(move |state| {
//         state.put(ps.clone());
//         Ok(())
//       })
//       .build()
//   }
  