package com.ins.middle.common;

import com.ins.middle.R;
import com.ins.middle.entity.BankCard;
import com.ins.middle.entity.BankCardConfig;
import com.sobey.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */

public class BankCardHelper {
    private static List<BankCardConfig> bankConfigs = new ArrayList<BankCardConfig>() {{
        add(new BankCardConfig("中国银行", R.drawable.shape_bank_red, AppData.Url.bank_icon + "zhongguo.png"));
        add(new BankCardConfig("工商银行", R.drawable.shape_bank_red, AppData.Url.bank_icon + "gongshang.png"));

        add(new BankCardConfig("建设银行", R.drawable.shape_bank_blue, AppData.Url.bank_icon + "jianse.png"));
        add(new BankCardConfig("交通银行", R.drawable.shape_bank_blue, AppData.Url.bank_icon + "jiaotong.png"));

        add(new BankCardConfig("农业银行", R.drawable.shape_bank_green, AppData.Url.bank_icon + "nongye.png"));
        add(new BankCardConfig("民生银行", R.drawable.shape_bank_green, AppData.Url.bank_icon + "minsheng.png"));
        add(new BankCardConfig("邮政储蓄银行", R.drawable.shape_bank_green, AppData.Url.bank_icon + "youzheng.png"));
    }};

    public static void setBankConfigs(List<BankCard> banks) {
        if (StrUtils.isEmpty(banks)) {
            return;
        }
        for (BankCard bank : banks) {
            setBankConfig(bank);
        }
    }

    public static void setBankConfig(BankCard bank) {
        for (BankCardConfig bankConfig : bankConfigs) {
            if (bank.getBankName().equals(bankConfig.getBankName())) {
                bank.setUrl(bankConfig.getUrl());
                bank.setBkSrc(bankConfig.getBkSrc());
                return;
            }
        }
        //未查询到该银行，设置默认值
        bank.setUrl("");
        bank.setBkSrc(R.drawable.shape_bank_blue);
    }
}
