package com.example.demo.service;

import com.example.demo.model.Stamp;
import com.example.demo.repository.PdfDocumentRepository;
import com.example.demo.repository.StampRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

//import com.itextpdf.kernel.utils.ImageDataFactory;
//import com.itextpdf.kernel.utils.ImageData;
//import com.itextpdf.kernel.utils.ImageUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
//import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;

import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class PdfService {

    @Autowired
    private PdfDocumentRepository pdfDocumentRepository;

    @Autowired
    private StampRepository stampRepository;

    @Autowired
    private StampService stampService;
    
    
    public byte[] stampAndSavePdf(MultipartFile file, String userId, String stampId, List<Integer> pages, float x, float y) throws Exception {
        // 从数据库获取StampImage
        Stamp stamp = stampRepository.findById(stampId);

        if (stamp == null || stamp.getStampImage() == null) {
            throw new IllegalArgumentException("Stamp not found or StampImage is null");
        }
        // 解码Base64字符串并创建Image
        byte[] imageBytes = Base64.getDecoder().decode(stamp.getStampImage());
        Image image = Image.getInstance(imageBytes);

        // 读取PDF文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(file.getInputStream());
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);

        try {
            PdfStamper pdfStamper = new PdfStamper(reader, writer);
            for (Integer page : pages) {
                int pageNumber = page;
                // 获取操作的页面
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNumber);
                // 根据域的大小缩放图片
//                image.scaleAbsolute(80f, 80f);
                // 添加图片
                image.setAbsolutePosition(x, y);
                pdfContentByte.addImage(image);
            }
            pdfStamper.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 5. 关闭资源并返回结果
        reader.close();
        return outputStream.toByteArray();

    }

    /**
     * 骑缝章签署
     * @throws IOException
     * @throws DocumentException
     * @throws GeneralSecurityException
    */
    public byte[] qfz(MultipartFile file, String stampId, List<Integer> pages, boolean isCross, float y) throws IOException, DocumentException, GeneralSecurityException {
        // 从数据库获取StampImage
        Stamp stamp = stampRepository.findById(stampId);

        if (stamp == null || stamp.getStampImage() == null) {
            throw new IllegalArgumentException("Stamp not found or StampImage is null");
        }
        // 解码Base64字符串并创建Image
        byte[] imageBytes = Base64.getDecoder().decode(stamp.getStampImage());
        Image image = Image.getInstance(imageBytes);

        // 读取PDF文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(file.getInputStream());
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        if (pages == null || pages.isEmpty()) {
            int totalPages = reader.getNumberOfPages();
            pages = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
        }
        //获得第一页
        com.itextpdf.text.Rectangle pageSize = reader.getPageSize(1);
        float height = pageSize.getHeight();
        float width = pageSize.getWidth();
        //pdf页数
        if(y == 0.0){
            y = height/2;
        }
        int nums;
        if(isCross) { // 双面打印
            nums = (pages.size()+1)/2;
        }
        else { // 单面打印
            nums = pages.size();
        }

        // 根据域的大小缩放图片
//                image.scaleAbsolute(80f, 80f);
        //生成骑缝章切割图片
        Image[] images = stampService.subImages(stamp, nums);

        int i= 0;
        try {
            PdfStamper pdfStamper = new PdfStamper(reader, writer);
            for (Integer page : pages) {

                int pageNumber = page;
                if(isCross && i%2 ==1){// 双面打印隔一页空一下
                    i++;
                    continue;
                }
                // 获取操作的页面
                PdfContentByte pdfContentByte = pdfStamper.getOverContent(pageNumber);
                Image crossImage;
                if (isCross){
                    crossImage = images[i/2];
                }
                else{
                    crossImage = images[i];
                }
                i++;
                float x = width - crossImage.getWidth();
                crossImage.setAbsolutePosition(x, y);
                pdfContentByte.addImage(crossImage);

            }
            pdfStamper.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 5. 关闭资源并返回结果
        reader.close();
        return outputStream.toByteArray();
    }

}