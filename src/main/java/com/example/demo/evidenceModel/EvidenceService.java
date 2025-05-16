package com.example.demo.evidenceModel;

import org.springframework.stereotype.Service;

@Service
public class EvidenceService {

    // 1. 创建存证
    public String createEvidence(EvidenceRequest request) {
        // TODO: 实现存证创建逻辑
        return "generated-evidence-code";
    }

    // 2. 查询存证
    public EvidenceResponse getEvidence(String evidenceCode) {
        // TODO: 实现存证查询逻辑
        return new EvidenceResponse();
    }

    // 3. 确认接收
    public void confirmReceipt(ReceiptRequest request) {
        // TODO: 实现接收确认逻辑
    }

    // 4. 终止授权
    public void terminateAuthorization(TerminationRequest request) {
        // TODO: 实现授权终止逻辑
    }
}