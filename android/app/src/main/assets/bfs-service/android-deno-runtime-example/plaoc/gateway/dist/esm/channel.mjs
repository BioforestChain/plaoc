const createChannel = async () => {
  try {
    const server = Deno.listenDatagram({ port: 8080, transport: "udp" });
    console.log(`Access it at:  http://localhost:8080/`, server);
  } catch (error) {
    console.warn("Error", error);
  }
};
export { createChannel };
//# sourceMappingURL=channel.mjs.map
