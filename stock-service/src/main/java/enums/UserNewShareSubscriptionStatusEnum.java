package enums;

public enum UserNewShareSubscriptionStatusEnum {
	BUY(0, "申购中"),
	BALLOT(1, "已中签"),
	PAID(2, "已认缴"),
	POSITION(3, "转持仓"),
	MISS(4, "未中签"),
	EXPIRED(5, "已过期"),
	CANCEL(6, "已取消");
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserNewShareSubscriptionStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (UserNewShareSubscriptionStatusEnum e : UserNewShareSubscriptionStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
