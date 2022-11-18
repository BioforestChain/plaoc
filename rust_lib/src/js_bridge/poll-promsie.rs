/// 参考：https://github.com/EmbarkStudios/poll-promise/blob/main/src/promise.rs
/// 用于将结果发送到 [`Promise`]。
#[must_use = "You should call Sender::send with the result"]
pub struct Sender<T>(std::sync::mpsc::Sender<T>);

impl<T> Sender<T> {
    /// 将结果发送到 [`Promise`]。
    ///
    /// 如果 [`Promise`] 已删除，则此操作不执行任何操作。
    pub fn send(self, value: T) {
        self.0.send(value).ok(); // 忽略接收器被丢弃引起的错误
    }
}

#[must_use]
pub struct Promise<T: Send + 'static> {
    value: PromiseImpl<T>,
    task_type: TaskType,

    join_handle: Option<tokio::task::JoinHandle<()>>,
}
// 确保 Promise 是异步的
static_assertions::assert_not_impl_all!(Promise<u32>: Sync);
static_assertions::assert_impl_all!(Promise<u32>: Send);

impl<T: Send + 'static> Promise<T> {
    /// 创建一个 [`Promise`] 和一个相应的 [`Sender`]。
    pub fn new() -> (Sender<T>, Self) {
        // 我们需要一个可以等待阻塞的通道（对于 `Self::block_until_ready`）。
        //（`tokio::sync::oneshot` 不支持阻塞接收）。
        let (tx, rx) = std::sync::mpsc::channel();
        (
            Sender(tx),
            Self {
                value: PromiseImpl::Pending(rx),
                task_type: TaskType::None,
                join_handle: None,
            },
        )
    }

    /// 创建一个已经完成的promise
    pub fn from_ready(value: T) -> Self {
        Self {
            value: PromiseImpl::Ready(value),
            task_type: TaskType::None,
            join_handle: None,
        }
    }

    /// 产生一个future。并发运行任务。
    /// ＃＃ 例子
    ///
    /// ``` no_run
    /// # async fn something_async() {}
    /// # 使用 js_bridge::Promise;
    /// let promise = Promise::spawn_async(async move { something_async().await });
    ///```
    pub fn spawn_async(future: impl std::future::Future<Output = T> + 'static + Send) -> Self {
        let (sender, mut promise) = Self::new();
        promise.task_type = TaskType::Async;

        {
            promise.join_handle =
                Some(tokio::task::spawn(async move { sender.send(future.await) }));
        }

        promise
    }

    /// 产生一个future。 在本地线程中运行它。
    /// ## Example
    /// ``` no_run
    /// # async fn something_async() {}
    /// # use js_bridge::Promise;
    /// let promise = Promise::spawn_local(async move { something_async().await });
    /// ```
    pub fn spawn_local(future: impl std::future::Future<Output = T> + 'static) -> Self {
        #[allow(unused_mut)]
        let (sender, mut promise) = Self::new();
        promise.task_type = TaskType::Local;

        {
            promise.join_handle = Some(tokio::task::spawn_local(async move {
                sender.send(future.await);
            }));
        }

        promise
    }

    /// 在后台任务中产生一个阻塞闭包。https://docs.rs/tokio/latest/tokio/task/fn.spawn_blocking.html
    /// ``` no_run
    /// # fn something_cpu_intensive() {}
    /// # use js_bridge::Promise;
    /// let promise = Promise::spawn_blocking(move || something_cpu_intensive());
    /// ```
    pub fn spawn_blocking<F>(f: F) -> Self
    where
        F: FnOnce() -> T + Send + 'static,
    {
        let (sender, mut promise) = Self::new();
        promise.join_handle = Some(tokio::task::spawn(async move {
            sender.send(tokio::task::block_in_place(f));
        }));
        promise
    }

    /// 在后台线程中产生一个阻塞闭包。
    /// ```
    /// # fn something_slow() {}
    /// # use js_bridge::Promise;
    /// let promise = Promise::spawn_thread("slow_operation", move || something_slow());
    /// ```
    #[cfg(not(target_arch = "wasm32"))] // can't spawn threads in wasm.
    pub fn spawn_thread<F>(thread_name: impl Into<String>, f: F) -> Self
    where
        F: FnOnce() -> T + Send + 'static,
    {
        let (sender, promise) = Self::new();
        std::thread::Builder::new()
            .name(thread_name.into())
            .spawn(move || sender.send(f()))
            .expect("Failed to spawn thread");
        promise
    }
    /// 轮询promise并返回对数据的引用。或者返回[`None`]
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn ready(&self) -> Option<&T> {
        match self.poll() {
            std::task::Poll::Pending => None,
            std::task::Poll::Ready(value) => Some(value),
        }
    }

    /// / 轮询promise并返回对数据的引用。或者返回[`None`]
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn ready_mut(&mut self) -> Option<&mut T> {
        match self.poll_mut() {
            std::task::Poll::Pending => None,
            std::task::Poll::Ready(value) => Some(value),
        }
    }

    /// 返回完成的承诺对象或承诺本身（如果尚未完成）。
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn try_take(self) -> Result<T, Self> {
        self.value.try_take().map_err(|value| Self {
            value,
            task_type: self.task_type,
            join_handle: self.join_handle,
        })
    }

    /// 阻塞执行直到准备好，然后返回对值的引用。
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn block_until_ready(&self) -> &T {
        self.value.block_until_ready(self.task_type)
    }

    /// 阻塞执行直到准备好，然后返回对值的引用。
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn block_until_ready_mut(&mut self) -> &mut T {
        self.value.block_until_ready_mut(self.task_type)
    }

    /// 阻塞执行直到准备就绪，然后返回承诺的值并使用 `Promise`。
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn block_and_take(self) -> T {
        self.value.block_until_ready(self.task_type);
        match self.value {
            PromiseImpl::Pending(_) => unreachable!(),
            PromiseImpl::Ready(value) => value,
        }
    }

    /// 返回对就绪值的引用 [`std::task::Poll::Ready`]
    /// or [`std::task::Poll::Pending`].
    ///
    ///如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn poll(&self) -> std::task::Poll<&T> {
        self.value.poll(self.task_type)
    }

    /// 返回对 [`std::task::Poll::Ready`] 中就绪值的 mut 引用
    /// 或 [`std::task::Poll::Pending`]。
    ///
    /// 如果连接的 [`Sender`] 在发送值之前被丢弃，则会出现恐慌。
    pub fn poll_mut(&mut self) -> std::task::Poll<&mut T> {
        self.value.poll_mut(self.task_type)
    }

    /// 返回运行的任务类型
    /// See [`TaskType`].
    pub fn task_type(&self) -> TaskType {
        self.task_type
    }

    /// 中止生成的正在运行的任务 [`Self::spawn_async`].
    pub fn abort(self) {
        if let Some(join_handle) = self.join_handle {
            join_handle.abort();
        }
    }
}
