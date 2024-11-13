use crate::errors::get_error_class_name;
use crate::fmt_errors::format_js_error;
use crate::ops::cli_exts;
use deno_broadcast_channel::InMemoryBroadcastChannel;
use deno_core::anyhow::Ok;
use deno_core::error::AnyError;
use deno_core::CompiledWasmModuleStore;
use deno_core::FsModuleLoader;
#[allow(unused_imports)]
use deno_core::ModuleLoader;
use deno_core::ModuleSpecifier;
use deno_core::SharedArrayBufferStore;
use deno_runtime::colors;
use deno_runtime::deno_web::BlobStore;
use deno_runtime::ops::worker_host::CreateWebWorkerCb;
use deno_runtime::permissions::PermissionsContainer;
use deno_runtime::web_worker::{WebWorker, WebWorkerOptions};
use deno_runtime::worker::MainWorker;
use deno_runtime::worker::WorkerOptions;
use deno_runtime::BootstrapOptions;
use std::rc::Rc;
use std::sync::Arc;

// lazy_static! {
//     // pub(crate) static ref JS_CONTEXT: Arc<Mutex<Vec<MainWorker>>> = Arc::new(Mutex::new(vec![]));
//     pub(crate) static  ref JS_CONTEXT: Vec<MainWorker> = vec![];
// }
pub static mut JS_CONTEXT: Vec<MainWorker> = vec![];

#[cfg(target_os = "android")]
use crate::module_loader::AssetsModuleLoader;

#[allow(dead_code)]
fn create_web_worker_callback(
    #[cfg(target_os = "android")] module_loader_builder: Arc<AssetsModuleLoader>,
    #[cfg(not(target_os = "android"))] module_loader_builder: fn() -> Rc<dyn ModuleLoader>,
    stdio: deno_runtime::ops::io::Stdio,
) -> Arc<CreateWebWorkerCb> {
    Arc::new(move |args| {
        #[cfg(target_os = "android")]
        let module_loader = Rc::new((*module_loader_builder.clone()).clone());
        #[cfg(not(target_os = "android"))]
        let module_loader = module_loader_builder();

        let create_web_worker_cb =
            create_web_worker_callback(module_loader_builder.clone(), stdio.clone());
        let preload_module_cb = Arc::new(|_| {
            todo!("Web workers are not supported in the example");
        });

        let extensions = cli_exts();
        let options = WebWorkerOptions {
            bootstrap: BootstrapOptions {
                args: vec![],
                cpu_count: std::thread::available_parallelism()
                    .map(|p| p.get())
                    .unwrap_or(1),
                debug_flag: false,
                enable_testing_features: false,
                user_agent: "plaoc".to_string(),
                location: Some(args.main_module.clone()),
                no_color: true, // !colors::use_color(),
                is_tty: colors::is_tty(),
                runtime_version: "x".to_string(),
                ts_version: "x".to_string(),
                unstable: true,
                locale: deno_core::v8::icu::get_language_tag(),
                inspect: false,
            },
            extensions,
            startup_snapshot: None, // 快照
            npm_resolver: None,
            // 根证书的容器能够为连接身份验证提供信任根。
            root_cert_store: None,
            seed: None,
            module_loader,
            create_web_worker_cb,
            pre_execute_module_cb: preload_module_cb.clone(),
            preload_module_cb,
            source_map_getter: None,
            format_js_error_fn: Some(Arc::new(format_js_error)),
            // use_deno_namespace: args.use_deno_namespace,
            worker_type: args.worker_type,
            // 用于代理从 devtools 到检查器的连接的 Websocket 服务器。
            maybe_inspector_server: None,
            get_error_class_fn: Some(&get_error_class_name),
            cache_storage_dir: None,
            blob_store: BlobStore::default(),
            broadcast_channel: InMemoryBroadcastChannel::default(),
            shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
            compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
            stdio: stdio.clone(),
            unsafely_ignore_certificate_errors: None,
        };

        WebWorker::bootstrap_from_options(
            args.name,
            args.permissions,
            args.main_module,
            args.worker_id,
            options,
        )
    })
}

