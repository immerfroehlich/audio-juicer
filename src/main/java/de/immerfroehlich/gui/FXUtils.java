package de.immerfroehlich.gui;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

public class FXUtils {

    private static class ThrowableWrapper {
		Throwable t;
	}
    
    /**
	 * Invokes a Runnable in JFX Thread and waits while it's finished. Like
	 * SwingUtilities.invokeAndWait does for EDT.
	 * 
	 * @param run
	 *            The Runnable that has to be called on JFX thread.
	 * @throws InterruptedException
	 *             f the execution is interrupted.
	 * @throws ExecutionException
	 *             If a exception is occurred in the run method of the Runnable
	 */
    public static void runAndWait(final Runnable run) {
    	try {
			runAndWaitImpl(run);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }

	private static void runAndWaitImpl(final Runnable run)
			throws InterruptedException, ExecutionException {
		if (Platform.isFxApplicationThread()) {
			try {
				run.run();
			} catch (Exception e) {
				throw new ExecutionException(e);
			}
		} else {
			final Lock lock = new ReentrantLock();
			final Condition condition = lock.newCondition();
			final ThrowableWrapper throwableWrapper = new ThrowableWrapper();
			lock.lock();
			try {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						lock.lock();
						try {
							run.run();
						} catch (Throwable e) {
							throwableWrapper.t = e;
						} finally {
							try {
								condition.signal();
							} finally {
								lock.unlock();
							}
						}
					}
				});
				condition.await();
				if (throwableWrapper.t != null) {
					throw new ExecutionException(throwableWrapper.t);
				}
			} finally {
				lock.unlock();
			}
		}
	}
	
	public static <R> Service<R> createService(Task<R> task, BiConsumer<WorkerStateEvent, Service<R>> onSuccededCallback) {
		Service<R> service = new Service<R>() {
			@Override
			protected Task<R> createTask() {
				return task;
			}
		};
		
		ApplicationContext.bindProgressPropertyToProgressBar(service.progressProperty());
		ApplicationContext.showProgressOverlay();
		
		service.setOnSucceeded((e) -> {
			onSuccededCallback.accept(e, service);
			ApplicationContext.hideProgressOverlay();
		});
	
		return service;
	}
	
}