package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.StampRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.PdfService;
//import com.example.demo.service.UserService;
//import com.example.demo.model.StampDTO;
import com.example.demo.service.StampService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.PdfReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/stamp")
public class StampController {
    @Autowired
    private StampService stampService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createStamp(@RequestBody Stamp stamp) {
        // 检查用户ID是否存在，如果不存在则新建一个用户
        if (stamp.getUserId() == null || stamp.getUserId().isEmpty()) {
            User newUser = userService.createUser();
            stamp.setUserId(newUser.getUserId());
        }
        if(stamp.getHandwritten()==null){
            try {
                String image = stampService.createStampImage(stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText());
                try {
                    // 将Base64编码的字符串转换为字节数组
                    byte[] imageBytes = Base64.getDecoder().decode(image);
//                    // 从字节数组创建BufferedImage对象
//                    BufferedImage tempimage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decode image: " + e.getMessage());
                }
                stamp.setStampImage(image);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create stamp image: " + e.getMessage());
            }
        }
        else{
            stamp.setStampImage(stamp.getHandwritten());
        }
        // Set default to false by default
        stamp.setDefault(false);

        try {
            // Save the stamp to the database
            Stamp savedStamp = stampService.createStamp(stamp);
            // Return the created stamp as a Map
            return ResponseEntity.ok(savedStamp.toMap());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save stamp: " + e.getMessage());
        }
    }

    @GetMapping("/query/{userId}")
    public ResponseEntity<?> queryStampsByUserId(@PathVariable String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID cannot be blank");
        }
        // 检查 userId 是否存在于数据库中
        if (!stampService.isUserIdExists(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID does not exist: " + userId);
        }

        List<Stamp> stamps = stampService.queryStampsByUserId(userId);
//        按照上面的查Stamp中的UserId，如果查不到也走不到这个报错
//        if (stamps == null || stamps.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No stamps found for user: " + userId);
//        }

        return ResponseEntity.ok(stamps);
    }

    @PostMapping("/setDefault")
    public ResponseEntity<String> setDefaultStamp(@RequestBody SetDefaultStampRequest request) {
        try {
            stampService.setDefaultStamp(request.getUserId(), request.getStampId());
            return ResponseEntity.ok("Default stamp changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Autowired
    private PdfService pdfService;
    @Autowired
    private StampRepository stampRepository;

    @PostMapping("/applynew")
    public ResponseEntity<?> applyStamp2(
            @RequestPart("document") MultipartFile document,
            @RequestPart("userId") String userId,
            @RequestPart(value = "stampId", required = false) String stampId,
            @RequestPart(value = "seamType", required = false) String seamType,
            @RequestPart(value = "crossPages", required = false) String crossPagesStr,
            @RequestPart(value = "pages", required = false) String pagesStr,
            @RequestPart(value = "x", required = false) String xStr,
            @RequestPart(value = "y", required = false) String yStr) {

        // 检查文件是否为空
        if (document == null || document.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    400, "Bad Request", Collections.singletonList("Document is empty or null"));
            return ResponseEntity.badRequest().body(errorResponse);
        }
        // 检查印章ID是否为空
        if (stampId == null || stampId.isEmpty()) {
            Stamp defaultStamp = stampRepository.findDefaultStampByUserId(userId);
            if (defaultStamp == null) {
                ErrorResponse errorResponse = new ErrorResponse(
                        400, "Bad Request", Collections.singletonList("No default stamp found for user: " + userId));
                return ResponseEntity.badRequest().body(errorResponse);
            }
            stampId = defaultStamp.getStampId();
        }else {
            // 检查stampId是否存在
            Stamp stamp = stampRepository.findById(stampId);
            if (stamp == null) {
                ErrorResponse errorResponse = new ErrorResponse(
                        400, "Bad Request", Collections.singletonList("Stamp ID does not exist: " + stampId));
                return ResponseEntity.badRequest().body(errorResponse);
            }
            else if (userId != null && !userId.isEmpty()){
                // 检查印章是否属于指定用户
                if (!stamp.getUserId().equals(userId)) {
                    ErrorResponse errorResponse = new ErrorResponse(
                            400, "Bad Request", Collections.singletonList("Stamp ID does not belong to the user: " + userId));
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            }
        }
        // 解析 crossPages 和 pages 为 List<Integer>
        List<Integer> crossPages = parseIntegerList(crossPagesStr);
        List<Integer> pages = parseIntegerList(pagesStr);

        // 解析 x 和 y 为 float
        float x = parseFloat(xStr, 0.0f);
        float y = parseFloat(yStr, 0.0f);
        // 检查 pages 和 crossPages 是否在有效范围内
        try {
            PdfReader reader = new PdfReader(document.getInputStream());
            int totalPages = reader.getNumberOfPages();

            // 将不可变集合转换为可变集合
            List<Integer> mutablePages = null;
            // 检查 pages
            if (pages != null) {
                mutablePages = new ArrayList<>(pages);
                Collections.sort(mutablePages);
                if (mutablePages.get(0) < 1 || mutablePages.get(pages.size() - 1) > totalPages) {
                    ErrorResponse errorResponse = new ErrorResponse(
                            400, "Bad Request", Collections.singletonList("Page number is out of bounds. Valid range is 1 to " + totalPages));
                    return ResponseEntity.badRequest().body(errorResponse);
                }

            }

            // 检查 crossPages
            // 将不可变集合转换为可变集合
            List<Integer> mutableCrossPages = null;
            if (crossPages != null) {
                mutableCrossPages = new ArrayList<>(crossPages);
                Collections.sort(mutableCrossPages);
                if (mutableCrossPages.get(0) < 1 || mutableCrossPages.get(crossPages.size() - 1) > totalPages) {
                    ErrorResponse errorResponse = new ErrorResponse(
                            400, "Bad Request", Collections.singletonList("Page number is out of bounds. Valid range is 1 to " + totalPages));
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            }
            // 判断是否为骑缝章
            if (seamType == null) {
                if(mutablePages==null){
                    mutablePages = IntStream.rangeClosed(1, totalPages)
                                    .boxed()
                                    .collect(Collectors.toList());
                }
                byte[] signedPdf = pdfService.stampAndSavePdf(document, userId, stampId, mutablePages, x, y);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                        .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
            } else {
                boolean isSeam = "cross-page".equals(seamType);
                // 骑缝章需要 crossPages 参数
                if (mutableCrossPages == null || mutableCrossPages.isEmpty()) {
                    if (mutablePages != null && !mutablePages.isEmpty()) {
                        mutableCrossPages = mutablePages;
                    }
                    else{
                        mutableCrossPages = IntStream.rangeClosed(1, totalPages)
                                            .boxed()
                                            .collect(Collectors.toList());
                    }
                }
                byte[] signedPdf = pdfService.qfz(document, stampId, mutableCrossPages, isSeam, y);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                        .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
            }
        }catch (IllegalArgumentException e) {
            // 处理非法参数异常
            ErrorResponse errorResponse = new ErrorResponse(
                    400, "Bad Request", Collections.singletonList(e.getMessage()));
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // 处理其他异常
            ErrorResponse errorResponse = new ErrorResponse(
                    500, "Internal Server Error", Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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

            byte[] signedPdf = pdfService.qfz(document, stampRequest.getStampId(), crossPages, isSeam, y);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed_document.pdf")
                    .body(new InputStreamResource(new ByteArrayInputStream(signedPdf)));
        }
//        return null;
    }
}