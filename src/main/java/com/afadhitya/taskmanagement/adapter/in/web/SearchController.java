package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.response.SearchResponse;
import com.afadhitya.taskmanagement.application.port.in.search.SearchUseCase;
import com.afadhitya.taskmanagement.domain.enums.SearchType;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class SearchController {

    private final SearchUseCase searchUseCase;

    @PreAuthorize("@workspaceSecurity.isWorkspaceMember(#workspaceId)")
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "workspace_id") Long workspaceId,
            @RequestParam(name = "type", required = false) SearchType type) {
        SearchResponse response = searchUseCase.search(workspaceId, query, type);
        return ResponseEntity.ok(response);
    }
}
