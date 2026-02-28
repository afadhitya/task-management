package com.afadhitya.taskmanagement.application.port.in.search;

import com.afadhitya.taskmanagement.application.dto.response.SearchResponse;
import com.afadhitya.taskmanagement.domain.enums.SearchType;

public interface SearchUseCase {

    SearchResponse search(Long workspaceId, String query, SearchType type);
}
