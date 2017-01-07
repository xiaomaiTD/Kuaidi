package com.ins.middle.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.google.gson.reflect.TypeToken;
import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.BankCard;
import com.ins.middle.entity.Cash;
import com.ins.middle.entity.User;
import com.ins.middle.entity.Wallet;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class CashActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    //    private VirtualKeyboardView keybord;
    private TextView btn_go;

    private View lay_cash_bankcard;
    private TextView text_cash_bankcard;
    private TextView text_cash_bankcardnote;
    private EditText edit_cash_money;
    private TextView text_cash_cashnote;
    private TextView text_cash_moneynote;
    private TextView text_cash_all;

    private BankCard card;
    private Cash cash;
    private float money;

    private DialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLoading != null) dialogLoading.dismiss();
    }

    private void initBase() {
        if (getIntent().hasExtra("money")) {
            money = getIntent().getFloatExtra("money", 0);
        }
        dialogLoading = new DialogLoading(this, "正在处理");
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
//        keybord = (VirtualKeyboardView) findViewById(R.id.keybord);
        btn_go = (TextView) findViewById(R.id.btn_go);

        lay_cash_bankcard = findViewById(R.id.lay_cash_bankcard);
        text_cash_bankcard = (TextView) findViewById(R.id.text_cash_bankcard);
        text_cash_bankcardnote = (TextView) findViewById(R.id.text_cash_bankcardnote);
        edit_cash_money = (EditText) findViewById(R.id.edit_cash_money);
        text_cash_cashnote = (TextView) findViewById(R.id.text_cash_cashnote);
        text_cash_moneynote = (TextView) findViewById(R.id.text_cash_moneynote);
        text_cash_all = (TextView) findViewById(R.id.text_cash_all);

        lay_cash_bankcard.setOnClickListener(this);
        text_cash_all.setOnClickListener(this);
        btn_go.setOnClickListener(this);
    }

    private void initData() {
        netGetCashConfig();
        netGetBankCards();
    }

    private void initCtrl() {
        btn_go.setEnabled(false);
        edit_cash_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    btn_go.setEnabled(false);
                    return;
                }
                float editcash = Float.parseFloat(s.toString());
                String input = s.subSequence(start, start + count).toString();
                if (s.length() == 1 && "0".equals(input)) {
                    edit_cash_money.setText("");
                    return;
                }
                String msg = AppHelper.getCashMsg(money, PackageUtil.isClient() ? cash.getPassengerDeposit() : cash.getDriverDeposit(), cash.getQuota(), editcash);
                if (msg != null) {
                    Snackbar.make(showingroup, msg, Snackbar.LENGTH_INDEFINITE).show();
                }

                float cashAll = AppHelper.getCashAll(money, PackageUtil.isClient() ? cash.getPassengerDeposit() : cash.getDriverDeposit(), cash.getQuota());
                if (editcash > cashAll) {
                    editcash = cashAll;
                    String content = ((int) editcash) + "";
                    if (editcash == 0) content = "";
                    edit_cash_money.setText(content);
                    edit_cash_money.setSelection(content.length());
                }


