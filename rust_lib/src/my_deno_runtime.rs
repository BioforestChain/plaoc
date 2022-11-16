use crate::errors::get_error_class_name;
use crate::fmt_errors::format_js_error;
use crate::ops::cli_exts;
use deno_broadcast_channel::InMemoryBroadcastChannel;
use deno_core::anyhow::Ok;
use deno_core::error::AnyError;
use deno_core::futures::future::LocalFutureObj;
use deno_core::CompiledWasmModuleStore;
use deno_core::FsModuleLoader;
use deno_core::ModuleLoader;
use deno_core::ModuleSpecifier;
use deno_core::SharedArrayBufferStore;
use deno_runtime::colors;
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

// #[cfg(target_os = "android")]
use crate::module_loader::AssetsModuleLoader;

fn create_web_worker_preload_module_callback() -> Arc<PreloadModuleCb> {
    Arc::new(move |worker| {
        let fut = async move { Ok(worker) };
        LocalFutureObj::new(Box::new(fut))
    })
}

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
        let preload_module_cb = create_web_worker_preload_module_callback();

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
            },
            extensions,
            // unsafely_ignore_certificate_gaubee: test2s: None,
            root_cert_store: None,
            seed: None,
            module_loader,
            create_web_worker_cb,
            preload_module_cb,
            source_map_getter: None,
            format_js_error_fn: Some(Arc::new(format_js_error)),
            // use_deno_namespace: args.use_deno_namespace,
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
            // maybe_exit_code: args.maybe_exit_code,
            stdio: stdio.clone(),
            unsafely_ignore_certificate_errors: todo!(),
        };
        // log::info!("bootstrap_from_options: {:?}", args.name);

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
    #[cfg(target_os = "android")] module_loader_builder: Arc<AssetsModuleLoader>,
    #[cfg(not(target_os = "android"))] module_loader_builder: fn() -> Rc<dyn ModuleLoader>,
    main_module: ModuleSpecifier,
    permissions: Permissions,
    stdio: deno_runtime::ops::io::Stdio,
) -> MainWorker {
    #[cfg(target_os = "android")]
    let module_loader = Rc::new((*module_loader_builder.clone()).clone());
    #[cfg(not(target_os = "android"))]
    let module_loader = module_loader_builder();

    let create_web_worker_cb = create_web_worker_callback(module_loader_builder, stdio.clone());
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
            user_agent: "plaoc".to_string(),
            no_color: true, // !colors::use_color(),
            is_tty: colors::is_tty(),
            runtime_version: "x".to_string(),
            ts_version: "x".to_string(),
            unstable: true, // 是否开启不安全api
        },
        extensions, // op
        unsafely_ignore_certificate_errors: None,
        root_cert_store: None,
        seed: None,
        source_map_getter: None,
        format_js_error_fn: Some(Arc::new(format_js_error)),
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
        stdio: stdio.clone(),
    };

    MainWorker::bootstrap_from_options(main_module, permissions, options)
}

// #[tokio::main]
pub async fn bootstrap_deno_runtime(
    #[cfg(target_os = "android")] module_loader_builder: Arc<AssetsModuleLoader>,
    #[cfg(not(target_os = "android"))] module_loader_builder: fn() -> Rc<dyn ModuleLoader>,
    entry_js_path: &str,
) -> Result<(), AnyError> {
    log::info!("start deno runtime for entry_js_path!!!{:}", &entry_js_path);
    let main_module = deno_core::resolve_path(entry_js_path)?;
    let permissions = Permissions::allow_all();

    let mut worker = create_main_worker(
        module_loader_builder,
        main_module.clone(),
        permissions,
        Default::default(),
    );
    log::info!("start deno runtime!!!");

    worker.execute_main_module(&main_module).await?;
    worker.run_event_loop(false).await?;
    Ok(())
}

// --------------------fs-----------------------

fn create_web_worker_fs_callback(stdio: deno_runtime::ops::io::Stdio) -> Arc<CreateWebWorkerCb> {
    Arc::new(move |args| {
        let create_web_worker_cb = create_web_worker_fs_callback(stdio.clone());
        let preload_module_cb = create_web_worker_preload_module_callback();

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
            },
            extensions,
            unsafely_ignore_certificate_errors: None,
            root_cert_store: None,
            seed: None,
            module_loader: Rc::new(FsModuleLoader),
            create_web_worker_cb,
            preload_module_cb,
            source_map_getter: None,
            format_js_error_fn: Some(Arc::new(format_js_error)),
            // use_deno_namespace: args.use_deno_namespace,
            worker_type: args.worker_type,
            maybe_inspector_server: None,
            get_error_class_fn: Some(&get_error_class_name),
            blob_store: BlobStore::default(),
            broadcast_channel: InMemoryBroadcastChannel::default(),
            shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
            compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
            stdio: stdio.clone(),
        };

        // log::info!("bootstrap_from_options: {:?}", args.name);

        WebWorker::bootstrap_from_options(
            args.name,
            args.permissions,
            args.main_module,
            args.worker_id,
            options,
        )
    })
}

pub fn create_main_fs_worker(
    main_module: ModuleSpecifier,
    permissions: Permissions,
    stdio: deno_runtime::ops::io::Stdio,
) -> MainWorker {
    let create_web_worker_cb = create_web_worker_fs_callback(stdio.clone());
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
            user_agent: "plaoc".to_string(),
            no_color: true, // !colors::use_color(),
            is_tty: colors::is_tty(),
            runtime_version: "x".to_string(),
            ts_version: "x".to_string(),
            unstable: true, // 是否开启不安全api
        },
        extensions, // op
        unsafely_ignore_certificate_errors: None,
        root_cert_store: None,
        seed: None,
        source_map_getter: None,
        format_js_error_fn: Some(Arc::new(format_js_error)),
        create_web_worker_cb,
        web_worker_preload_module_cb,
        maybe_inspector_server: None,
        should_break_on_first_statement: false,
        module_loader: Rc::new(FsModuleLoader),
        get_error_class_fn: Some(&get_error_class_name),
        origin_storage_dir: None,
        blob_store: BlobStore::default(),
        broadcast_channel: InMemoryBroadcastChannel::default(),
        shared_array_buffer_store: Some(SharedArrayBufferStore::default()),
        compiled_wasm_module_store: Some(CompiledWasmModuleStore::default()),
        stdio: stdio.clone(),
    };

    log::info!("6");
    MainWorker::bootstrap_from_options(main_module, permissions, options)
}

// #[tokio::main]
pub async fn bootstrap_deno_fs_runtime(entry_js_path: &str) -> Result<(), AnyError> {
    log::info!(
        "start deno runtime for entry_js_path FsModuleLoader!!!{:}",
        &entry_js_path
    );
    let main_module = deno_core::resolve_path(entry_js_path)?;
    let permissions = Permissions::allow_all();

    let mut worker = create_main_fs_worker(main_module.clone(), permissions, Default::default());
    worker.execute_main_module(&main_module).await?;
    worker.run_event_loop(false).await?;
    Ok(())
}
