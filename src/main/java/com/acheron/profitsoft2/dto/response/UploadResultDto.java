package com.acheron.profitsoft2.dto.response;

import java.util.List;

public record UploadResultDto(
        int imported,
        int failed,
        List<String> errors
) {}
