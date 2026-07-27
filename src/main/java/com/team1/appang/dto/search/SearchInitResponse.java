package com.team1.appang.dto.search;

import java.util.List;

public record SearchInitResponse(
        String message,
        List<String> recommendKeywords
) {
}