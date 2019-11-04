package cn.hydrz.dipper.map.function;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hydrz.dipper.map.constant.AmapConst;
import cn.hydrz.dipper.map.model.District;
import cn.hydrz.dipper.map.model.DistrictLevel;
import cn.hydrz.dipper.map.util.AmapUtil;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hydrz
 * @email n.haoyuan@gmail.com
 * @date 2019/11/1 下午2:36
 */
public class DistrictFunctionsTest {
    private static final WKTReader wkt = new WKTReader(new GeometryFactory());
    private static Integer INIT_ID = 10000;

    @Test
    public void initData() throws IOException, IllegalAccessException {
        Map<String, Object> all = DistrictFunctions.getDistrictsList();
        List<Map<String, Object>> list = (List<Map<String, Object>>) all.get("districts");
        ArrayList<District> districts = new ArrayList<>();
        apply(list, 0, districts);

        CsvWriter writer = CsvUtil.getWriter(DistrictFunctions.RESOURCE_PATH, CharsetUtil.CHARSET_UTF_8);
        writer.flush();

        Field[] declaredFields = (new District()).getClass().getDeclaredFields();
        String[] title = new String[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            title[i] = declaredFields[i].getName();
        }
        writer.write(title);

        for (District district : districts) {
            setPolyline(district);
            String[] line = new String[districts.size()];
            Field[] declaredFieldsRow = district.getClass().getDeclaredFields();
            for (int i = 0; i < declaredFieldsRow.length; i++) {
                Field field = declaredFieldsRow[i];
                field.setAccessible(true);
                line[i] = ObjectUtil.hasEmpty(field.get(district)) ? "" : field.get(district).toString();
            }
            writer.write(line);
        }

        writer.close();
    }

    @Test
    public void getDistrictByPoint() throws ParseException {
        DistrictFunctions.init();
        Point point = (Point) wkt.read("POINT(119.319972 26.060499)");
        District districtByPoint = DistrictFunctions.getDistrictByPoint(point, DistrictLevel.DISTRICT);
        System.out.println(districtByPoint);
    }


    /**
     * 数据格式转换
     *
     * @param nodeTree  行政区域树
     * @param parentId  父级ID
     * @param districts 最后得到的列表
     */
    private static void apply(List<Map<String, Object>> nodeTree, Integer parentId, List<District> districts) {
        nodeTree.stream().forEach(i -> {
            try {
                District district = new District();
                String centerStr = MapUtil.get(i, "center", String.class);
                Point center = (Point) wkt.read(String.format("POINT(%s)", centerStr.replace(",", " ")));
                district.setCityCode(ObjectUtil.hasEmpty(i.get("citycode")) ? 0 : MapUtil.get(i, "citycode", Integer.class));
                district.setAdCode(MapUtil.get(i, "adcode", Integer.class));
                district.setName(MapUtil.get(i, "name", String.class));
                district.setLevel(DistrictLevel.getByName(MapUtil.get(i, "level", String.class)).getCode());
                Integer id = getId(district);
                district.setId(id);
                district.setParentId(parentId);
                district.setCenter(center);
                districts.add(district);

                List<Map<String, Object>> children = (List<Map<String, Object>>) i.get("districts");

                if (!children.isEmpty()) {
                    apply(children, id, districts);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 给行政区域设置ID
     *
     * @param district 行政区域
     * @return ID
     */
    private static Integer getId(District district) {
        Integer id = ++INIT_ID;
        return id;
    }

    /**
     * 设置行政区域的polyline
     *
     * @param district 行政区域
     */
    private static void setPolyline(District district) {
        String url = String.format(AmapConst.DISTRICT_POLYLINE, district.getLevel(), district.getName(), AmapUtil.getWebKey());
        Map<String, Object> result = AmapUtil.get(url);

        List<Map<String, Object>> districts = (List<Map<String, Object>>) result.get("districts");

        Map<String, Object> item = districts.get(0);

        if (!ObjectUtil.hasEmpty(item)) {
            try {
                String polylineStr = (String) item.get("polyline");
                polylineStr = ObjectUtil.hasEmpty(polylineStr) ? "" : AmapUtil.transformerPolylineToWKT(polylineStr).toString();
                MultiPolygon polyline = (MultiPolygon) wkt.read(polylineStr);
                district.setPolyline(polyline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}