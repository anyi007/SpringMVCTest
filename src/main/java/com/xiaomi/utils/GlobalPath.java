package com.xiaomi.utils;

import java.io.File;

public class GlobalPath {
//    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private static String sysRootPath = "";
    private static String classpath = "";

    static {
        classpath = GlobalPath.class.getResource("/").getPath();
        classpath = classpath.substring(1, classpath.length() - 1);
        if (!classpath.substring(0, 1).equals("/") && !classpath.substring(1, 2).equals(":")) {
            classpath = "/" + classpath;
        }

        sysRootPath = classpath;
        if (sysRootPath != null) {
            File f = new File(sysRootPath);
            sysRootPath = f.getParentFile().getParent();
            sysRootPath = sysRootPath.replaceAll("\\\\", "/");
        }

    }

    public GlobalPath() {
    }

    public static String getClassPath() {
        return classpath;
    }

    public static String getSysRootPath() {
        return sysRootPath;
    }

    public static void main(String[] args) {
        System.out.println(getClassPath());
        System.out.println(getSysRootPath());
    }
}
