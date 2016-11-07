package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.utils.StrUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DomainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text_domain_test;
    private TextView text_domain_deve_xie;
    private TextView text_domain_deve_li;
    private EditText edit_domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);

        initBase();
        initView();
        initData();
        initCtrl();
    }

    private void initBase() {
    }

    private void initView() {
        text_domain_test = (TextView) findViewById(R.id.text_domain_test);
        text_domain_deve_xie = (TextView) findViewById(R.id.text_domain_deve_xie);
        text_domain_deve_li = (TextView) findViewById(R.id.text_domain_deve_li);
        edit_domain = (EditText) findViewById(R.id.edit_domain);

        findViewById(R.id.lay_domain_test).setOnClickListener(this);
        findViewById(R.id.lay_domain_deve_xie).setOnClickListener(this);
        findViewById(R.id.lay_domain_deve_li).setOnClickListener(this);
        findViewById(R.id.btn_go).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lay_domain_test) {
            edit_domain.setText(text_domain_test.getText());
        } else if (i == R.id.lay_domain_deve_xie) {
            edit_domain.setText(text_domain_deve_xie.getText());
        } else if (i == R.id.lay_domain_deve_li) {
            edit_domain.setText(text_domain_deve_li.getText());
        } else if (i == R.id.btn_go) {
            String domain = edit_domain.getText().toString();
            if (!StrUtils.isEmpty(domain)) {
                String uri;
                if (PackageUtil.isClient()) {
                    uri = "kuaidi.LoadUpActivity";
                } else {
                    uri = "driver.LoadUpActivity";
                }
                AppData.Url.domain = "http://" + domain + "/Carpooling/";
                modify();
                Intent intent = new Intent(uri);
                startActivity(intent);
                finish();
            }
        }
    }

    private void modify() {
        try {
            Object o = AppData.Url.class.newInstance();//获取对象
//        Field f=Constants.class.getField("param1");//根据key获取参数
            Field[] field = AppData.Url.class.getFields();//获取全部参数
            for (int i = 0; i < field.length; i++) {
                Field f = field[i];
                if ((Modifier.STATIC + Modifier.PUBLIC) == f.getModifiers()) {//获取字段的修饰符，public 1,static 8
                    if (f.getType().getName().indexOf("String") != -1) {
                        String strtemp = (String) f.get(o);
                        f.set(o, replace(AppData.Url.domain, strtemp));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replace(String domain, String str) {
        int index = str.indexOf("Carpooling/");
        if (str.endsWith("Carpooling/")) {
            return domain;
        } else {
            String substring = str.substring(index + "Carpooling/".length());
            return domain + substring;
        }
    }
}
