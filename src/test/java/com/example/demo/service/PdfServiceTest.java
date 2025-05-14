package com.example.demo.service;

import com.example.demo.model.Stamp;
import com.example.demo.model.Stamp;
import com.example.demo.model.StampRequest;
import com.example.demo.repository.StampRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.example.demo.service.StampService.subImages;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfServiceTest {
    @InjectMocks
    private StampService stampService;

    @InjectMocks
    private PdfService PdfService;

    @Mock
    private StampRepository stampRepository;

    private Stamp stamp;

    @Test
    public void testpdfread() throws IOException {
        String json = "{"
                + "\"userId\": \"user123\","
                + "\"stampId\": \"1\","
                + "\"seamConfig\": {"
                + "    \"seamType\": \"cross-page\","
                + "    \"crossPages\": [1, 2, 3]"
                + "},"
                + "\"position\": {"
                + "    \"pages\": [1, 2],"
                + "    \"x\": 100.0,"
                + "    \"y\": 200.0"
                + "}"
                + "}";

        ObjectMapper objectMapper = new ObjectMapper();
        StampRequest stampRequest;
        try {
            stampRequest = objectMapper.readValue(json, StampRequest.class);

        } catch (IOException e) {
            // JSON解析失败，返回400 Bad Request

        }

    }

    @Test
    public void testStampAndSavePdf() throws Exception {
        List<Integer> pages = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        int cross=1;
        int i=0;
        for (Integer page : pages) {
            int pageNumber = page;
            if(i%2 ==1){
                page++;
            }
            i++;

        }
    }
    @Test
    public void testtemp() throws Exception {
        // 模拟 Stamp 对象
        stamp = new Stamp();

//        stamp = stampRepository.findById("1");
        stamp.setStampId("1");
        stamp.setUserId("user123");
        stamp.setStyle("OFFICIAL");
        stamp.setColor("RED");
        stamp.setWrapText("正版认证");
        stamp.setHorizonText("某某某公司事业专用章");
        stamp.setHandwritten(null);
        String base64Image = stampService.createStampImage(stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText());
        stamp.setStampImage(base64Image);
        stamp.setDefault(true);

        byte[] imageBytes = Base64.getDecoder().decode(stamp.getStampImage());
        Image image = Image.getInstance(imageBytes);
        // 定义输入和输出路径
        String inputPdfPath = "D:/code/demo/1.pdf";
        String outputPdfPath = "D:/code/demo/output.pdf";
        File inputFile = new File("D:/code/demo/1.pdf");
        FileInputStream fis = new FileInputStream(inputFile);
        byte[] fileContent = new byte[(int) inputFile.length()];
        fis.read(fileContent);
        PdfReader reader = new PdfReader(fileContent);
//        PdfReader reader = new PdfReader(inputPdfPath);
        float x = 100f;
        float y = 100f;
        // 读取PDF文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PdfReader reader = new PdfReader(fileContent);
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        int a = reader.getNumberOfPages();
        String[] pages = new String[] {"1"};
        try {
            PdfStamper pdfStamper = new PdfStamper(reader, writer);
            for (String page : pages) {
                int pageNumber = Integer.parseInt(page);
//                PdfDictionary PdfDictionary = reader.getPageN(pageNumber);
                // 获取操作的页面
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNumber);
                image.setAbsolutePosition(x, y);
                pdfContentByte.addImage(image);
            }
            pdfStamper.close();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        fis.close();
        // 创建 MockMultipartFile
        MultipartFile file = new MockMultipartFile("1.pdf", fileContent);
        // 调用方法
        List<Integer> pages1 = Arrays.asList(1, 2, 3);
//        byte[] result = PdfService.stampAndSavePdf(file,"user123", "1", pages1, 100f, 100f);
        try(FileOutputStream fos = new FileOutputStream(outputPdfPath)) {
            fos.write(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 验证结果
        assertNotNull(outputStream.toByteArray());
//        assertTrue(result.length > 0);

//         验证 StampRepository 是否被调用
//        verify(stampRepository).findById("1");

        // 验证输出文件是否存在
        File outputFile = new File(outputPdfPath);
//        assertTrue(outputFile.exists());
    }

    @Test
    public void testStampNotFound() throws IOException {
        // 设置 Mock 的行为，返回 null
        when(stampRepository.findById("1")).thenReturn(null);

        // 定义输入和输出路径
        String inputPdfPath = "D:/code/demo/1.pdf";
        String outputPdfPath = "D:/code/demo/output.pdf";
        File inputFile = new File("D:/code/demo/1.pdf");
        FileInputStream fis = new FileInputStream(inputFile);
        byte[] fileContent = new byte[(int) inputFile.length()];
        fis.read(fileContent);
        fis.close();// 创建 MockMultipartFile
        MultipartFile file = new MockMultipartFile("1.pdf", fileContent);


        // 验证是否抛出 IllegalArgumentException
        List<Integer> pages1 = Arrays.asList(1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            byte[] a = PdfService.stampAndSavePdf(file, "user123", "1", pages1, 100f, 100f);

            try(FileOutputStream fos = new FileOutputStream("D:/code/demo/output.pdf")) {
                fos.write(a);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // 验证异常信息
        assertEquals("Stamp not found or StampImage is null", exception.getMessage());
    }

    public void test() throws Exception {
//        String base64Image = "你的Base64编码字符串"; // 替换为你的Base64编码字符串
        stamp = new Stamp();
        stamp.setStampId("1");
        stamp.setUserId("user123");
        String style = "OFFICIAL";
        String color = "RED";
        String wrapText = "正版认证";
        String horizonText = "某某某公司事业专用章";
        stamp.setHandwritten(null);
        String base64Image = stampService.createStampImage(stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText());
        stamp.setStampImage(base64Image);
        stamp.setDefault(true);
        Image[] images = subImages(stamp, 5);

        // 假设你有一个PDF文件路径
        String pdfPath = "D:/code/demo/1.pdf";
        PdfReader reader = new PdfReader(pdfPath);
        PdfStamper stamper = new PdfStamper(reader, new ByteArrayOutputStream());

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            PdfContentByte over = stamper.getUnderContent(i);
            for (Image img : images) {
                over.addImage(img);
            }
        }

        stamper.close();
        reader.close();
    }

}
