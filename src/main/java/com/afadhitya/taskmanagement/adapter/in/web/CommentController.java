package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.CreateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.application.port.in.comment.CreateCommentUseCase;
import com.afadhitya.taskmanagement.application.port.in.comment.DeleteCommentUseCase;
import com.afadhitya.taskmanagement.application.port.in.comment.GetCommentsByTaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.comment.UpdateCommentUseCase;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final GetCommentsByTaskUseCase getCommentsByTaskUseCase;
    private final UpdateCommentUseCase updateCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;

    @PreAuthorize("@taskSecurity.canViewTask(#taskId)")
    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CommentResponse response = createCommentUseCase.createComment(taskId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("@taskSecurity.canViewTask(#taskId)")
    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentResponse> comments = getCommentsByTaskUseCase.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("@commentSecurity.canModifyComment(#id)")
    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CommentResponse response = updateCommentUseCase.updateComment(id, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@commentSecurity.canModifyComment(#id)")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        deleteCommentUseCase.deleteComment(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
