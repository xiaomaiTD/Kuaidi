package com.ins.driver.common;

import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.entity.User;
import com.ins.middle.utils.EventBusHelper;
import com.ins.middle.view.ProgView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */

public class ProgNetHelper {
    public static void netReqFirstMoney(final ProgView progView, final int orderId) {
        RequestParams params = new RequestParams(AppData.Url.orderStatus);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", "0");
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(progView.getContext(), text, Toast.LENGTH_SHORT).show();
                progView.setWateFirstMoney();

                EventBusHelper.sendEventOrder("3", orderId);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(progView.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void netReqGetPassenger(final ProgView progView, final int orderId) {
        RequestParams params = new RequestParams(AppData.Url.orderStatus);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", "1");
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(progView.getContext(), text, Toast.LENGTH_SHORT).show();
                progView.setStart();
                EventOrder eventOrder = new EventOrder();
                eventOrder.setAboutOrder("4");
                eventOrder.setOrderId(orderId);
                EventBus.getDefault().post(eventOrder);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(progView.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
