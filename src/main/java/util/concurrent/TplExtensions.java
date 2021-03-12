// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package util.concurrent;

import com.tunnelvisionlabs.util.validation.NotNull;
import com.tunnelvisionlabs.util.validation.Requires;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Extensions to the Task Parallel Library.
 *
 * <p>Copied from Microsoft/vs-threading@14f77875.</p>
 */
public enum TplExtensions {
	;

//        /// <summary>
//        /// A singleton completed task.
//        /// </summary>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes")]
//        public static readonly Task CompletedTask = Task.FromResult(default(EmptyStruct));
//
//        /// <summary>
//        /// A task that is already canceled.
//        /// </summary>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes")]
//        public static readonly Task CanceledTask = ThreadingTools.TaskFromCanceled(new CancellationToken(canceled: true));
//
//        /// <summary>
//        /// A completed task with a <c>true</c> result.
//        /// </summary>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes")]
//        public static readonly Task<bool> TrueTask = Task.FromResult(true);
//
//        /// <summary>
//        /// A completed task with a <c>false</c> result.
//        /// </summary>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes")]
//        public static readonly Task<bool> FalseTask = Task.FromResult(false);
//
//        /// <summary>
//        /// Wait on a task without possibly inlining it to the current thread.
//        /// </summary>
//        /// <param name="task">The task to wait on.</param>
//        public static void WaitWithoutInlining(this Task task)
//        {
//            Requires.NotNull(task, nameof(task));
//            if (!task.IsCompleted)
//            {
//                // Waiting on a continuation of a task won't ever inline the predecessor (in .NET 4.x anyway).
//                var continuation = task.ContinueWith(t => { }, CancellationToken.None, TaskContinuationOptions.ExecuteSynchronously, TaskScheduler.Default);
//                continuation.Wait();
//            }
//
//            task.Wait(); // purely for exception behavior; alternatively in .NET 4.5 task.GetAwaiter().GetResult();
//        }

