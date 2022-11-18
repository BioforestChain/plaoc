/// 运行的任务类型
#[derive(Clone, Copy)]
#[allow(dead_code)]
enum PromiseType {
    /// 等待
    PENDING,
    ///  完成
    FULFILLED,
    /// 拒绝
    REJECTED,
}

pub struct Promise {
    value: PromiseImpl<T>,
    reason: null,
    status: PromiseType::PENDING,
    onResolvedCallbacks: Vec<&self>,
    onRejectedCallbacks: Vec<&self>,
}

impl Promise {
    fn reoslve() {}
    fn reject() {}
    fn pending() {}
    fn then() {}
    fn catch() {}
    fn finally(&self) {}
    pub fn reoslve() {}
    pub fn reject() {}
}

/// 运行的任务类型
#[derive(Clone, Copy)]
#[allow(dead_code)]
pub enum TaskType {
    /// 任务在本地线程中运行。
    Local,
    ///  在另一个线程中异步运行。
    Async,
    /// 其他情况。
    None,
}

pub enum PromiseImpl<T: Send + 'static> {
    Pending(std::sync::mpsc::Receiver<T>),
    Ready(T),
}

 impl<T: Send + 'static> PromiseImpl<T> {
    /// 查看是否准备完
    #[allow(unused_variables)]
    fn poll_mut(&mut self, task_type: TaskType) -> std::task::Poll<&mut T> {
        match self {
            Self::Pending(rx) => {
                // 在不阻塞的情况下，在channel通道中返回一个值，有可能是空对象
                if let Ok(value) = rx.try_recv() {
                    *self = Self::Ready(value);
                    match self {
                        Self::Ready(ref mut value) => std::task::Poll::Ready(value),
                        Self::Pending(_) => unreachable!(), // 设置为无法访问，结束pending
                    }
                } else {
                    std::task::Poll::Pending
                }
            }
            // 我准备好啦
            Self::Ready(ref mut value) => std::task::Poll::Ready(value),
        }
    }

    /// 看看状态是否完成，返回已完成的promise对象或promise本身。
    fn try_take(self) -> Result<T, Self> {
        match self {
            Self::Pending(ref rx) => match rx.try_recv() {
                Ok(value) => Ok(value),
                Err(std::sync::mpsc::TryRecvError::Empty) => Err(self),
                Err(std::sync::mpsc::TryRecvError::Disconnected) => {
                    panic!("The Promise Sender was dropped")
                }
            },
            Self::Ready(value) => Ok(value),
        }
    }
    /// 查看是否准备完
    #[allow(unsafe_code)]
    #[allow(unused_variables)]
    fn poll(&self, task_type: TaskType) -> std::task::Poll<&T> {
        match self {
            Self::Pending(rx) => {
                match rx.try_recv() {
                    Ok(value) => {
                        // 只可以是Pending->Ready 的状态改变
                        unsafe {
                            let myself = self as *const Self as *mut Self;
                            *myself = Self::Ready(value);
                        }
                        match self {
                            Self::Ready(ref value) => std::task::Poll::Ready(value),
                            Self::Pending(_) => unreachable!(),
                        }
                    }
                    Err(std::sync::mpsc::TryRecvError::Empty) => std::task::Poll::Pending,
                    Err(std::sync::mpsc::TryRecvError::Disconnected) => {
                        panic!("The Promise Sender was dropped")
                    }
                }
            }
            Self::Ready(ref value) => std::task::Poll::Ready(value),
        }
    }
    /// 阻塞直到准备好 可变传递
    #[allow(unused_variables)]
    fn block_until_ready_mut(&mut self, task_type: TaskType) -> &mut T {
        match self {
            Self::Pending(rx) => {
                let value = rx.recv().expect("The Promise Sender was dropped");
                *self = Self::Ready(value);
                match self {
                    Self::Ready(ref mut value) => value,
                    Self::Pending(_) => unreachable!(),
                }
            }
            Self::Ready(ref mut value) => value,
        }
    }

    /// 阻塞直到准备好  不可变传递
    #[allow(unsafe_code)]
    #[allow(unused_variables)]
    fn block_until_ready(&self, task_type: TaskType) -> &T {
        match self {
            Self::Pending(rx) => {
                let value = rx.recv().expect("The Promise Sender was dropped");
                unsafe {
                    let myself = self as *const Self as *mut Self;
                    *myself = Self::Ready(value);
                }
                match self {
                    Self::Ready(ref value) => value,
                    Self::Pending(_) => unreachable!(),
                }
            }
            Self::Ready(ref value) => value,
        }
    }
}
