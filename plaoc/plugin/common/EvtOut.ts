/// <reference lib="dom"/>

export class EvtOut<T> {
  #controller!: ReadableStreamController<T>;
  #stream = new ReadableStream<T>({
    start: (controller) => (this.#controller = controller),
  });
  emit(data: T) {
    this.#controller.enqueue(data);
  }

  async *#toAsyncGenerator(reader: ReadableStreamDefaultReader<T>) {
    do {
      const result = await reader.read();
      if (result.done) {
        break;
      }
      yield result.value!;
    } while (true);
  }
  toAsyncGenerator(): Evt<T> {
    const stream = this.#stream.tee()[1];
    const reader = stream.getReader();
    const ag = this.#toAsyncGenerator(reader);
    const _return = ag.return;
    ag.return = (value) => {
      reader.cancel();
      return _return.call(ag, value);
    };

    const _throw = ag.throw;
    ag.throw = (e) => {
      reader.cancel();
      return _throw.call(ag, e);
    };
    return ag;
  }
}
export type Evt<T = unknown> = AsyncGenerator<
  Awaited<NonNullable<T>>,
  void,
  void
>;