	/**
	 * Returns a future that completes as the original future completes or when a timeout expires, whichever happens first.
	 *
	 * @param <T> The type of value returned by the original future.
	 * @param future The future to wait for.
	 * @param timeout The maximum time to wait.
	 * @return A future that completes with the result of the specified {@code future} or fails with a {@link CancellationException} if {@code timeout} elapses first.
	 */
	public static <T> CompletableFuture<T> withTimeout(@NotNull CompletableFuture<T> future, @NotNull Duration timeout) {
		return Async.runAsync(() -> {
			Requires.notNull(future, "future");
			Requires.notNull(timeout, "timeout");

			CompletableFuture<Void> timeoutTask = Async.delayAsync(timeout);
			return Async.awaitAsync(
				Async.configureAwait(Async.whenAny(future, timeoutTask), false),
				completedFuture -> {
					if (completedFuture == timeoutTask) {
						return Futures.completedCancelled();
					}

					// The timeout did not elapse, so cancel the timer to recover system resources.
					timeoutTask.cancel(false);

					return Async.awaitAsync(Async.configureAwait(future, false));
				});
		});
	}

//        /// <summary>
//        /// Applies one task's results to another.
//        /// </summary>
//        /// <typeparam name="T">The type of value returned by a task.</typeparam>
//        /// <param name="task">The task whose completion should be applied to another.</param>
//        /// <param name="tcs">The task that should receive the completion status.</param>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", MessageId = "tcs")]
//        public static void ApplyResultTo<T>(this Task<T> task, TaskCompletionSource<T> tcs)
//        {
//            Requires.NotNull(task, nameof(task));
//            Requires.NotNull(tcs, nameof(tcs));
//
//            if (task.IsCompleted)
//            {
//                ApplyCompletedTaskResultTo(task, tcs);
//            }
//            else
//            {
//                // Using a minimum of allocations (just one task, and no closure) ensure that one task's completion sets equivalent completion on another task.
//                task.ContinueWith(
//                    (t, s) => ApplyCompletedTaskResultTo(t, (TaskCompletionSource<T>)s),
//                    tcs,
//                    CancellationToken.None,
//                    TaskContinuationOptions.ExecuteSynchronously,
//                    TaskScheduler.Default);
//            }
//        }
//
//        /// <summary>
//        /// Applies one task's results to another.
//        /// </summary>
//        /// <typeparam name="T">The type of value returned by a task.</typeparam>
//        /// <param name="task">The task whose completion should be applied to another.</param>
//        /// <param name="tcs">The task that should receive the completion status.</param>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", MessageId = "tcs")]
//        public static void ApplyResultTo<T>(this Task task, TaskCompletionSource<T> tcs)
//        {
//            Requires.NotNull(task, nameof(task));
//            Requires.NotNull(tcs, nameof(tcs));
//
//            if (task.IsCompleted)
//            {
//                ApplyCompletedTaskResultTo<T>(task, tcs, default(T));
//            }
//            else
//            {
//                // Using a minimum of allocations (just one task, and no closure) ensure that one task's completion sets equivalent completion on another task.
//                task.ContinueWith(
//                    (t, s) => ApplyCompletedTaskResultTo(t, (TaskCompletionSource<T>)s, default(T)),
//                    tcs,
//                    CancellationToken.None,
//                    TaskContinuationOptions.ExecuteSynchronously,
//                    TaskScheduler.Default);
//            }
//        }
//
//        /// <summary>
//        /// Creates a task that is attached to the parent task, but produces the same result as an existing task.
//        /// </summary>
//        /// <typeparam name="T">The type of value produced by the task.</typeparam>
//        /// <param name="task">The task to wrap with an AttachedToParent task.</param>
//        /// <returns>A task that is attached to parent.</returns>
//        public static Task<T> AttachToParent<T>(this Task<T> task)
//        {
//            Requires.NotNull(task, nameof(task));
//
//            var tcs = new TaskCompletionSource<T>(TaskCreationOptions.AttachedToParent);
//            task.ApplyResultTo(tcs);
//            return tcs.Task;
//        }
//
//        /// <summary>
//        /// Creates a task that is attached to the parent task, but produces the same result as an existing task.
//        /// </summary>
//        /// <param name="task">The task to wrap with an AttachedToParent task.</param>
//        /// <returns>A task that is attached to parent.</returns>
//        public static Task AttachToParent(this Task task)
//        {
//            Requires.NotNull(task, nameof(task));
//
//            var tcs = new TaskCompletionSource<EmptyStruct>(TaskCreationOptions.AttachedToParent);
//            task.ApplyResultTo(tcs);
//            return tcs.Task;
//        }
//
//        /// <summary>
//        /// Schedules some action for execution at the conclusion of a task, regardless of the task's outcome.
//        /// </summary>
//        /// <param name="task">The task that should complete before the posted <paramref name="action"/> is invoked.</param>
//        /// <param name="action">The action to execute after <paramref name="task"/> has completed.</param>
//        /// <param name="options">The task continuation options to apply.</param>
//        /// <param name="cancellation">The cancellation token that signals the continuation should not execute (if it has not already begun).</param>
//        /// <returns>
//        /// The task that will execute the action.
//        /// </returns>
//        public static Task AppendAction(this Task task, Action action, TaskContinuationOptions options = TaskContinuationOptions.None, CancellationToken cancellation = default(CancellationToken))
//        {
//            Requires.NotNull(task, nameof(task));
//            Requires.NotNull(action, nameof(action));
//
//            return task.ContinueWith((t, state) => ((Action)state)(), action, cancellation, options, TaskScheduler.Default);
//        }
//
//        /// <summary>
//        /// Gets a task that will eventually produce the result of another task, when that task finishes.
//        /// If that task is instead canceled, its successor will be followed for its result, iteratively.
//        /// </summary>
//        /// <typeparam name="T">The type of value returned by the task.</typeparam>
//        /// <param name="taskToFollow">The task whose result should be returned by the following task.</param>
//        /// <param name="ultimateCancellation">A token whose cancellation signals that the following task should be cancelled.</param>
//        /// <param name="taskThatFollows">The TaskCompletionSource whose task is to follow.  Leave at <c>null</c> for a new task to be created.</param>
//        /// <returns>The following task.</returns>
//        public static Task<T> FollowCancelableTaskToCompletion<T>(Func<Task<T>> taskToFollow, CancellationToken ultimateCancellation, TaskCompletionSource<T> taskThatFollows = null)
//        {
//            Requires.NotNull(taskToFollow, nameof(taskToFollow));
//
//            var tcs = new TaskCompletionSource<FollowCancelableTaskState<T>, T>(
//                    new FollowCancelableTaskState<T>(taskToFollow, ultimateCancellation));
//
//            if (ultimateCancellation.CanBeCanceled)
//            {
//                var sourceState = tcs.SourceState;
//                sourceState.RegisteredCallback = ultimateCancellation.Register(
//                    state =>
//                    {
//                        var tuple = (Tuple<TaskCompletionSource<FollowCancelableTaskState<T>, T>, CancellationToken>)state;
//                        tuple.Item1.TrySetCanceled(tuple.Item2);
//                    },
//                    Tuple.Create(tcs, ultimateCancellation));
//                tcs.SourceState = sourceState; // copy back in, since it's a struct
//            }
//
//            FollowCancelableTaskToCompletionHelper(tcs, taskToFollow());
//
//            if (taskThatFollows == null)
//            {
//                return tcs.Task;
//            }
//            else
//            {
//                tcs.Task.ApplyResultTo(taskThatFollows);
//                return taskThatFollows.Task;
//            }
//        }

