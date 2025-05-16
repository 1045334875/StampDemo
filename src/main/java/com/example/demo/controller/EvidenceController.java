package com.example.demo.controller;

import com.example.demo.evidenceModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    // 1. 授权存证接口
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createEvidence(@RequestBody EvidenceRequest request) {
        // 调用服务方法创建存证
        String evidenceCode = evidenceService.createEvidence(request);
        return ResponseEntity.ok(Map.of("evidenceCode", evidenceCode));
    }

    // 2. 存证查询接口
    @GetMapping("/{evidenceCode}")
    public ResponseEntity<EvidenceResponse> getEvidence(@PathVariable String evidenceCode) {
        // 调用服务方法查询存证
        EvidenceResponse evidence = evidenceService.getEvidence(evidenceCode);
        if (evidence == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(evidence);
    }

    // 3. 接收确认接口
    @PostMapping("/receive")
    public ResponseEntity<Map<String, String>> confirmReceipt(@RequestBody ReceiptRequest request) {
        // 调用服务方法确认接收
        evidenceService.confirmReceipt(request);
        return ResponseEntity.ok(Map.of("message", "Receipt confirmed"));
    }

    // 4. 授权终止接口
    @PostMapping("/terminate")
    public ResponseEntity<Map<String, String>> terminateAuthorization(@RequestBody TerminationRequest request) {
        // 调用服务方法终止授权
        evidenceService.terminateAuthorization(request);
        return ResponseEntity.ok(Map.of("message", "Terminated"));
    }
}