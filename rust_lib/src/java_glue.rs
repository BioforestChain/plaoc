#![allow(
clippy::enum_variant_names,
clippy::unused_unit,
clippy::let_and_return,
clippy::not_unsafe_ptr_arg_deref,
clippy::cast_lossless,
clippy::blacklisted_name,
clippy::too_many_arguments,
clippy::trivially_copy_pass_by_ref,
clippy::let_unit_value,
clippy::clone_on_copy
)]

include!(concat!(env!("OUT_DIR"), "/java_glue.rs"));


/**
 * https://www.infinyon.com/blog/2021/05/java-client/
 * This is a typical Rust pattern when using build scripts. The code takes the file in ${OUT_DIR}/java_glue.rs and includes the contents into src/lib.rs in the build directory. The result will be as if we hand-wrote the generated code in our lib.rs file.
 */