package com.digitald4.budget;

import com.digitald4.budget.proto.BudgetProtos.Account;
import com.digitald4.common.proto.DD4Protos.GeneralData;
import com.digitald4.common.util.ProtoUtil;

public class TestCase {

	static {
		ProtoUtil.init(GeneralData.getDescriptor(), Account.getDescriptor());
	}
}