#[allow(dead_code)]
pub fn create_main_worker(
    #[cfg(target_os = "android")] module_loader_builder: Arc<AssetsModuleLoader>,
    #[cfg(not(target_os = "android"))] module_loader_builder: fn() -> Rc<dyn ModuleLoader>,
    main_module: ModuleSpecifier,
    permissions: PermissionsContainer,
    stdio: deno_runtime::ops::io::Stdio,
) -> MainWorker {
    #[cfg(target_os = "android")]
    let module_loader = Rc::new((*module_loader_builder.clone()).clone());
    #[cfg(not(target_os = "android"))]
    let module_loader = module_loader_builder();

    let create_web_worker_cb = create_web_worker_callback(module_loader_builder, stdio.clone());
    let web_worker_preload_module_cb = Arc::new(|_| {
        todo!("Web workers are not supported in the example");
    });

    let web_worker_event_cb = Arc::new(|_| {
        todo!("Web workers are not supported in the example");
    });

    let options = WorkerOptions {
        bootstrap: BootstrapOptions {
            args: vec![],
            cpu_count: std::thread::available_parallelism()
                .map(|p| p.get())
                .unwrap_or(1),
            debug_flag: false,
            enable_testing_features: false,
            location: None,
            user_agent: "plaoc".to_string(),
            no_color: true, // !colors::use_color(),
            is_tty: colors::is_tty(),
            runtime_version: "x".to_string(),
            ts_version: "x".to_string(),
            unstable: true, // 是否开启不安全api
            locale: deno_core::v8::icu::get_language_tag(),
            inspect: false,
        },
        extensions: cli_exts(), // op
        extensions_with_js: vec![],
        startup_snapshot: None,
        npm_resolver: None,
        unsafely_ignore_certificate_errors: None,
        root_cert_store: None,
        seed: None,
        source_map_getter: None,
        format_js_error_fn: Some(Arc::new(format_js_error)),
        create_web_worker_cb,
        web_worker_preload_module_cb,
        web_worker_pre_execute_module_cb: web_worker_event_cb,
        maybe_inspector_server: None,
        should_break_on_first_statement: false,
        should_wait_for_inspector_session: false,
        module_loader,
        get_error_class_fn: Some(&get_error_class_name),
        cache_storage_dir: None,
        origin_storage_dir: None,
        blob_store: BlobStore::default(),
        broadcast_channel: InMemoryBroadcastChannel::default(),
        shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
        compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
        stdio: stdio.clone(),
    };

    MainWorker::bootstrap_from_options(main_module, permissions, options)
}

#[allow(dead_code)]
pub async fn bootstrap_deno_runtime(
    #[cfg(target_os = "android")] module_loader_builder: Arc<AssetsModuleLoader>,
    #[cfg(not(target_os = "android"))] module_loader_builder: fn() -> Rc<dyn ModuleLoader>,
    entry_js_path: &str,
) -> Result<(), AnyError> {
    log::info!("start deno runtime for entry_js_path!!!{:}", &entry_js_path);
    let main_module = deno_core::resolve_path(entry_js_path)?;
    let permissions = PermissionsContainer::allow_all();

    let mut worker = create_main_worker(
        module_loader_builder,
        main_module.clone(),
        permissions,
        Default::default(),
    );
    log::info!("start deno runtime!!!");

    worker.execute_main_module(&main_module).await?;
    // worker.run_event_loop(false).await?;
    Ok(())
}

// --------------------fs-----------------------

// fn create_web_worker_fs_callback(stdio: deno_runtime::ops::io::Stdio) -> Arc<CreateWebWorkerCb> {
//     Arc::new(move |args| {
//         let create_web_worker_cb = create_web_worker_fs_callback(stdio.clone());
//         let preload_module_cb = create_web_worker_preload_module_callback();
//         let extensions = cli_exts();
//         let options = WebWorkerOptions {
//             bootstrap: BootstrapOptions {
//                 args: vec![],
//                 cpu_count: std::thread::available_parallelism()
//                     .map(|p| p.get())
//                     .unwrap_or(1),
//                 debug_flag: false,
//                 enable_testing_features: false,
//                 user_agent: "plaoc".to_string(),
//                 location: Some(args.main_module.clone()),
//                 no_color: true, // !colors::use_color(),
//                 is_tty: colors::is_tty(),
//                 runtime_version: "x".to_string(),
//                 ts_version: "x".to_string(),
//                 unstable: true,
//             },
//             extensions,
//             unsafely_ignore_certificate_errors: None,
//             root_cert_store: None,
//             seed: None,
//             module_loader: Rc::new(FsModuleLoader),
//             create_web_worker_cb,
//             preload_module_cb,
//             source_map_getter: None,
//             format_js_error_fn: Some(Arc::new(format_js_error)),
//             worker_type: args.worker_type,
//             maybe_inspector_server: None,
//             get_error_class_fn: Some(&get_error_class_name),
//             blob_store: BlobStore::default(),
//             broadcast_channel: InMemoryBroadcastChannel::default(),
//             shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
//             compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
//             stdio: Default::default(),
//         };
//         WebWorker::bootstrap_from_options(
//             args.name,
//             args.permissions,
//             args.main_module,
//             args.worker_id,
//             options,
//         )
//     })
// }

