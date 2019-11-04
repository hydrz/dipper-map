package cn.hydrz.dipper.map.model;

import lombok.Data;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;

/**
 * @author hydrz
 */
@Data
public class District implements Serializable {
    /**
     * 区域ID
     */
    private Integer id;

    /**
     * 父级ID
     */
    private Integer parentId;

    /**
     * 城市编码
     */
    private Integer cityCode;

    /**
     * 区域编码, 街道没有独有的adcode，均继承父类（区县）的adcode
     */
    private Integer adCode;

    /**
     * 行政区名称
     */
    private String name;

    /**
     * 行政区划级别
     * 0:country:国家
     * 1:province:省份（直辖市会在province和city显示）
     * 2:city:市（直辖市会在province和city显示）
     * 3:district:区县
     * 4:street:街道
     */
    private Integer level;

    /**
     * 区域中心点
     */
    private Point center;

    /**
     * 行政区边界坐标点 (当一个行政区范围，由完全分隔两块或者多块的地块组成，每块地的 polyline 坐标串以 | 分隔)
     */
    private MultiPolygon polyline;
}