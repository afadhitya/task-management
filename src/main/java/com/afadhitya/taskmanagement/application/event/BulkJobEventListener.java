package com.afadhitya.taskmanagement.application.event;

import com.afadhitya.taskmanagement.application.usecase.bulkjob.BulkJobProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BulkJobEventListener {

    private final BulkJobProcessor bulkJobProcessor;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBulkJobSubmitted(BulkJobSubmittedEvent event) {
        bulkJobProcessor.processBulkUpdateTasks(event.getJobId(), event.getRequest());
    }
}
