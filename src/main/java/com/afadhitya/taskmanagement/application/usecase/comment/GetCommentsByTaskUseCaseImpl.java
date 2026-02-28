package com.afadhitya.taskmanagement.application.usecase.comment;

import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.application.mapper.CommentMapper;
import com.afadhitya.taskmanagement.application.port.in.comment.GetCommentsByTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCommentsByTaskUseCaseImpl implements GetCommentsByTaskUseCase {

    private final CommentPersistencePort commentPersistencePort;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        List<Comment> comments = commentPersistencePort.findByTaskIdAndParentCommentIsNullAndIsDeletedFalse(taskId);
        return comments.stream()
                .map(commentMapper::toResponse)
                .toList();
    }
}
