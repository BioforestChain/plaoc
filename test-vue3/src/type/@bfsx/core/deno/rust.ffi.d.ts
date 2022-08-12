declare const Rust: Deno.StaticForeignLibraryInterface<{
    send_buffer: {
        parameters: ("usize" | "pointer")[];
        result: "void";
    };
}>;
export default Rust;
