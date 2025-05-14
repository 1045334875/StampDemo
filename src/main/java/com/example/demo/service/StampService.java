package com.example.demo.service;

import com.example.demo.model.*;

import com.example.demo.repository.StampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import java.awt.*;
import java.io.IOException;

@Service
public class StampService {
    @Autowired
    private StampRepository stampRepository;

    public Stamp createStamp(Stamp stamp) {
        // Generate a unique stampId
        String stampId = UUID.randomUUID().toString();
        stamp.setStampId(stampId);
        // Save the stamp to the database
        return stampRepository.save(stamp);
    }
    public List<Stamp> findAllStamps() {
        return stampRepository.findAll();
    }

    public List<Stamp> queryStampsByUserId(String userId) {
        return stampRepository.findAllByUserId(userId);
    }
    public boolean isUserIdExists(String userId) {
        return !stampRepository.findAllByUserId(userId).isEmpty();
    }

    public Stamp findStampById(String stampId) {
        return stampRepository.findById(stampId);
    }

    public void updateStamp(Stamp stamp) {
        stampRepository.update(stamp);
    }

    public void deleteStamp(String stampId) {
        stampRepository.delete(stampId);
    }

    public void setDefaultStamp(String userId, String stampId) {
        // 检查用户ID和印章ID是否为空
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
        if (stampId == null || stampId.isEmpty()) {
            throw new IllegalArgumentException("Stamp ID cannot be blank");
        }

        // 检查印章ID是否存在
        Optional<Stamp> stamp = Optional.ofNullable(stampRepository.findById(stampId));
        if (!stamp.isPresent()) {
            throw new IllegalArgumentException("Stamp ID does not exist: " + stampId);
        }

        // 检查印章是否属于指定用户
        if (!stamp.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Stamp ID does not belong to the user: " + userId);
        }
        stampRepository.setDefaultStamp(userId, stampId);
    }

    public static Image[] subImages(Stamp stamp, int n) throws IOException, BadElementException {
        Image[] nImage = new Image[n];
        byte[] imageBytes = Base64.getDecoder().decode(stamp.getStampImage());
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage img = ImageIO.read(bis);
        File outputfile = new File("D:\\code\\demo\\test_image_sum.jpg");
        ImageIO.write(img, "jpg", outputfile);
        int h = img.getHeight();
        int w = img.getWidth();

        int sw = w / n;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < n; i++) {
            BufferedImage subImg;
            //最后剩余部分
            if (i == n - 1) {
                subImg = img.getSubimage(i * sw, 0, w - i * sw, h);
                //前n-1块均匀切
            } else {
                subImg = img.getSubimage(i * sw, 0, sw, h);
            }

            ImageIO.write(subImg, "png", out);
            outputfile = new File("D:\\code\\demo\\test_image" + i + ".jpg");
            ImageIO.write(img, "jpg", outputfile);
            Image image = Image.getInstance(out.toByteArray());// Toolkit.getDefaultToolkit().createImage(subImg.getSource());
            nImage[i] = image;
            out.flush();
            out.reset();
        }
        return nImage;
    }

    public String createStampImage(String style, String color, String wrapText, String horizonText) throws Exception {
        SealConfiguration configuration = new SealConfiguration();
        configuration.setImageSize(300); // 图片大小

        switch (color.toUpperCase()) {
            case "RED":
                configuration.setBackgroudColor(Color.RED); // 背景颜色是红色
                break;
            case "BLUE":
                configuration.setBackgroudColor(Color.BLUE); // 背景颜色是蓝色
                break;
            case "BLACK":
                configuration.setBackgroudColor(Color.BLACK); // 背景颜色是黑色
                break;
            default:
                throw new IllegalArgumentException("Invalid color");
        }

        String image = null;
        // 根据样式生成签章
        switch (style.toUpperCase()) {
            case "OFFICIAL":
                // 公章样式配置
                SealFont centerFont = new SealFont();
                centerFont.setFontText("★");
                centerFont.setFontSize(100);
                configuration.setCenterFont(centerFont);
                if (wrapText != null) {
                    // 设置副文字
                    SealFont viceFont = new SealFont();
                    viceFont.setBold(true);
                    viceFont.setFontFamily("宋体");
                    viceFont.setMarginSize(0);
                    viceFont.setFontText(wrapText);
                    viceFont.setFontSize(22);
                    viceFont.setFontSpace(22.0);
                    configuration.setViceFont(viceFont);
                }
                if (horizonText != null) {
                    // 设置主文字
                    SealFont mainFont = new SealFont();
                    mainFont.setBold(true);
                    mainFont.setFontFamily("楷体");
                    mainFont.setMarginSize(10);
                    mainFont.setFontText(horizonText);
                    mainFont.setFontSize(25);
                    mainFont.setFontSpace(20.0);
                    configuration.setMainFont(mainFont);
                }
                configuration.setBorderCircle(new SealCircle(3, 140, 140));
                configuration.setBorderInnerCircle(new SealCircle(1, 135, 135));
                configuration.setInnerCircle(new SealCircle(2, 100, 100));
                image = SealUtil.buildAndStoreSeal(configuration);

                break;
            case "SPECIAL":
                // 专用章样式配置
                SealFont font = new SealFont();
                font.setFontSize(120).setBold(true).setFontText(wrapText);
                image = SealUtil.buildAndStorePersonSeal(300, 16, font, "印");

                break;
            case "OVAL":
                // 椭圆章样式配置
                if (wrapText != null) {
                    // 设置副文字
                    SealFont viceFont = new SealFont();
                    viceFont.setBold(true);
                    viceFont.setFontFamily("宋体");
                    viceFont.setMarginSize(10);
                    viceFont.setFontText(wrapText);
                    viceFont.setFontSize(22);
                    viceFont.setFontSpace(12.0);
                    configuration.setViceFont(viceFont);
                }
                if (horizonText != null) {
                    // 设置主文字
                    SealFont mainFont = new SealFont();
                    mainFont.setBold(true);
                    mainFont.setFontFamily("楷体");
                    mainFont.setMarginSize(10);
                    mainFont.setFontText(horizonText);
                    mainFont.setFontSize(25);
                    mainFont.setFontSpace(12.0);
                    configuration.setMainFont(mainFont);
                }
                configuration.setBorderCircle(new SealCircle(3, 140, 100));
                configuration.setBorderInnerCircle(new SealCircle(1, 135, 95));
                configuration.setInnerCircle(new SealCircle(2, 85, 45));
                // 返回签章信息
                image = SealUtil.buildAndStoreSeal(configuration);

                break;
            case "SQUARE":
                // 方形章样式配置
                break;
            default:
                throw new IllegalArgumentException("Invalid style");
        }





        // 设置中心文字
//        SealFont centerFont = new SealFont();
//        centerFont.setBold(true);
//        centerFont.setFontFamily("宋体");
//        centerFont.setFontText("发货专用");
//        centerFont.setFontSize(25);
//        configuration.setCenterFont(centerFont);


       return image;
    }
}
