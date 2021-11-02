package me.blankm.danmuku.demo;

/**
 * Created by weipeng on 16/8/16.
 */
public class LiveUtils {

    private LiveUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    //获取等级图片
    public static int getLevelRes(String level) {
        int tt = toInt(level);

        if (tt < 11) {
            return MipmapRes.LevelImg[(tt == 0 ? 0 : tt - 1)];
        } else {
            return MipmapRes.LevelImg[9];
        }

    }


    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        return toInt(obj.toString(), 0);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            if (str == null) {
                return defValue;
            }

            if (str.contains(".")) {
                str = str.substring(0, str.indexOf("."));
            }
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

}
