package cn.hydrz.dipper.map.util;

import cn.hutool.core.util.CharUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author hydrz
 */
public class AmapUtil {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    /**
     * 获取高德web服务key
     *
     * @return String
     */
    public static String getWebKey() {
        return System.getenv("AMAP_WEBKEY");
    }


    /**
     * 发起请求
     *
     * @param url 请求地址
     * @return API返回结果
     */
    public static Map<String, Object> get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();

            return gson.fromJson(response.body().string(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 高德格式转wkt格式
     *
     * @param polylineStr 高德返回的polyline
     * @return StringBuilder
     */
    public static StringBuilder transformerPolylineToWKT(String polylineStr) {
        StringBuilder stringBuilder = new StringBuilder(polylineStr);
        stringBuilder.insert(0, "MULTIPOLYGON(((");
        stringBuilder.append(")))");

        int pos = 0;
        int length = stringBuilder.length();

        while (pos < length) {
            char c = stringBuilder.charAt(pos);

            if (CharUtil.equals(c, ',', true)) {
                stringBuilder.replace(pos, pos + 1, " ");
            }

            if (CharUtil.equals(c, ';', true)) {
                stringBuilder.replace(pos, pos + 1, ",");
            }

            if (CharUtil.equals(c, '|', true)) {
                stringBuilder.replace(pos, pos + 1, ")),((");
                pos += 4;
                length += 4;
            }

            pos++;
        }
        return stringBuilder;
    }
}
