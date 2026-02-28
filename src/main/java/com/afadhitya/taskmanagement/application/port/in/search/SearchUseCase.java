package com.afadhitya.taskmanagement.application.port.in.search;

import com.afadhitya.taskmanagement.application.dto.response.SearchResponse;

public interface SearchUseCase {

    SearchResponse search(Long workspaceId, String query, String type);
}
