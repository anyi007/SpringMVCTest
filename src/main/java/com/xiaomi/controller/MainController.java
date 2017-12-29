package com.xiaomi.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xiaomi.bean.*;
import com.xiaomi.dao.CartDao;
import com.xiaomi.dao.GoodsDao;
import com.xiaomi.dao.StoreDao;
import com.xiaomi.dao.UserBeanDao;
import com.xiaomi.utils.CheckStringEmptyUtils;
import com.xiaomi.utils.JSONUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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

//    @RequestMapping("/upload2")
//    public void upload2(HttpServletRequest request) throws IllegalStateException, IOException {
//        System.out.println("11111111111111111");
//        //创建一个通用的多部分解析器
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//        //判断 request 是否有文件上传,即多部分请求
//        if (multipartResolver.isMultipart(request)) {
//            //转换成多部分request
//            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
//            //取得request中的所有文件名
//            Iterator<String> iter = multiRequest.getFileNames();
//            while (iter.hasNext()) {
//                //记录上传过程起始时的时间，用来计算上传时间
//                int pre = (int) System.currentTimeMillis();
//                //取得上传文件
//                MultipartFile file = multiRequest.getFile(iter.next());
//                if (file != null) {
//                    //取得当前上传文件的文件名称
//                    String myFileName = file.getOriginalFilename();
//                    //如果名称不为“”,说明该文件存在，否则说明该文件不存在
//                    if (myFileName.trim() != "") {
//                        System.out.println(myFileName);
//                        //重命名上传后的文件名
//                        String fileName = "demoUpload" + file.getOriginalFilename();
//                        //定义上传路径
//                        String path = "D:/" + fileName;
//                        File localFile = new File(path);
//                        file.transferTo(localFile);
//                    }
//                }
//                //记录上传该文件后的时间
//                int finaltime = (int) System.currentTimeMillis();
//                System.out.println(finaltime - pre);
//            }
//        }
//    }

    @RequestMapping("/upload2")
    public void fileUpload(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        System.out.println("文件上传接口");
        //从request中获取流信息
        InputStream inputStream = request.getInputStream();
        String tempFileName = "D:tempFile";
        //指向临时文件
        File file = new File(tempFileName);
        //输出流指向临时文件
        FileOutputStream outputStream = new FileOutputStream(tempFileName);

        byte b[] = new byte[1024];
        int n;
        while ((n = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, n);
        }
        //关闭输入输出流
        outputStream.close();
        inputStream.close();
        System.out.println("保存临时文件成功");

        //获取上传文件的名称
        RandomAccessFile r = new RandomAccessFile(tempFileName, "r");
        r.readLine();
        String str = r.readLine();
        int beginIndex = str.lastIndexOf("\\") + 1;
        int endIndex = str.lastIndexOf("\"");
        String filename = str.substring(beginIndex, endIndex);


        //获取上传文件的内容的起止位置
        r.seek(0);
        long startPosition = 0;
        int i = 1;
//        文件内容的开始位置
        while ((n = r.read()) != -1 && i <= 4) {
            if (n == '\n') {
                startPosition = r.getFilePointer();
                i++;
            }
        }
        startPosition = startPosition - 1;
        System.out.println("获取文件内容起始");
        //文件内容的结束位置
        r.seek(r.length());
        long endPosition = r.getFilePointer();
        int j = 1;
        while (endPosition >= 0 && j <= 2) {
            endPosition--;
            r.seek(endPosition);
            if (r.readByte() == '\n') {
                j++;
            }
        }
        endPosition = endPosition - 1;
        System.out.println("获取文件内容结束");

        //文件的保存路径
        String realPath = session.getServletContext().getRealPath("/") + "static";
        File file1 = new File(realPath);
        if (!file1.exists()) {
            file1.mkdir();
        }

        File saveFile = new File(realPath, filename);
        RandomAccessFile randomAccessFile = new RandomAccessFile(saveFile, "rw");
        //从临时文件当中读取文件内容（根据起止位置获取）
        r.seek(startPosition);
        while (startPosition < endPosition) {
            randomAccessFile.write(r.readByte());
            startPosition = r.getFilePointer();
        }

        //关闭流  删除临时文件；
        randomAccessFile.close();
        r.close();
        file.delete();
        System.out.println("上传成功了");

    }
//    public void fileUpload(@RequestParam MultipartFile[] photos, HttpSession session) throws IllegalStateException, IOException {
//        System.out.println("11111111111111111");
//        //服务端的imges目录需要手动创建好
//        String path = session.getServletContext().getRealPath("/static");
//        for (int i = 0; i < photos.length; i++) {
//            if (!photos[i].isEmpty()) {
//                String fileName = photos[i].getOriginalFilename();
//                if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
//                    File file = new File(path, fileName);
//                    //完成文件上传
//                    photos[i].transferTo(file);
//                } else {
//                    //失败返回
//                    System.out.println("失败失败");
//                }
//            }
//        }
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
