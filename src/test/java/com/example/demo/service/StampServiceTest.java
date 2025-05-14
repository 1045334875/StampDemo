package com.example.demo.service;

import com.example.demo.model.Stamp;
//import com.example.demo.model.StampDTO;
import com.example.demo.repository.StampRepository;
import com.itextpdf.text.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

import static com.example.demo.service.StampService.subImages;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StampServiceTest {

    @InjectMocks
    private StampService stampService;

    @Mock
    private StampRepository stampRepository;

    private Stamp stamp;

    @BeforeEach
    public void setUp() throws Exception {
        stamp = new Stamp();
        stamp.setStampId("2");
        stamp.setUserId("user123");
        stamp.setStyle("OFFICIAL");
        stamp.setColor("RED");
        stamp.setWrapText("Wrap Text");
        stamp.setHorizonText("Horizon Text");
        stamp.setHandwritten(null);
        String base64Image = stampService.createStampImage(stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText());
        stamp.setStampImage(base64Image);
        stamp.setDefault(true);
    }

    @Test
    public void testtamp() throws Exception {
        stamp = new Stamp();
        stamp.setStampId("2");
        stamp.setUserId("user123");
        stamp.setStyle("OFFICIAL");
        stamp.setColor("RED");
        stamp.setWrapText("Wrap Text");
        stamp.setHorizonText("Horizon Text");
        stamp.setHandwritten(null);
        String base64Image = stampService.createStampImage(stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText());
        stamp.setStampImage(base64Image);
        stamp.setDefault(true);
        Image[] subImage = subImages(stamp, 2);
//        for (Image image : subImage) {
//
//            File outputfile = new File("path/to/save/image.jpg");
////            ImageIO.write(image, "jpg", outputfile);
//        }
    }


    @Test
    public void testCreateStamp_ShouldReturnSavedStamp() {
        when(stampRepository.save(any(Stamp.class))).thenReturn(stamp);

        Stamp savedStamp = stampService.createStamp(stamp);

        assertNotNull(savedStamp);
        assertEquals(stamp.getStampId(), savedStamp.getStampId());
        verify(stampRepository, times(1)).save(any(Stamp.class));
    }

    @Test
    public void testQueryStampsByUserId_ShouldReturnListOfStampDTO() {
        boolean isCross = true;
        int pages = 9;
        int nums;
        if(isCross) {
            nums = (pages+1)/2;
        }
        else {
            nums = pages;
        }
        nums = nums+1;
//        List<Stamp> stamps = Collections.singletonList(stamp);
//        when(stampRepository.findAllByUserId(anyString())).thenReturn(stamps);
    }

    @Test
    public void testCreateStampImage_ShouldReturnBase64EncodedImage() throws Exception {
        String style = "OFFICIAL";
        String color = "RED";
        String wrapText = "正版认证";
        String horizonText = "某某某公司事业专用章";
        String base64Image = stampService.createStampImage(style, color, wrapText, horizonText);
        assertNotNull(base64Image);
        saveBase64Image(base64Image, "D:\\code\\demo\\official.png");

        style = "SPECIAL";
        color = "BLUE";
        base64Image = stampService.createStampImage(style, color, wrapText, horizonText);
        assertNotNull(base64Image);
        saveBase64Image(base64Image, "D:\\code\\demo\\special.png");

//        verify(SealUtil.class).buildAndStoreSeal(any(SealConfiguration.class));
    }
    public static void saveBase64Image(String base64Image, String filePath)  {
        try {
            // 将Base64编码的字符串转换为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // 创建文件输出流
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                // 将字节数组写入文件
                fos.write(imageBytes);
            }

            System.out.println("Image saved successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showBase64Image(String base64Image) {
        try {
            // 将Base64编码的字符串转换为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // 从字节数组创建BufferedImage对象
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            // 创建一个JFrame窗口
            JFrame frame = new JFrame("Display Base64 Image");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(image.getWidth() + 10, image.getHeight() + 40); // 设置窗口大小

            // 创建一个JLabel并设置图像
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.add(label);

            // 显示窗口
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}