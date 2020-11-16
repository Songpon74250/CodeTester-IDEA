package de.fxnm.listener.scanner.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import de.fxnm.listener.FeedbackListener;
import de.fxnm.service.ProjectStateService;
import de.fxnm.web.components.submission.SubmissionResult;

public class UiScannerFeedbackListener extends FeedbackListener {

    public UiScannerFeedbackListener(final Project project) {
        super(project);
    }

    @Override
    public void scanStarting(final Object... details) {
        ApplicationManager.getApplication().invokeLater(() -> {

        });
    }

    @Override
    public void scanStartingImp(final Object... details) {
        if (this.toolWindow() != null) {
            this.toolWindow().removeCheckResult();
            this.toolWindow().displayInfoMessage(true, details[0].toString());
        }

        ProjectStateService.getService(this.project()).setManualLoginLogoutConfig(false);
    }


    @Override
    public void scanCompletedImp(final Object... details) {
        if (this.toolWindow() != null) {
            this.toolWindow().displayCheckResult((SubmissionResult) details[0]);
        }

        ProjectStateService.getService(this.project()).setManualLoginLogoutConfig(true);
    }

    @Override
    public void scanFailedImp(final Object... details) {
        if (this.toolWindow() != null) {
            this.toolWindow().displayErrorMessage(false, details[0].toString());
        }
        ProjectStateService.getService(this.project()).setManualLoginLogoutConfig(true);

    }
}