	/**
	 * Returns an awaitable for the specified future that will never throw, even if the source future fails or is
	 * canceled.
	 *
	 * @param future The future whose completion should signal the completion of the returned awaitable.
	 * @return An awaitable.
	 */
	public static NoThrowFutureAwaitable noThrowAwaitable(@NotNull CompletableFuture<?> future) {
		return noThrowAwaitable(future, true);
	}

	/**
	 * Returns an awaitable for the specified future that will never throw, even if the source future fails or is
	 * canceled.
	 *
	 * @param future The future whose completion should signal the completion of the returned awaitable.
	 * @param captureContext if set to {@code true} the continuation will be scheduled on the caller's context;
	 * {@code false} to always execute the continuation on the thread pool.
	 * @return An awaitable.
	 */
	public static NoThrowFutureAwaitable noThrowAwaitable(@NotNull CompletableFuture<?> future, boolean captureContext) {
		return new NoThrowFutureAwaitable(future, captureContext);
	}

	/**
	 * Consumes a future and doesn't do anything with it. Useful for fire-and-forget calls to async methods within async
	 * methods.
	 *
	 * @param future The future whose result is to be ignored.
	 */
	public static void forget(CompletableFuture<?> future) {
	}

//        /// <summary>
//        /// Invokes asynchronous event handlers, returning a task that completes when all event handlers have been invoked.
//        /// Each handler is fully executed (including continuations) before the next handler in the list is invoked.
//        /// </summary>
//        /// <param name="handlers">The event handlers.  May be <c>null</c></param>
//        /// <param name="sender">The event source.</param>
//        /// <param name="args">The event argument.</param>
//        /// <returns>The task that completes when all handlers have completed.</returns>
//        /// <exception cref="AggregateException">Thrown if any handlers fail. It contains a collection of all failures.</exception>
//        public static async Task InvokeAsync(this AsyncEventHandler handlers, object sender, EventArgs args)
//        {
//            if (handlers != null)
//            {
//                var individualHandlers = handlers.GetInvocationList();
//                List<Exception> exceptions = null;
//                foreach (AsyncEventHandler handler in individualHandlers)
//                {
//                    try
//                    {
//                        await handler(sender, args);
//                    }
//                    catch (Exception ex)
//                    {
//                        if (exceptions == null)
//                        {
//                            exceptions = new List<Exception>(2);
//                        }
//
//                        exceptions.Add(ex);
//                    }
//                }
//
//                if (exceptions != null)
//                {
//                    throw new AggregateException(exceptions);
//                }
//            }
//        }
//
//        /// <summary>
//        /// Invokes asynchronous event handlers, returning a task that completes when all event handlers have been invoked.
//        /// Each handler is fully executed (including continuations) before the next handler in the list is invoked.
//        /// </summary>
//        /// <typeparam name="TEventArgs">The type of argument passed to each handler.</typeparam>
//        /// <param name="handlers">The event handlers.  May be <c>null</c></param>
//        /// <param name="sender">The event source.</param>
//        /// <param name="args">The event argument.</param>
//        /// <returns>The task that completes when all handlers have completed.  The task is faulted if any handlers throw an exception.</returns>
//        /// <exception cref="AggregateException">Thrown if any handlers fail. It contains a collection of all failures.</exception>
//        public static async Task InvokeAsync<TEventArgs>(this AsyncEventHandler<TEventArgs> handlers, object sender, TEventArgs args)
//            where TEventArgs : EventArgs
//        {
//            if (handlers != null)
//            {
//                var individualHandlers = handlers.GetInvocationList();
//                List<Exception> exceptions = null;
//                foreach (AsyncEventHandler<TEventArgs> handler in individualHandlers)
//                {
//                    try
//                    {
//                        await handler(sender, args);
//                    }
//                    catch (Exception ex)
//                    {
//                        if (exceptions == null)
//                        {
//                            exceptions = new List<Exception>(2);
//                        }
//
//                        exceptions.Add(ex);
//                    }
//                }
//
//                if (exceptions != null)
//                {
//                    throw new AggregateException(exceptions);
//                }
//            }
//        }
//
//        /// <summary>
//        /// Converts a TPL task to the APM Begin-End pattern.
//        /// </summary>
//        /// <typeparam name="TResult">The result value to be returned from the End method.</typeparam>
//        /// <param name="task">The task that came from the async method.</param>
//        /// <param name="callback">The optional callback to invoke when the task is completed.</param>
//        /// <param name="state">The state object provided by the caller of the Begin method.</param>
//        /// <returns>A task (that implements <see cref="IAsyncResult"/> that should be returned from the Begin method.</returns>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", MessageId = "Apm")]
//        public static Task<TResult> ToApm<TResult>(this Task<TResult> task, AsyncCallback callback, object state)
//        {
//            Requires.NotNull(task, nameof(task));
//
//            if (task.AsyncState == state)
//            {
//                if (callback != null)
//                {
//                    task.ContinueWith(
//                        (t, cb) => ((AsyncCallback)cb)(t),
//                        callback,
//                        CancellationToken.None,
//                        TaskContinuationOptions.None,
//                        TaskScheduler.Default);
//                }
//
//                return task;
//            }
//
//            var tcs = new TaskCompletionSource<TResult>(state);
//            task.ContinueWith(
//                t =>
//                {
//                    ApplyCompletedTaskResultTo(t, tcs);
//
//                    if (callback != null)
//                    {
//                        callback(tcs.Task);
//                    }
//                },
//                CancellationToken.None,
//                TaskContinuationOptions.None,
//                TaskScheduler.Default);
//
//            return tcs.Task;
//        }
//
//        /// <summary>
//        /// Converts a TPL task to the APM Begin-End pattern.
//        /// </summary>
//        /// <param name="task">The task that came from the async method.</param>
//        /// <param name="callback">The optional callback to invoke when the task is completed.</param>
//        /// <param name="state">The state object provided by the caller of the Begin method.</param>
//        /// <returns>A task (that implements <see cref="IAsyncResult"/> that should be returned from the Begin method.</returns>
//        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", MessageId = "Apm")]
//        public static Task ToApm(this Task task, AsyncCallback callback, object state)
//        {
//            Requires.NotNull(task, nameof(task));
//
//            if (task.AsyncState == state)
//            {
//                if (callback != null)
//                {
//                    task.ContinueWith(
//                        (t, cb) => ((AsyncCallback)cb)(t),
//                        callback,
//                        CancellationToken.None,
//                        TaskContinuationOptions.None,
//                        TaskScheduler.Default);
//                }
//
//                return task;
//            }
//
//            var tcs = new TaskCompletionSource<object>(state);
//            task.ContinueWith(
//                t =>
//                {
//                    ApplyCompletedTaskResultTo(t, tcs, null);
//
//                    if (callback != null)
//                    {
//                        callback(tcs.Task);
//                    }
//                },
//                CancellationToken.None,
//                TaskContinuationOptions.None,
//                TaskScheduler.Default);
//
//            return tcs.Task;
//        }
//
//        /// <summary>
//        /// Applies a completed task's results to another.
//        /// </summary>
//        /// <typeparam name="T">The type of value returned by a task.</typeparam>
//        /// <param name="completedTask">The task whose completion should be applied to another.</param>
//        /// <param name="taskCompletionSource">The task that should receive the completion status.</param>
//        private static void ApplyCompletedTaskResultTo<T>(Task<T> completedTask, TaskCompletionSource<T> taskCompletionSource)
//        {
//            Assumes.NotNull(completedTask);
//            Assumes.True(completedTask.IsCompleted);
//            Assumes.NotNull(taskCompletionSource);
//
//            if (completedTask.IsCanceled)
//            {
//                // NOTE: this is "lossy" in that we don't propagate any CancellationToken that the Task would throw an OperationCanceledException with.
//                // Propagating that data would require that we actually cause the completedTask to throw so we can inspect the
//                // OperationCanceledException.CancellationToken property, which we consider more costly than it's worth.
//                taskCompletionSource.TrySetCanceled();
//            }
//            else if (completedTask.IsFaulted)
//            {
//                taskCompletionSource.TrySetException(completedTask.Exception.InnerExceptions);
//            }
//            else
//            {
//                taskCompletionSource.TrySetResult(completedTask.Result);
//            }
//        }
//
//        /// <summary>
//        /// Applies a completed task's results to another.
//        /// </summary>
//        /// <typeparam name="T">The type of value returned by a task.</typeparam>
//        /// <param name="completedTask">The task whose completion should be applied to another.</param>
//        /// <param name="taskCompletionSource">The task that should receive the completion status.</param>
//        /// <param name="valueOnRanToCompletion">The value to set on the completion source when the source task runs to completion.</param>
//        private static void ApplyCompletedTaskResultTo<T>(Task completedTask, TaskCompletionSource<T> taskCompletionSource, T valueOnRanToCompletion)
//        {
//            Assumes.NotNull(completedTask);
//            Assumes.True(completedTask.IsCompleted);
//            Assumes.NotNull(taskCompletionSource);
//
//            if (completedTask.IsCanceled)
//            {
//                // NOTE: this is "lossy" in that we don't propagate any CancellationToken that the Task would throw an OperationCanceledException with.
//                // Propagating that data would require that we actually cause the completedTask to throw so we can inspect the
//                // OperationCanceledException.CancellationToken property, which we consider more costly than it's worth.
//                taskCompletionSource.TrySetCanceled();
//            }
//            else if (completedTask.IsFaulted)
//            {
//                taskCompletionSource.TrySetException(completedTask.Exception.InnerExceptions);
//            }
//            else
//            {
//                taskCompletionSource.TrySetResult(valueOnRanToCompletion);
//            }
//        }
//
//        /// <summary>
//        /// Gets a task that will eventually produce the result of another task, when that task finishes.
//        /// If that task is instead canceled, its successor will be followed for its result, iteratively.
//        /// </summary>
//        /// <typeparam name="T">The type of value returned by the task.</typeparam>
//        /// <param name="tcs">The TaskCompletionSource whose task is to follow.</param>
//        /// <param name="currentTask">The current task.</param>
//        /// <returns>
//        /// The following task.
//        /// </returns>
//        private static Task<T> FollowCancelableTaskToCompletionHelper<T>(TaskCompletionSource<FollowCancelableTaskState<T>, T> tcs, Task<T> currentTask)
//        {
//            Requires.NotNull(tcs, nameof(tcs));
//            Requires.NotNull(currentTask, nameof(currentTask));
//
//            currentTask.ContinueWith(
//                (t, state) =>
//                {
//                    var tcsNested = (TaskCompletionSource<FollowCancelableTaskState<T>, T>)state;
//                    switch (t.Status)
//                    {
//                        case TaskStatus.RanToCompletion:
//                            tcsNested.TrySetResult(t.Result);
//                            tcsNested.SourceState.RegisteredCallback.Dispose();
//                            break;
//                        case TaskStatus.Faulted:
//                            tcsNested.TrySetException(t.Exception.InnerExceptions);
//                            tcsNested.SourceState.RegisteredCallback.Dispose();
//                            break;
//                        case TaskStatus.Canceled:
//                            var newTask = tcsNested.SourceState.CurrentTask;
//                            Assumes.True(newTask != t, "A canceled task was not replaced with a new task.");
//                            FollowCancelableTaskToCompletionHelper(tcsNested, newTask);
//                            break;
//                    }
//                },
//                tcs,
//                tcs.SourceState.UltimateCancellation,
//                TaskContinuationOptions.ExecuteSynchronously,
//                TaskScheduler.Default);
//
//            return tcs.Task;
//        }

