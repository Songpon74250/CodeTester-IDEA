package de.fxnm.callable;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;

import de.fxnm.listener.FeedbackListener;

public abstract class BaseCallable<T> implements Callable<T> {

    private static final Logger LOG = Logger.getInstance(BaseCallable.class);

    private final Project project;
    private final Set<FeedbackListener> listeners = new CopyOnWriteArraySet<>();
    private Boolean finished = false;

    public BaseCallable(final Project project) {
        this.project = project;
    }

    public Project project() {
        return this.project;
    }

    public void addListener(final FeedbackListener listener) {
        this.listeners.add(listener);
    }

    public void startCallable(final String processName, final Object... details) {
        LOG.info("Started Callable");
        this.backgroundLoadingBar(processName);
        this.listeners.forEach(listener -> listener.scanStarting(details));
    }

    public void finishedCallable(final Object... details) {
        LOG.info("Finished Callable successful");
        this.finished = true;
        this.listeners.forEach(listener -> listener.scanCompleted(details));
    }

    public void failedCallable(final Object... details) {
        LOG.info("Finished Callable with error");
        this.finished = true;
        this.listeners.forEach(listener -> listener.scanFailed(details));
    }

    private void backgroundLoadingBar(final String processName) {
        ProgressManager.getInstance().run(new Task.Backgroundable(this.project(), processName) {
            public void run(@NotNull final ProgressIndicator progressIndicator) {
                while (!BaseCallable.this.finished) {
                    progressIndicator.setIndeterminate(true);
                }
            }
        });
    }
}