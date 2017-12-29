package com.xiaomi.dao;

import com.xiaomi.bean.UserBean;
import com.xiaomi.utils.DBUtils;

import java.sql.ResultSet;
import java.util.ArrayList;

public class UserBeanDao {

    /**
     * 分页查询用户
     *
     * @param page
     * @param size
     * @return
     */
    public static ArrayList<UserBean> getUserList(int page, int size) {
        ArrayList<UserBean> mList = new ArrayList<>();
        String sql = "select * from user limit " + (page - 1) * size + "," + size;
        ResultSet resultSet = DBUtils.getResultSet(sql);
        try {
            while (resultSet.next()) {
                UserBean mUserBean = new UserBean(resultSet.getInt("UserId"),
                        resultSet.getString("UserName")
                );
                mList.add(mUserBean);
            }
            return mList;
        } catch (Exception e) {
            e.printStackTrace();
            return mList;
        } finally {
            DBUtils.closeConnection();
        }
    }


    public static void main(String[] args) {
        ArrayList<UserBean> userList = UserBeanDao.getUserList(2, 10);
        if (userList != null && userList.size() != 0) {
            for (UserBean userBean : userList) {
                System.out.println(userBean.toString());
            }
        } else {
            System.out.println("数据为空");
        }
    }
}
