package com.kanban.controller;

import com.kanban.dto.AiCommandRequestDto;
import com.kanban.dto.AiCommandResponseDto;
import com.kanban.service.AiIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiIntegrationController implements AiIntegrationControllerApi {

    private final AiIntegrationService aiIntegrationService;

    public AiIntegrationController(AiIntegrationService aiIntegrationService) {
        this.aiIntegrationService = aiIntegrationService;
    }

    @Override
    @PostMapping("/command")
    public ResponseEntity<AiCommandResponseDto> executeCommand(@RequestBody AiCommandRequestDto request) {
        return ResponseEntity.ok(aiIntegrationService.executeCommand(request));
    }

    @Override
    @GetMapping("/tools")
    public ResponseEntity<List<Map<String, Object>>> getMcpTools() {
        return ResponseEntity.ok(aiIntegrationService.getMcpTools());
    }

    @Override
    @GetMapping("/mcp-manifest")
    public ResponseEntity<Map<String, Object>> getMcpManifest() {
        return ResponseEntity.ok(aiIntegrationService.getMcpManifest());
    }
}
