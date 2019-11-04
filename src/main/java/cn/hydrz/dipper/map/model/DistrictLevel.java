package cn.hydrz.dipper.map.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hydrz
 */
@Getter
@AllArgsConstructor
public enum DistrictLevel {
    COUNTRY(0, "country", "国家"),
    PROVINCE(1, "province", "省份"),
    CITY(2, "city", "市"),
    DISTRICT(3, "district", "区县"),
    STREET(4, "street", "街道"),
    ;

    private Integer code;
    private String name;
    private String zh;

    /**
     * 用数值ID获取
     *
     * @param code 数值
     * @return DistrictLevel
     */
    public static DistrictLevel getByCode(Integer code) {
        for (DistrictLevel level : DistrictLevel.values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }

        return null;
    }

    /**
     * 用名称获取
     *
     * @param name 行政区域名称
     * @return DistrictLevel
     */
    public static DistrictLevel getByName(String name) {
        for (DistrictLevel level : DistrictLevel.values()) {
            if (level.getName().equals(name)) {
                return level;
            }
        }

        return null;
    }
}
