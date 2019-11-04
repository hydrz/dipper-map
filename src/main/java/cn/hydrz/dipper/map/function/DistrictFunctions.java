package cn.hydrz.dipper.map.function;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hydrz.dipper.map.constant.AmapConst;
import cn.hydrz.dipper.map.model.District;
import cn.hydrz.dipper.map.model.DistrictLevel;
import cn.hydrz.dipper.map.util.AmapUtil;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author hydrz
 */
public class DistrictFunctions {
    public static final String RESOURCE_PATH = DistrictFunctions.class.getClassLoader().getResource("db/district.csv").getPath();
    private static final Logger log = Logger.getLogger(DistrictFunctions.class.getName());
    private static final WKTReader wkt = new WKTReader(new GeometryFactory());
    private static List<District> districts = new ArrayList();

    public static void init() {
        CsvReadConfig csvReadConfig = CsvReadConfig.defaultConfig();
        csvReadConfig.setContainsHeader(true);
        CsvData data = CsvUtil.getReader(csvReadConfig).read(new File(RESOURCE_PATH), CharsetUtil.CHARSET_UTF_8);

        for (int i = 1; i < data.getRowCount(); i++) {
            CsvRow row = data.getRow(i);
            District district = new District();

            Field[] declaredFields = district.getClass().getDeclaredFields();
            for (int j = 0; j < declaredFields.length; j++) {
                Field field = declaredFields[j];
                field.setAccessible(true);
                try {
                    String value = row.getByName(field.getName());

                    Object convert = null;

                    if (field.getName().equals("center") || field.getName().equals("polyline")) {
                        convert = wkt.read(value);
                    } else {
                        convert = Convert.convert(field.getType(), value);
                    }

                    field.set(district, convert);
                } catch (IllegalAccessException | ParseException e) {
                    e.printStackTrace();
                }
            }

            districts.add(district);
        }

    }

    /**
     * 获取一个点所在的行政区域
     *
     * @param point 点
     * @param level 行政区域级别
     * @return 行政区域
     */
    public static District getDistrictByPoint(Point point, DistrictLevel level) {
        List<District> collect = districts.stream()
                .filter(i ->
                        i.getLevel().equals(level.getCode()))
                .filter(i ->
                        point.relate(i.getPolyline()).isWithin())
                .sorted((i1, i2) -> {
                    double distance1 = i1.getCenter().distance(point);
                    double distance2 = i2.getCenter().distance(point);
                    return NumberUtil.compare(distance1, distance2);
                }).collect(Collectors.toList());

        return collect.isEmpty() ? null : collect.get(0);
    }

    /**
     * 所有行政区域列表
     *
     * @return 列表
     */
    public static Map<String, Object> getDistrictsList() {
        String url = String.format(AmapConst.DISTRICT_ALL, AmapUtil.getWebKey());
        return AmapUtil.get(url);
    }

}