	/**
	 * An awaitable that wraps a future and never throws an exception when waited on.
	 */
	public static final class NoThrowFutureAwaitable implements Awaitable<Void> {

		/**
		 * The future.
		 */
		private final CompletableFuture<?> task;

		/**
		 * A value indicating whether the continuation should be scheduled on the current sync context.
		 */
		private final boolean captureContext;

		/**
		 * Constructs a new instance of the {@link NoThrowTaskAwaitable} class.
		 *
		 * @param future The future.
		 * @param captureContext Whether the continuation should be scheduled on the current sync context.
		 */
		public NoThrowFutureAwaitable(@NotNull CompletableFuture<?> future, boolean captureContext) {
			Requires.notNull(future, "task");
			this.task = future;
			this.captureContext = captureContext;
		}

		/**
		 * Gets the awaiter.
		 *
		 * @return The awaiter.
		 */
		@Override
		public NoThrowFutureAwaiter getAwaiter() {
			return new NoThrowFutureAwaiter(this.task, this.captureContext);
		}
	}

	/**
	 * An awaiter that wraps a future and never throws an exception when waited on.
	 */
	public static final class NoThrowFutureAwaiter implements Awaiter<Void>, CriticalNotifyCompletion {

		/**
		 * The future.
		 */
		private final CompletableFuture<?> task;

