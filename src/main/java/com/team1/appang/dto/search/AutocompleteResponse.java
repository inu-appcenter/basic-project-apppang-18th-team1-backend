package com.team1.appang.dto.search;

import java.util.List;

public record AutocompleteResponse(
        String message,
        List<String> suggestions
) {
}
