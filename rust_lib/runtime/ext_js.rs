use deno_core::Extension;
pub fn init_js_files_ext() -> Extension {
    Extension::builder()
        .js(deno_core::include_js_files!(prefix "deno:runtime",
            "js/01_build.js",
            "js/01_errors.js",
            "js/01_version.js",
            "js/01_web_util.js",
            "js/06_util.js",
            "js/10_permissions.js",
            "js/11_workers.js",
            "js/12_io.js",
            "js/13_buffer.js",
            "js/30_fs.js",
            "js/30_os.js",
            "js/40_compiler_api.js",
            "js/40_diagnostics.js",
            "js/40_error_stack.js",
            "js/40_files.js",
            "js/40_fs_events.js",
            "js/40_http.js",
            "js/40_process.js",
            "js/40_read_file.js",
            "js/40_signals.js",
            "js/40_spawn.js",
            "js/40_testing.js",
            "js/40_tty.js",
            "js/40_write_file.js",
            "js/41_prompt.js",
            "js/90_deno_ns.js",
            "js/99_main.js",
        ))
        .build()
}