		/**
		 * A value indicating whether the continuation should be scheduled on the current sync context.
		 */
		private final boolean captureContext;

		/**
		 * Constructs a new instance of the {@link NoThrowTaskAwaiter} class.
		 *
		 * @param task The future.
		 * @param captureContext if set to {@code true} [capture context].
		 */
		public NoThrowFutureAwaiter(@NotNull CompletableFuture<?> task, boolean captureContext) {
			Requires.notNull(task, "future");
			this.task = task;
			this.captureContext = captureContext;
		}

		/**
		 * Gets a value indicating whether the future has completed.
		 */
		@Override
		public boolean isDone() {
			return this.task.isDone();
		}

		/**
		 * Schedules a delegate for execution at the conclusion of a future's execution.
		 *
		 * @param continuation The action.
		 */
		@Override
		public void onCompleted(Runnable continuation) {
			new FutureAwaitable<>(task, captureContext).getAwaiter().onCompleted(continuation);
		}

		/**
		 * Schedules a delegate for execution at the conclusion of a future's execution.
		 *
		 * @param continuation The action.
		 */
		@Override
		public void unsafeOnCompleted(Runnable continuation) {
			new FutureAwaitable<>(task, captureContext).getAwaiter().unsafeOnCompleted(continuation);
		}

		/**
		 * Does nothing.
		 */
		@Override
		public Void getResult() {
			// Never throw here.
			return null;
		}
	}

//        /// <summary>
//        /// A state bag for the <see cref="FollowCancelableTaskToCompletion"/> method.
//        /// </summary>
//        /// <typeparam name="T">The type of value ultimately returned.</typeparam>
//        private struct FollowCancelableTaskState<T>
//        {
//            /// <summary>
//            /// The delegate that returns the task to follow.
//            /// </summary>
//            private readonly Func<Task<T>> getTaskToFollow;
//
//            /// <summary>
//            /// Initializes a new instance of the <see cref="FollowCancelableTaskState{T}"/> struct.
//            /// </summary>
//            /// <param name="getTaskToFollow">The get task to follow.</param>
//            /// <param name="cancellationToken">The cancellation token.</param>
//            internal FollowCancelableTaskState(Func<Task<T>> getTaskToFollow, CancellationToken cancellationToken)
//                : this()
//            {
//                Requires.NotNull(getTaskToFollow, nameof(getTaskToFollow));
//
//                this.getTaskToFollow = getTaskToFollow;
//                this.UltimateCancellation = cancellationToken;
//            }
//
//            /// <summary>
//            /// Gets the ultimate cancellation token.
//            /// </summary>
//            internal CancellationToken UltimateCancellation { get; private set; }
//
//            /// <summary>
//            /// Gets or sets the cancellation token registration to dispose of when the task completes normally.
//            /// </summary>
//            internal CancellationTokenRegistration RegisteredCallback { get; set; }
//
//            /// <summary>
//            /// Gets the current task to follow.
//            /// </summary>
//            internal Task<T> CurrentTask
//            {
//                get
//                {
//                    var task = this.getTaskToFollow();
//                    Assumes.NotNull(task);
//                    return task;
//                }
//            }
//        }
//
//        /// <summary>
//        /// A task completion source that contains additional state.
//        /// </summary>
//        /// <typeparam name="TState">The type of the state.</typeparam>
//        /// <typeparam name="TResult">The type of the result.</typeparam>
//        private class TaskCompletionSource<TState, TResult> : TaskCompletionSource<TResult>
//        {
//            /// <summary>
//            /// Initializes a new instance of the <see cref="TaskCompletionSource{TState, TResult}" /> class.
//            /// </summary>
//            /// <param name="sourceState">The state to store in the <see cref="SourceState" /> property.</param>
//            /// <param name="taskState">State of the task.</param>
//            /// <param name="options">The options.</param>
//            internal TaskCompletionSource(TState sourceState, object taskState = null, TaskCreationOptions options = TaskCreationOptions.None)
//                : base(taskState, options)
//            {
//                this.SourceState = sourceState;
//            }
//
//            /// <summary>
//            /// Gets or sets the state passed into the constructor.
//            /// </summary>
//            internal TState SourceState { get; set; }
//        }
}
