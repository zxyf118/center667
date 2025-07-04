package enums;

public enum NewShareDataChangeTypeEnum {
	ADD_NEW_SHARE("ADD_NEW_SHARE", "添加新股"), 
	EDIT_NEW_SHARE("EDIT_NEW_SHARE", "修改新股"),
	DEL_NEW_SHARE("DEL_NEW_SHARE", "删除新股");

	private final String code;
	private final String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	NewShareDataChangeTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (NewShareDataChangeTypeEnum e : NewShareDataChangeTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