pub fn create_main_fs_worker(
    main_module: ModuleSpecifier,
    permissions: PermissionsContainer,
    // stdio: deno_runtime::ops::io::Stdio,
) -> MainWorker {
    // let create_web_worker_cb = create_web_worker_fs_callback(stdio.clone());
    let create_web_worker_cb = Arc::new(|_| {
        todo!("Web workers are not supported in the example");
    });
    let web_worker_preload_module_cb = Arc::new(|_| {
        todo!("Web workers are not supported in the example");
    });
    let web_worker_event_cb = Arc::new(|_| {
        todo!("Web workers are not supported in the example");
    });

    let options = WorkerOptions {
        bootstrap: BootstrapOptions {
            args: vec![],
            cpu_count: std::thread::available_parallelism()
                .map(|p| p.get())
                .unwrap_or(1),
            debug_flag: false,
            enable_testing_features: false,
            location: None,
            user_agent: "plaoc".to_string(),
            no_color: true, // !colors::use_color(),
            is_tty: colors::is_tty(),
            runtime_version: "x".to_string(),
            ts_version: "x".to_string(),
            unstable: true, // 是否开启不安全api
            locale: deno_core::v8::icu::get_language_tag(),
            inspect: false,
        },
        extensions: cli_exts(), // op
        extensions_with_js: vec![],
        startup_snapshot: None,
        npm_resolver: None,
        unsafely_ignore_certificate_errors: None,
        root_cert_store: None,
        seed: None,
        source_map_getter: None,
        format_js_error_fn: Some(Arc::new(format_js_error)),
        create_web_worker_cb,
        web_worker_preload_module_cb,
        web_worker_pre_execute_module_cb: web_worker_event_cb,
        maybe_inspector_server: None,
        should_break_on_first_statement: false,
        should_wait_for_inspector_session: false,
        module_loader: Rc::new(FsModuleLoader),
        get_error_class_fn: Some(&get_error_class_name),
        cache_storage_dir: None,
        origin_storage_dir: None,
        blob_store: BlobStore::default(),
        broadcast_channel: InMemoryBroadcastChannel::default(),
        shared_array_buffer_store: None,
        compiled_wasm_module_store: None,
        // shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
        // compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
        stdio: Default::default(),
    };

    MainWorker::bootstrap_from_options(main_module, permissions, options)
}

// #[tokio::main]
pub async fn bootstrap_deno_fs_runtime(entry_js_path: &str) -> Result<(), AnyError> {
    log::info!(
        "rust#bootstrap_deno_fs_runtime,entry_js_path: {:}",
        &entry_js_path
    );
    let main_module = deno_core::resolve_path(entry_js_path)?;
    let permissions = PermissionsContainer::allow_all();

    let worker = create_main_fs_worker(main_module.clone(), permissions);

    // let call_excute =  |script_name,source_code| {
    //     worker.execute_script(script_name, source_code);
    // };

    unsafe {
        JS_CONTEXT.push(worker);

        let result = JS_CONTEXT[0].execute_main_module(&main_module).await;
        log::info!(
            "rust#bootstrap_deno_fs_runtime,执行结束🌶: {:}",
            &entry_js_path
        );
        if let Err(err) = result {
            log::error!("execute_mod err {:?}", err);
        }

        if let Err(e) = JS_CONTEXT[0].run_event_loop(false).await {
            log::error!("Future got unexpected error: {:?}", e);
        }
    }

    Ok(())
}
