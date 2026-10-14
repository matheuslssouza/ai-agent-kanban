package com.kanban.controller;

import com.kanban.dto.AiCommandRequestDto;
import com.kanban.dto.AiCommandResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Tag(name = "AI & MCP Integration", description = "Endpoints for AI agents, MCP tools, and natural language card control")
public interface AiIntegrationControllerApi {

    @Operation(summary = "Execute an AI command or natural language instruction")
    ResponseEntity<AiCommandResponseDto> executeCommand(AiCommandRequestDto request);

    @Operation(summary = "Get Model Context Protocol (MCP) tool definitions")
    ResponseEntity<List<Map<String, Object>>> getMcpTools();

    @Operation(summary = "Get MCP server manifest")
    ResponseEntity<Map<String, Object>> getMcpManifest();
}
