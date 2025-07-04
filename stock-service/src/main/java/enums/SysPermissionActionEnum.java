package enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SysPermissionActionEnum {
    ACTION_1("1","创建管理员"),
    ACTION_2("2","修改管理员"),
    ACTION_3("3","修改管理员状态"),
    ACTION_4("4","删除管理员"),
    ACTION_5("5","添加角色下级"),
    ACTION_6("6","修改角色名"),
    ACTION_7("7","配置角色权限"),
    ACTION_8("8","删除角色"),
    ACTION_9("9","删除菜单"),
    ACTION_10("10","修改菜单"),
    ACTION_11("11","新增菜单"),
    ;

    @EnumValue
    private String value;

    @JsonValue
    private String name;

    SysPermissionActionEnum(String code, String name) {
        this.value = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        for (SysPermissionActionEnum e : SysPermissionActionEnum.values()) {
            if (code.equals(e.getValue())) {
                return e.getName();
            }
        }
        return null;
    }
}
