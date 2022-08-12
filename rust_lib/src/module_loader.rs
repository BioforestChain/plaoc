#![cfg(target_os = "android")]

use anyhow::Error;
// use assets_manager::{loader, Asset, AssetCache};
use deno_core::error::generic_error;
use deno_core::*;
use futures::future::FutureExt;
use ndk::asset::{Asset, AssetManager};
use std::ffi::CString;
use std::io::Read;
use std::pin::Pin;
use std::ptr::NonNull;
use std::rc::Rc;
use std::sync::Arc;

pub struct AssetsModuleLoader {
    pub cache: Arc<AssetManager>,
}
impl AssetsModuleLoader {
    pub fn new() -> Self {
        log::info!("init AssetsModuleLoader1");
        let ctx = ndk_context::android_context();
        let vm = unsafe { jni::JavaVM::from_raw(ctx.vm().cast()).unwrap() };
        let env = vm.attach_current_thread().unwrap();

        let asset_manager_ptr = unsafe {
            log::info!("init AssetsModuleLoader2");
            let jasset_manager = env
                .call_method(
                    ctx.context().cast(),
                    "getAssets",
                    "()Landroid/content/res.AssetManager;",
                    &[],
                )
                .unwrap()
                .l()
                .unwrap();
            log::info!("init AssetsModuleLoader3");

            let asset_manager_ptr =
                ndk_sys::AAssetManager_fromJava(env.get_native_interface(), jasset_manager.cast());
            log::info!("init AssetsModuleLoader4");

            NonNull::new(asset_manager_ptr).unwrap()
        };

        AssetsModuleLoader::from_ptr(asset_manager_ptr)
    }
    pub fn from_ptr(asset_manager_ptr: NonNull<ndk_sys::AAssetManager>) -> Self {
        let asset_manager = unsafe { AssetManager::from_ptr(asset_manager_ptr) };
        log::info!("asset_manager ready!!!");
        AssetsModuleLoader {
            cache: Arc::new(asset_manager),
        }
    }
    pub fn get_string_asset(self, path: &str) -> String {
        let code = {
            let mut asset = self
                .cache
                .open(&CString::new(path).unwrap())
                .expect(&format!("Could not open asset {}", path));
            let data = asset.get_buffer().unwrap();
            match std::str::from_utf8(data) {
                Ok(v) => v,
                Err(e) => panic!("Invalid UTF-8 sequence: {}", e),
            }
            .to_string()
        };
        return code;
    }
}
impl Clone for AssetsModuleLoader {
    fn clone(&self) -> Self {
        AssetsModuleLoader::from_ptr(self.cache.ptr())
    }
}

impl ModuleLoader for AssetsModuleLoader {
    fn resolve(
        &self,
        specifier: &str,
        referrer: &str,
        _is_main: bool,
    ) -> Result<ModuleSpecifier, Error> {
        Ok(resolve_import(specifier, referrer)?)
    }

    fn load(
        &self,
        module_specifier: &ModuleSpecifier,
        _maybe_referrer: Option<ModuleSpecifier>,
        _is_dynamic: bool,
    ) -> Pin<Box<ModuleSourceFuture>> {
        let module_specifier = module_specifier.clone();
        let path = module_specifier
            .to_file_path()
            .map_err(|_| {
                generic_error(format!(
                    "Provided module specifier \"{}\" is not a file URL.",
                    module_specifier.clone()
                ))
            })
            .unwrap();
        let module_type = if let Some(extension) = path.extension() {
            let ext = extension.to_string_lossy().to_lowercase();
            if ext == "json" {
                ModuleType::Json
            } else {
                ModuleType::JavaScript
            }
        } else {
            ModuleType::JavaScript
        };
        // let asset_id = &str::replace(&path.to_string_lossy(), "/", ".")[1..];
        // log::info!("read asset:{}", asset_id);
        // let code: String = self
        //     .cache
        //     .load::<String>(asset_id)
        //     .unwrap()
        //     .read()
        //     .to_string();

        let code = {
            let mut asset_path = path.to_str().unwrap();
            if asset_path.starts_with("/") {
                asset_path = &path.to_str().unwrap()[1..];
            }
            let mut asset = self
                .cache
                .open(&CString::new(asset_path).unwrap())
                .expect(&format!("Could not open asset {}", asset_path));
            let data = asset.get_buffer().unwrap();
            match std::str::from_utf8(data) {
                Ok(v) => v,
                Err(e) => panic!("Invalid UTF-8 sequence: {}", e),
            }
            .to_string()
        };

        return async move {
            let module = ModuleSource {
                code,
                module_type,
                module_url_specified: module_specifier.clone().to_string(),
                module_url_found: module_specifier.to_string(),
            };
            Ok(module)
            // Err(generic_error("Module loading is not supported"))
        }
        .boxed_local();
        // async move {
        //     let path = module_specifier.to_file_path().map_err(|_| {
        //         generic_error(format!(
        //             "Provided module specifier \"{}\" is not a file URL.",
        //             module_specifier
        //         ))
        //     })?;
        //     let module_type = if let Some(extension) = path.extension() {
        //         let ext = extension.to_string_lossy().to_lowercase();
        //         if ext == "json" {
        //             ModuleType::Json
        //         } else {
        //             ModuleType::JavaScript
        //         }
        //     } else {
        //         ModuleType::JavaScript
        //     };

        //     let asset_id = &str::replace(&path.to_string_lossy(), "/", ".")[1..];
        //     log::info!("read asset:{}", asset_id);
        //     let code: String = *self.cache.load::<String>(asset_id)?.read();
        //     let module = ModuleSource {
        //         code,
        //         module_type,
        //         module_url_specified: module_specifier.to_string(),
        //         module_url_found: module_specifier.to_string(),
        //     };
        //     Ok(module)
        // }
        // .boxed_local()
    }
}
