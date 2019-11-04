package cn.hydrz.dipper.map.constant;

/**
 * @author hydrz
 */
public interface AmapConst {
    String API_URL = "https://restapi.amap.com/v3";
    String DISTRICT_ALL = API_URL + "/config/district?s=rsv3&subdistrict=3&extensions=base&key=%s";
    String DISTRICT_POLYLINE = API_URL + "/config/district?s=rsv3&subdistrict=0&extensions=all&level=%s&keywords=%s&key=%s";
}
