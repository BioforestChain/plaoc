#![cfg(target_os = "android")]
use crate::errors::get_error_class_name;
use crate::fmt_errors::PrettyJsError;
use crate::module_loader::AssetsModuleLoader;
use crate::ops::cli_exts;
use deno_core::error::AnyError;
use deno_core::futures::future::LocalFutureObj;
use deno_core::CompiledWasmModuleStore;
use deno_core::Extension;
use deno_core::ModuleSpecifier;
use deno_core::SharedArrayBufferStore;
use deno_runtime::colors;
use deno_runtime::deno_broadcast_channel::InMemoryBroadcastChannel;
use deno_runtime::deno_web::BlobStore;
use deno_runtime::ops::worker_host::CreateWebWorkerCb;
use deno_runtime::ops::worker_host::PreloadModuleCb;
use deno_runtime::permissions::Permissions;
use deno_runtime::web_worker::{WebWorker, WebWorkerOptions};
use deno_runtime::worker::MainWorker;
use deno_runtime::worker::WorkerOptions;
use deno_runtime::BootstrapOptions;
use std::rc::Rc;
use std::sync::Arc;

fn create_web_worker_preload_module_callback() -> Arc<PreloadModuleCb> {
    Arc::new(move |mut worker| {
        let fut = async move { Ok(worker) };
        LocalFutureObj::new(Box::new(fut))
    })
}

fn create_web_worker_callback(
    module_loader_arc: Arc<AssetsModuleLoader>,
) -> Arc<CreateWebWorkerCb> {
    Arc::new(move |args| {
        let module_loader: Rc<AssetsModuleLoader> = Rc::new((*module_loader_arc.clone()).clone());
        let create_web_worker_cb = create_web_worker_callback(module_loader_arc.clone());
        let preload_module_cb = create_web_worker_preload_module_callback();

        let mut extensions = cli_exts();

        let options = WebWorkerOptions {
            bootstrap: BootstrapOptions {
                args: vec![],
                cpu_count: std::thread::available_parallelism()
                    .map(|p| p.get())
                    .unwrap_or(1),
                debug_flag: false,
                enable_testing_features: false,
                location: Some(args.main_module.clone()),
                no_color: true, // !colors::use_color(),
                is_tty: colors::is_tty(),
                runtime_version: "x".to_string(),
                ts_version: "x".to_string(),
                unstable: true,
            },
            extensions,
            unsafely_ignore_certificate_errors: None,
            root_cert_store: None,
            user_agent: "plaoc".to_string(),
            seed: None,
            module_loader,
            create_web_worker_cb,
            preload_module_cb,
            source_map_getter: None,
            js_error_create_fn: Some(Rc::new(PrettyJsError::create)),
            use_deno_namespace: args.use_deno_namespace,
            worker_type: args.worker_type,
            maybe_inspector_server: None,
            get_error_class_fn: Some(&get_error_class_name),
            blob_store: BlobStore::default(),
            broadcast_channel: InMemoryBroadcastChannel::default(),
            /// 用于在隔离之间传输 SharedArrayBuffers 的存储。
            /// 如果多个isolate应该有共享的可能
            /// SharedArrayBuffers，它们应该使用相同的[SharedArrayBufferStore]。 如果
            /// 没有指定[SharedArrayBufferStore]，SharedArrayBuffer不能指定
            /// 序列化。
            shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
            /// 用于在之间传输 `WebAssembly.Module` 对象的存储隔离。
            /// 如果多个isolate应该有共享的可能
            /// `WebAssembly.Module` 对象，它们应该使用相同的
            /// [CompiledWasmModuleStore]。 如果没有指定 [CompiledWasmModuleStore]，
            /// `WebAssembly.Module` 对象不能被序列化。
            compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
            maybe_exit_code: args.maybe_exit_code,
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

pub fn create_main_worker(
    module_loader_arc: Arc<AssetsModuleLoader>,
    main_module: ModuleSpecifier,
    permissions: Permissions,
) -> MainWorker {
    let module_loader: Rc<AssetsModuleLoader> = Rc::new((*module_loader_arc.clone()).clone());
    let create_web_worker_cb = create_web_worker_callback(module_loader_arc);
    let web_worker_preload_module_cb = create_web_worker_preload_module_callback();

    let extensions = cli_exts();

    let options = WorkerOptions {
        bootstrap: BootstrapOptions {
            args: vec![],
            cpu_count: std::thread::available_parallelism()
                .map(|p| p.get())
                .unwrap_or(1),
            debug_flag: false,
            enable_testing_features: false,
            location: None,
            no_color: true, // !colors::use_color(),
            is_tty: colors::is_tty(),
            runtime_version: "x".to_string(),
            ts_version: "x".to_string(),
            unstable: true, // 是否开启不安全api
        },
        extensions, // op
        unsafely_ignore_certificate_errors: None,
        root_cert_store: None,
        user_agent: "plaoc".to_string(),
        seed: None,
        source_map_getter: None,
        js_error_create_fn: Some(Rc::new(PrettyJsError::create)),
        create_web_worker_cb,
        web_worker_preload_module_cb,
        maybe_inspector_server: None,
        should_break_on_first_statement: false,
        module_loader,
        get_error_class_fn: Some(&get_error_class_name),
        origin_storage_dir: None,
        blob_store: BlobStore::default(),
        broadcast_channel: InMemoryBroadcastChannel::default(),
        shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
        compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
    };

    MainWorker::bootstrap_from_options(main_module, permissions, options)
}

// #[tokio::main]
pub async fn bootstrap_deno_runtime(
    module_loader: Arc<AssetsModuleLoader>,
    entry_js_path: &str,
) -> Result<(), AnyError> {
    let main_module = deno_core::resolve_path(entry_js_path)?;
    let permissions = Permissions::allow_all();

    let mut worker = create_main_worker(module_loader, main_module.clone(), permissions);
    log::info!("start deno runtime!!!");

    worker.execute_main_module(&main_module).await?;
    worker.run_event_loop(false).await?;
    Ok(())
}
