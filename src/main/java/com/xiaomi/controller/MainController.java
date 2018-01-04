package com.xiaomi.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xiaomi.bean.*;
import com.xiaomi.dao.CartDao;
import com.xiaomi.dao.GoodsDao;
import com.xiaomi.dao.StoreDao;
import com.xiaomi.dao.UserBeanDao;
import com.xiaomi.utils.CheckStringEmptyUtils;


import com.xiaomi.utils.ImageUtil;
import com.xiaomi.utils.JSONUtils;
import com.xiaomi.utils.UUIDUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;




import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;



@Controller
@RequestMapping("/user")
public class MainController {
    @RequestMapping("/hello")
    public String hello() {
        System.out.println("123");
        return "index";
    }

    @RequestMapping("/hello1")
    public String hello1(String Userid) {
        System.out.println("456userid:" + Userid);
        return "index";
    }

    /**
     * 购物车
     *
     * @param response
     * @param userid
     */
    @RequestMapping("/getShop")
    public void getShop(HttpServletResponse response, String userid) {
        response.setHeader("Content-type", "textml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonArray jsonArray = new JsonArray();

        String msg = "失败";
        int errorcode = -100;
        try {
            if (CheckStringEmptyUtils.IsEmpty(userid)) {
                msg = "userid是空的";
                System.out.println("userid是空的");
            } else {
                System.out.println("userid:" + userid);
                //先去查询所有的购物车
                ArrayList<CartBean> cartByUserId = CartDao.getCartByUserId(1);
                ArrayList<Goods> mGoodsList = new ArrayList<>();
                ArrayList<Store> mStoreList = new ArrayList<>();//店铺的列表
                ArrayList<Integer> mStoreIdList = new ArrayList<>();
                if (cartByUserId != null) {
                    //然后根据购物车的店铺id去查询店铺
                    for (CartBean cartBean : cartByUserId) {
                        if (!mStoreIdList.contains(cartBean.getStoreId())) {
                            Store storeListByStoreId = StoreDao.getStoreListByStoreId(cartBean.getStoreId());
                            if (storeListByStoreId != null) {
                                mStoreList.add(storeListByStoreId);
                                mStoreIdList.add(cartBean.getStoreId());
                            }
                        }
                        mGoodsList.add(GoodsDao.getAllGoodsByGoodId(cartBean.getGoodId()));
                    }
                }

                ArrayList<AppStore> mAppStoreList = new ArrayList<>();
                //遍历所有的商铺
                for (Store store : mStoreList) {
                    AppStore mAppStore = new AppStore();
                    mAppStore.setStoreId(store.getStoreId());
                    mAppStore.setStoreName(store.getStoreName());
                    ArrayList<Goods> mAppGoodList = new ArrayList<>();
                    //然后去查找商铺里的商品
                    for (int i = 0; i < mGoodsList.size(); ) {
                        if (mGoodsList.get(i).getStoreId() == store.getStoreId()) {
                            mGoodsList.get(i).setGoodNumber(CartDao.getGoodNumberByGoodId(mGoodsList.get(i).getGoodId()));
                            mAppGoodList.add(mGoodsList.get(i));
                            mGoodsList.remove(mGoodsList.get(i));
                        } else {
                            i++;
                        }
                    }
                    mAppStore.setmGoodList(mAppGoodList);
                    mAppStoreList.add(mAppStore);
                }
                jsonArray = JSONUtils.getJSONArrayByList(mAppStoreList);
                errorcode = 200;
                msg = "成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "失败";
            errorcode = -100;
        } finally {
            finalData(out, msg, errorcode, jsonArray);
        }
    }


    /**
     * 分页数据
     *
     * @param response
     * @param page
     * @param size
     * @ResponseBody 该注解用于将Controller的方法返回的对象，根据HTTP Request Header的Accept的内容,通过适当的HttpMessageConverter转换为指定格式后，写入到Response对象的body数据区。
     * 使用时机：
     * 返回的数据不是html标签的页面，而是其他某种格式的数据时（如json、xml等）使用.
     * 配置返回JSON和XML数据
     */
    @ResponseBody
    @RequestMapping("/getDataList")
    public String getDataList(HttpServletResponse response, int page, int size) {
        response.setHeader("Content-type", "textml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        String msg = "失败";
        int errorcode = -100;
        PrintWriter out = null;
        JsonArray jsonArray = new JsonArray();
        try {
            out = response.getWriter();
            ArrayList<UserBean> userList = UserBeanDao.getUserList(page, size);
            jsonArray = JSONUtils.getJSONArrayByList(userList);
            errorcode = 200;
            msg = "成功";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "失败";
            errorcode = -100;
        } finally {
            return finalData(out, msg, errorcode, jsonArray);
        }
    }

    @ResponseBody
    @RequestMapping("/upload2")
    public String upload2(HttpSession session, @RequestParam(required = false, value = "file") MultipartFile file) throws IllegalStateException, IOException {
        System.out.println("11111111111111111");
        String name = "";
        if (file.isEmpty()) {
            System.out.println("2222222222");
            return name;
        } else {
            System.out.println("33333333333333");
        }
        try {
            String suffixName = file.getOriginalFilename().split("\\.")[1];
            String thefileName = "/headimg/" + UUIDUtil.getUUID();
            String fileName = thefileName + "." + suffixName;
//            String realPath = session.getServletContext().getRealPath("/") + "static/";
//            File file1 = new File(realPath + fileName);
            System.out.println("absoluteuploadpath:"+ ImageUtil.getAbsoluteUploadPath());
            System.out.println("absoluteuploadpath1:"+ImageUtil.absoluteUploadPath);
            File file1 = new File(ImageUtil.getAbsoluteUploadPath() + fileName);
            System.out.println("11asd:" + file1.getPath());
            FileUtils.writeByteArrayToFile(file1, file.getBytes());

            name = ImageUtil.relativeUploadPath + fileName;
            System.out.println("11name:" + name);
        } catch (IOException var7) {
            var7.printStackTrace();
            System.out.println("IOException:" + var7.toString());
        }
        return name;
    }

//    @ResponseBody
//    @RequestMapping("/upload2")
//    public String fileUpload(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
//        System.out.println("文件上传接口");
//        //从request中获取流信息
//        InputStream fileSource = request.getInputStream();
//        String tempFileName = "D:/tempFile";
//        //指向临时文件
//        File tempFile = new File(tempFileName);
//        //输出流指向临时文件
//        FileOutputStream outputStream = new FileOutputStream(tempFileName);
//
//        byte b[] = new byte[1024];
//        int n;
//        while ((n = fileSource.read(b)) != -1) {
//            outputStream.write(b, 0, n);
//        }
//        //关闭输入输出流
//        outputStream.close();
//        fileSource.close();
//        System.out.println("保存临时文件成功");
//
//        //获取上传文件的名称
//        RandomAccessFile randomFile = new RandomAccessFile(tempFileName, "r");
//        randomFile.readLine();
//        String str = randomFile.readLine();
//        int beginIndex = str.lastIndexOf("\\") + 1;
//        int endIndex = str.lastIndexOf("\"");
//        String filename = str.substring(beginIndex, endIndex);
//        System.out.println("获取文件名称：" + filename);
//
//        //获取上传文件的内容的起止位置
//        randomFile.seek(0);
//        long startPosition = 0;
//        int i = 1;
////        文件内容的开始位置
//        while ((n = randomFile.read()) != -1 && i <= 4) {
//            if (n == '\n') {
//                startPosition = randomFile.getFilePointer();
//                i++;
//            }
//        }
//        startPosition = startPosition - 1;
//        System.out.println("获取文件内容起始");
//        //文件内容的结束位置
//        randomFile.seek(randomFile.length());
//        long endPosition = randomFile.getFilePointer();
//        int j = 1;
//        while (endPosition >= 0 && j <= 2) {
//            endPosition--;
//            randomFile.seek(endPosition);
//            if (randomFile.readByte() == '\n') {
//                j++;
//            }
//        }
//        endPosition = endPosition - 1;
//        System.out.println("获取文件内容结束");
//
//        //文件的保存路径
//        String realPath = session.getServletContext().getRealPath("/") + "static/";
//        File file1 = new File(realPath);
//        if (!file1.exists()) {
//            file1.mkdir();
//        }
//
//        File saveFile = new File(realPath, filename);
//        RandomAccessFile randomAccessFile = new RandomAccessFile(saveFile, "rw");
//        //从临时文件当中读取文件内容（根据起止位置获取）
//        randomFile.seek(startPosition);
//        while (startPosition < endPosition) {
//            randomAccessFile.write(randomFile.readByte());
//            startPosition = randomFile.getFilePointer();
//        }
//
//        //关闭流  删除临时文件；
//        randomAccessFile.close();
//        randomFile.close();
////        file.delete();
//        System.out.println("上传成功了" + realPath);
//
//        return realPath;
//    }
//
//    public String uploadPictures(MultipartFile file0) {
//        String name = "";
//        try {
//            String suffixName = file0.getOriginalFilename().split("\\.")[1];
//            String thefileName = "/headimg/" + UUIDUtil.getUUID();
//            String fileName = thefileName + "." + suffixName;
//            File file = new File(ImageUtil.absoluteUploadPath + fileName);
//            FileUtils.writeByteArrayToFile(file, file0.getBytes());
//            name = ImageUtil.relativeUploadPath + fileName;
//        } catch (IOException var7) {
//            var7.printStackTrace();
//        }
//        return name;
//    }

    private String finalData(PrintWriter out, String msg, int errorcode, Object data) {
        AppBean mBean = new AppBean(msg, errorcode, data);
        Gson gson = new Gson();
        out.print(gson.toJson(mBean));
        out.flush();
        out.close();
        return gson.toJson(mBean);
    }

    /**
     * 这里写一点Gson的用法
     *
     * @param args
     */
    public static void main(String[] args) {
        /**
         * 把Bean转换成json
         */
        /**
         * 这里如果想把某个属性，在转换成jSON的时候换个名字，去对应的字段上边加上SerializedName注解，详情去AppBean里边看
         * 如果有没些属性，在转换成JSON的时候，不想暴露出来，就去使用transient关键字
         */
        AppBean mBean = new AppBean("失败", -100, "");
        Gson gson = new Gson();
        String BeanToJson = gson.toJson(mBean);
        System.out.println("BeanToGson:" + BeanToJson);

        /**
         * 再把JSON转换成Bean
         */
        Gson gson1 = new Gson();
        AppBean BeanFromJson = gson1.fromJson(BeanToJson, AppBean.class);
        System.out.println("BeanFromJson:" + BeanFromJson.toString());
    }
}
