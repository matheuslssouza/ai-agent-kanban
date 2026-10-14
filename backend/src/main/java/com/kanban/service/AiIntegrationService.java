package com.kanban.service;

import com.kanban.dto.AiCommandRequestDto;
import com.kanban.dto.AiCommandResponseDto;

import java.util.List;
import java.util.Map;

public interface AiIntegrationService {

    AiCommandResponseDto executeCommand(AiCommandRequestDto request);

    Map<String, Object> getMcpManifest();

    List<Map<String, Object>> getMcpTools();
}
