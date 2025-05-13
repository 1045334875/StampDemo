package com.example.demo.service;

import com.example.demo.model.Document;
import com.example.demo.model.Stamp;
import com.example.demo.repository.PdfDocumentRepository;
import com.example.demo.repository.StampRepository;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
//import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

//import com.itextpdf.kernel.utils.ImageDataFactory;
//import com.itextpdf.kernel.utils.ImageData;
//import com.itextpdf.kernel.utils.ImageUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;

import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;


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
        try(FileOutputStream fos = new FileOutputStream("D:/code/demo/output.pdf")) {
            fos.write(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();

    }

    /**
     * 骑缝章签署
     * @throws IOException
     * @throws DocumentException
     * @throws GeneralSecurityException
    */
    public byte[] qfz(MultipartFile file, String stampId, List<Integer> pages) throws IOException, DocumentException, GeneralSecurityException {
        //选择需要印章的pdf
        PdfReader reader = new PdfReader(file.getInputStream());
        Stamp stamp = stampRepository.findById(stampId);
        //获得第一页
        com.itextpdf.text.Rectangle pageSize = reader.getPageSize(1);
        float height = pageSize.getHeight();
        float width = pageSize.getWidth();
        //pdf页数
        int nums = pages.size(); //reader.getNumberOfPages();
        //生成骑缝章切割图片
        Image[] images = stampService.subImages(stamp, nums);

        InputStream inputStream = file.getInputStream(); //new FileInputStream(KEYSTORE_PATH);

//        String path = PDF_PATH;
        // Creating the signature   签名算法
//        int i= 1;
//        for(Image image : images) {
//            PdfPage page = pdf.getPage(i);
//            PdfCanvas canvas = new PdfCanvas(page);
//            PdfImageXObject image = new PdfImageXObject(ImageDataFactory.createImageData(pdf, new ByteArrayInputStream(Base64.getDecoder().decode(stamp.getStampImage()))));
//            canvas.addImage(image, 100, 600);
//        }

//            //选择需要印章的pdf
//            reader = new PdfReader(path);
//            path = "./data/"+new Random().nextInt(1000)+".pdf";
//
//            FileOutputStream os = new FileOutputStream(new File(path));
//            //签署需要提供一个临时的目录
//            PdfStamper stamper =
//                    PdfStamper.createSignature(reader, os, '\0', new File(PDF_SIGNED), true);
//            // Creating the appearance
//            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
//
//            appearance.setReason("签署理由");
//            appearance.setLocation("签署位置");
//
//            appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
//            //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
//            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
//            //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名域名称不能一样
//            //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
//            //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
//            appearance.setVisibleSignature(new Rectangle(width-20, height/2 , width, height/2 + 60),
//                    i, System.currentTimeMillis()+"");
//            //fileName: 随机作用域
//
//            //签署图片地址
//            appearance.setSignatureGraphic(image);
//            // 调用itext签名方法完成pdf签章
////            MakeSignature.signDetached(appearance, digest, pks, chain,
////                    null, null, null, 0, subfilter);
//            i++;
////            Files.copy(Paths.get(path),new FileOutputStream (new File(PDF_SIGNED)));
        return null;
    }

}