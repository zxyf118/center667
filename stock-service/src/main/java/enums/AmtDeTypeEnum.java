package enums;

public enum AmtDeTypeEnum {
	ManualRecharge("ManualRecharge", "人工充值"),
	ManualDeduction("ManualDeduction", "人工扣款"),
	BankCardWithdrawalApplication("BankCardWithdrawalApplication", "银行卡提现申请"),
	BankCardWithdrawalDeclined("BankCardWithdrawalDeclined", "银行卡提现拒绝"),
	BankCardWithdrawalCnnel("BankCardWithdrawalCnnel", "银行卡提现取消"),
	BuyStock("BuyStock", "买入股票"),
	SubscripNewShare("SubscripNewShare", "新股申购"),
	WinNewShare("WinNewShare", "新股中签"),
	PayNewShare("PayNewShare", "新股认缴"),
	SubscripNewShareFail("SubscripNewShareFail", "新股申购未中签"),
	ReturnFromNewShare("ReturnFromNewShare", "申购新股金额回退"),
	TransferPosition("TransferPosition", "委托订单转入持仓"),
	CanneledPending("CanneledPending", "委托订单被撤销"),
	RejectedPending("RejectedPending", "委托订单被拒绝"),
	ClosingPosition("ClosingPosition", "平仓"),
	UserInterest("UserInterest","融资利息结算"),
	FinancingRepayment("FinancingRepayment","融资还款");
	
	private String code;
	
	private String name;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	AmtDeTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (AmtDeTypeEnum e : AmtDeTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
