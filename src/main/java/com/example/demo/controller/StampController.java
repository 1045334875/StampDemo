package com.example.demo.controller;

import com.example.demo.model.Document;
import com.example.demo.model.SetDefaultStampRequest;
import com.example.demo.model.StampRequest;
import com.example.demo.service.PdfService;
//import com.example.demo.model.StampDTO;
import com.example.demo.model.Stamp;
import com.example.demo.service.StampService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/stamp")
public class StampController {
    @Autowired
    private StampService stampService;

    @PostMapping("/create")
    public ResponseEntity<Stamp> createStamp(@RequestBody Stamp stamp) {
        if(stamp.getHandwritten()==null){
            try {
                String image = stampService.createStampImage(stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText());
                try {
                    // 将Base64编码的字符串转换为字节数组
                    byte[] imageBytes = Base64.getDecoder().decode(image);

                    // 从字节数组创建BufferedImage对象
                    BufferedImage tempimage = ImageIO.read(new ByteArrayInputStream(imageBytes));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                stamp.setStampImage(image);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else{
            stamp.setStampImage(stamp.getHandwritten());
        }
        // Set default to false by default
        stamp.setDefault(false);

        // Save the stamp to the database
        Stamp savedStamp = stampService.createStamp(stamp);

        // Return the created stamp
        return ResponseEntity.ok(savedStamp);
    }

    @GetMapping("/query/{userId}")
    public List<Stamp> queryStampsByUserId(@PathVariable String userId) {
        return stampService.queryStampsByUserId(userId);
    }

    @PostMapping("/setDefault")
    public ResponseEntity<String> setDefaultStamp(@RequestBody SetDefaultStampRequest request) {
        stampService.setDefaultStamp(request.getUserId(), request.getStampId());
        return ResponseEntity.ok("Default stamp changed");
    }

    @Autowired
    private PdfService pdfService;


    @PostMapping("/applynew")
    public ResponseEntity<InputStreamResource> applyStamp2(
            @RequestPart("document") MultipartFile document,
            @RequestPart("userId") String userId,
            @RequestPart(value = "stampId", required = false) String stampId,
            @RequestPart(value = "seamType", required = false) String seamType,
            @RequestPart(value = "crossPages", required = false) String crossPagesStr,
            @RequestPart(value = "pages", required = false) String pagesStr,
            @RequestPart(value = "x", required = false) String xStr,
            @RequestPart(value = "y", required = false) String yStr) throws Exception {if (document == null || document.isEmpty()) {
        return ResponseEntity.badRequest().body(null);
    }
        // 解析 crossPages 和 pages 为 List<Integer>
        List<Integer> crossPages = parseIntegerList(crossPagesStr);
        List<Integer> pages = parseIntegerList(pagesStr);

        // 解析 x 和 y 为 float
        float x = parseFloat(xStr, 0.0f);
        float y = parseFloat(yStr, 0.0f);

        // 判断是否为骑缝章
        if (seamType == null) {
            byte[] signedPdf = pdfService.stampAndSavePdf(document, userId, stampId, pages, x, y);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                    .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
        } else {
            boolean isSeam = "cross-page".equals(seamType);

            byte[] signedPdf = pdfService.qfz(document, stampId, crossPages);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                    .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
        }
    }

    private List<Integer> parseIntegerList(String listStr) {
        if (listStr == null || listStr.isEmpty()) {
            return null;
        }
        String[] parts = listStr.split(",");
        return java.util.Arrays.stream(parts)
                .map(Integer::parseInt)
                .toList();
    }

    private float parseFloat(String value, float defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }

    @PostMapping("/apply")
    public ResponseEntity<InputStreamResource> applyStamp(@RequestPart("document") MultipartFile document,
                                                          @RequestParam("requestBody") String requestBody) throws Exception {
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
//                .body(new InputStreamResource(document));
        if (document == null || document.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        // 解析requestBody JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        StampRequest stampRequest;
        try {
            stampRequest = objectMapper.readValue(requestBody, StampRequest.class);

        } catch (IOException e) {
            // JSON解析失败，返回400 Bad Request
            return ResponseEntity.badRequest().body(null);
        }

        // 获取签章位置信息
        List<Integer> pages = stampRequest.getPosition().getPages();
        float x = stampRequest.getPosition().getX();
        float y = stampRequest.getPosition().getY();

        // 判断是否为骑缝章
        if (stampRequest.getSeamConfig() == null) {

            byte[] signedPdf = pdfService.stampAndSavePdf(document, stampRequest.getUserId(), stampRequest.getStampId(), pages, x, y);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                    .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
        } else {
            boolean isSeam = "cross-page".equals(stampRequest.getSeamConfig().getSeamType());
            List<Integer> crossPages = stampRequest.getSeamConfig().getCrossPages();

            byte[] signedPdf = pdfService.qfz(document, stampRequest.getStampId(), crossPages);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                    .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
        }
//        return null;
    }
}