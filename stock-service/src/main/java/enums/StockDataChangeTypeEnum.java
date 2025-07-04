package enums;

public enum StockDataChangeTypeEnum {
	ADD_STOCK("ADD_STOCK", "新增股票"), 
	EDIT_STOCK("EDIT_STOCK", "编辑股票"),
	DELETE_STOCK("DELETE_STOCK", "删除股票");

	private final String code;
	private final String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	StockDataChangeTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (StockDataChangeTypeEnum e : StockDataChangeTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
