package com.xiaomi.utils;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class ImageUtil {
    public static final String IMAGE_TYPE_JPEG = "image/jpeg";
    public static String absoluteUploadPath;
    public static String relativeUploadPath = "/static/upload";

//    static {
//        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
//        ServletContext servletContext = applicationContext.getServletContext();
//        if (absoluteUploadPath == null) {
//            absoluteUploadPath = servletContext.getRealPath(relativeUploadPath);
//        }
//    }

    public static String getAbsoluteUploadPath(){
        if(absoluteUploadPath==null){
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            ServletContext servletContext = applicationContext.getServletContext();
            if (absoluteUploadPath == null) {
                absoluteUploadPath = servletContext.getRealPath(relativeUploadPath);
            }

        }
        return absoluteUploadPath;
    }

    public ImageUtil() {

    }

    public static BufferedImage codeToImage(String code) {
        int width = 65;
        int height = 20;
        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 25);
        g.setFont(new Font("Arial", 3, 18));
        g.drawLine(0, 0, 0, 0);

        for(int i = 0; i < code.length(); ++i) {
            g.setColor(getRandColor(0, 150));
            g.drawString(String.valueOf(code.charAt(i)), 15 * i + 6, 16);
        }

        Random random = new Random();

        for(int i = 0; i < random.nextInt(5) + 5; ++i) {
            g.setColor(new Color(random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1));
            g.drawLine(random.nextInt(100), random.nextInt(30), random.nextInt(100), random.nextInt(30));
        }

        g.dispose();
        return image;
    }

    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }

        if (bc > 255) {
            bc = 255;
        }

        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public static BufferedImage loadImageLocal(String imgPath) {
        try {
            return ImageIO.read(new File(imgPath));
        } catch (IOException var2) {
            System.out.println(var2.getMessage());
            return null;
        }
    }

    public static BufferedImage modifyImage(BufferedImage img, String myName, int x1, int y1, String toName, int x2, int y2, String fontName, int fontSize, String fontColor) {
        return modifyImage(img, myName, x1, y1, toName, x2, y2, fontName, fontSize, fontColor, 3);
    }

    public static BufferedImage modifyImage(BufferedImage img, String myName, int x1, int y1, String toName, int x2, int y2, String fontName, int fontSize, String fontColor, int fontInt) {
        try {
            Font font = new Font(fontName, fontInt, fontSize);
            if (font != null) {
                Graphics2D g = img.createGraphics();
                g.setBackground(Color.WHITE);
                g.setFont(font);
                g.setColor(Color.decode(fontColor));
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (myName != null) {
                    g.drawString(myName.toString(), x1, y1);
                }

                if (toName != null) {
                    g.drawString(toName.toString(), x2, y2);
                }

                g.dispose();
            }
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return img;
    }

    public static BufferedImage modifyImage(BufferedImage img, String name1, int x1, int y1, String name2, int x2, int y2, String name3, int x3, int y3, String fontName, int fontSize, String fontColor, int rotate) {
        try {
            Font font = new Font(fontName, 2, fontSize);
            if (font != null) {
                Graphics2D g = img.createGraphics();
                g.setBackground(Color.WHITE);
                g.setFont(font);
                g.setColor(Color.decode(fontColor));
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (rotate != 0) {
                    g.rotate((double)rotate * 3.141592653589793D / 180.0D);
                }

                if (name1 != null) {
                    g.drawString(name1.toString(), x1, y1);
                }

                if (name2 != null) {
                    g.drawString(name2.toString(), x2, y2);
                }

                if (name3 != null) {
                    g.drawString(name3.toString(), x3, y3);
                }

                g.dispose();
            }
        } catch (Exception var16) {
            var16.printStackTrace();
        }

        return img;
    }

    public static String createImage(BufferedImage img) {
        String filename = UUID.randomUUID().toString();
        String rootPath = GlobalPath.getSysRootPath();
        String imgPath = relativeUploadPath + "/" + DateUtil.getDateFormat(new Date(), "yyyy-MM-dd") + "/" + filename + ".jpg";
        File outputfile = new File(rootPath + imgPath);
        if (!outputfile.exists()) {
            outputfile.mkdirs();
        }

        try {
            ImageIO.write(img, "jpg", outputfile);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return imgPath;
    }

    public static BufferedImage scaleImage(BufferedImage img, int x, int y) {
        BufferedImage result = null;

        try {
            Image image = img.getScaledInstance(x, y, 1);
            result = new BufferedImage(x, y, 1);
            Graphics2D g = result.createGraphics();
            g.drawImage(image, 0, 0, (ImageObserver)null);
            g.dispose();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return result;
    }

    public static String writeImage(byte[] img) {
        ByteArrayInputStream bin = new ByteArrayInputStream(img);
        BufferedImage image = null;

        try {
            image = ImageIO.read(bin);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return createImage(image);
    }

    public static byte[] getImageFromNetByUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream inStream = conn.getInputStream();
            byte[] btImg = readInputStream(inStream);
            return btImg;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        boolean var3 = false;

        int len;
        while((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        inStream.close();
        return outStream.toByteArray();
    }

    public static void main(String[] args) {
        String url = "http://wx.qlogo.cn/mmopen/eoAcX5qb34IWBlQ9AicLJxk5cBNicRuTsNZCwmjXARtecd8GJH6O55PAXRrxO7hbe9giaRiaaMUSPlFoxLW7hkGIRICqLq8Jw7dia/0";
        byte[] btImg = getImageFromNetByUrl(url);
        if (btImg != null && btImg.length > 0) {
            System.out.println(writeImage(btImg));
        } else {
            System.out.println("没有从该连接获得内容");
        }

    }
}
