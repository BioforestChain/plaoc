#[allow(dead_code)]
use {
    futures::{
        future::{BoxFuture, FutureExt},
        task::{waker_ref, ArcWake},
    },
    std::{
        future::Future,
        sync::mpsc::{sync_channel, Receiver, SyncSender},
        sync::{Arc, Mutex},
        task::{Context}
    },
};

/// 从通道接收任务并运行它们的任务执行器。
pub struct Executor {
    ready_queue: Receiver<Arc<Task>>,
}

#[derive(Clone)]
pub struct Spawner {
    task_sender: SyncSender<Arc<Task>>,
}

struct Task {
 
    future: Mutex<Option<BoxFuture<'static, ()>>>,
    /// Handle to place the task itself back onto the task queue.
    task_sender: SyncSender<Arc<Task>>,
}

pub fn new_executor_and_spawner() -> (Executor, Spawner) {
    const MAX_QUEUED_TASKS: usize = 10_000;
    let (task_sender, ready_queue) = sync_channel(MAX_QUEUED_TASKS);
    (Executor { ready_queue }, Spawner { task_sender })
}

impl Executor {
    pub fn run(&self) {
        while let Ok(task) = self.ready_queue.recv() {
            log::info!(" executor run 🥺");
            // 取一个future，如果它还没有完成（仍然是 Some），
            // 轮询它以尝试完成它。
            let mut future_slot = task.future.lock().unwrap();
            if let Some(mut future) = future_slot.take() {
                // 创建一个waker的引用
                let waker = waker_ref(&task);
                let context = &mut Context::from_waker(&*waker);
                if future.as_mut().poll(context).is_pending() {
                    log::info!(" executor pending 🥺");
                   // 我们还没有完成对future的处理，所以放它
                    // 再次放回到它的任务中，以便将来再次运行。
                    *future_slot = Some(future);
                }else {
                    log::info!(" executor ready 🥺");
                }
            }
        }
    }
}


impl ArcWake for Task {
      fn wake_by_ref(arc_self: &Arc<Self>) {
        // 通过将此任务发送回任务通道来实现 `wake`
        // 这样它将被执行者再次轮询。
        let cloned = arc_self.clone();
        arc_self
            .task_sender
            .send(cloned)
            .expect("too many tasks queued");
    }
}

impl Spawner {
    /// 获取一个future 装入执行器的队列中
    pub fn spawn(&self, future: impl Future<Output = ()> + 'static + Send) {
        let future = future.boxed();
        let task = Arc::new(Task {
            future: Mutex::new(Some(future)),
            task_sender: self.task_sender.clone(),
        });
        self.task_sender.send(task).expect("too many tasks queued");
    }
}