//                if (editcash > cash.getQuota()) {
//                    editcash = cash.getQuota();
//                    edit_cash_money.setText(((int) editcash) + "");
//                    edit_cash_money.setSelection((((int) editcash) + "").length());
//                    Snackbar.make(showingroup, "单次最多提现" + ((int) editcash) + "元", Snackbar.LENGTH_INDEFINITE).show();
//                }
                boolean enable = AppHelper.enableCash(money, editcash, PackageUtil.isClient() ? cash.getPassengerDeposit() : cash.getDriverDeposit());
                btn_go.setEnabled(enable);
            }
        });
    }

    private void setCashData(Cash cash, BankCard card) {
        if (cash != null) {
            if (card != null) {
                String note = "提现到" + card.getBankName() + "，收付费" + cash.getCashRate() + "%";
                text_cash_bankcardnote.setText(note);
                text_cash_cashnote.setText(note);
            } else {
                text_cash_bankcardnote.setText("");
                text_cash_cashnote.setText("");
            }
            text_cash_moneynote.setText((PackageUtil.isClient() ? cash.getPassengerDeposit() : cash.getDriverDeposit()) + "元底金无法提现，可用于车费支付");
        }
        if (card != null) {
            if (!StrUtils.isEmpty(card.getBankName()) && !StrUtils.isEmpty(card.getBankNum())) {
                String name = card.getBankName() + "（" + card.getBankNum().substring(card.getBankNum().length() - 4) + "）";
                text_cash_bankcard.setText(name);
            }
        } else {
            text_cash_bankcard.setText("请选择银行卡");
        }
        if (cash != null) {
            if (money < (PackageUtil.isClient() ? cash.getPassengerDeposit() : cash.getDriverDeposit())) {
                Snackbar.make(showingroup, "您的余额小于底金，无法提现", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.lay_cash_bankcard) {
            intent.setClass(this, BankCardActivity.class);
            intent.putExtra("type", 1);
            startActivityForResult(intent, RESULT_BANKCARD);
        } else if (i == R.id.text_cash_all) {
            //又改成了，只需把余额全部显示出来
//            edit_cash_money.setText((int) money + "");
            if (cash != null) {
                edit_cash_money.setText("" + (int) AppHelper.getCashAll(money, PackageUtil.isClient() ? cash.getPassengerDeposit() : cash.getDriverDeposit(), cash.getQuota()));
            }
        } else if (i == R.id.btn_go) {
            String input = edit_cash_money.getText().toString();
            if (StrUtils.isEmpty(input)) return;
            if (card != null) {
                float editcash = Float.parseFloat(input);
                if (editcash % 100 == 0) {
                    User user = AppData.App.getUser();
                    if (user.getHasPayPassword() == 1) {
                        //已设置提现密码，直接进入提现
                        intent.setClass(this, BindUnBankCardActivity.class);
                        intent.putExtra("type", 1);
                        startActivityForResult(intent, RESULT_PAYPSW);
                    } else {
                        //未设置提现密码，先设置提现密码
                        intent.setClass(this, ModifyPswPayActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "请先设置提现密码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(showingroup, "提现金额只能是100的整数倍", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(showingroup, "没有选择银行卡，请重试", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private static final int RESULT_BANKCARD = 0xf101;
    private static final int RESULT_PAYPSW = 0xf102;

    //页面返回回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_BANKCARD:
                if (resultCode == RESULT_OK) {
                    card = (BankCard) data.getSerializableExtra("bankcard");
                    setCashData(cash, card);
                }
                break;
            case RESULT_PAYPSW:
                if (resultCode == RESULT_OK) {
                    float cashmoney = Float.parseFloat(edit_cash_money.getText().toString());
                    netGetCash(card.getId(), cashmoney);
                }
                break;
        }
    }

    public void netGetCashConfig() {
        RequestParams params = new RequestParams(AppData.Url.getCashConfig);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, Cash.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "获取提现配置失败，请联系工作人员");
                else {
                    cash = (Cash) pojo;
                    setCashData(cash, card);
                    LoadingViewUtil.showout(showingroup, showin);
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(CashActivity.this, text, Toast.LENGTH_SHORT).show();
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_fail, showin, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
            }

            @Override
            public void netStart(int status) {
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
            }
        });
    }

    private void netGetBankCards() {
        final RequestParams params = new RequestParams(AppData.Url.findBankCard);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("pageNO", "1");
        params.addBodyParameter("pageSize", "1");
        CommonNet.samplepost(params, new TypeToken<List<BankCard>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "错误：返回数据为空");
                else {
                    List<BankCard> cards = (ArrayList<BankCard>) pojo;
                    if (!StrUtils.isEmpty(cards)) {
                        card = cards.get(0);
                        setCashData(cash, card);
                    }
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(CashActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void netGetCash(int bankId, final float money) {
        final RequestParams params = new RequestParams(AppData.Url.getCash);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("bankId", bankId + "");
        params.addBodyParameter("money", (int) money + "");
        CommonNet.samplepost(params, String.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Toast.makeText(CashActivity.this, text, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra("money", CashActivity.this.money - money);
//                setResult(Activity.RESULT_OK, intent);
                finish();

                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_CASH_MONEY, CashActivity.this.money - money + ""));
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(CashActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                dialogLoading.show();
            }

            @Override
            public void netEnd(int status) {
                dialogLoading.dismiss();
            }
        });
    }
}
