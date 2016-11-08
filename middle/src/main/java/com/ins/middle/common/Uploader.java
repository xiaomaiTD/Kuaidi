package com.ins.middle.common;

import android.util.Log;
import android.widget.Toast;

import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/8.
 */

public class Uploader {

    ////////////////////////////////////////////////
    //////////////////批量上传
    ////////////////////////////////////////////////

    private List<String> urls = new ArrayList<>();
    private int index = 0;

    public void startUpload(List<String> paths, UploadCallback callback) {
        index = 0;
        urls.clear();
        uploadFile(paths, callback);
    }

    private void uploadFile(final List<String> paths, final UploadCallback callback) {
        if (index > paths.size() - 1) {
            afterUpload(callback);
            return;
        }
        String path = paths.get(index);
        RequestParams params = new RequestParams(AppData.Url.upload);
        params.setMultipart(true);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("files", new File(path));
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, text);
                else {
                    CommonEntity commonEntity = (CommonEntity) pojo;
                    String url = commonEntity.getFilePath();
                    //上传完毕
                    urls.add(url);
                    index++;
                    uploadFile(paths, callback);
                }
            }

            @Override
            public void netSetError(int code, String text) {
                if (callback != null) callback.uploadfiled(code, text);
            }
        });
    }

    private void afterUpload(UploadCallback callback) {
        for (String path : urls) {
            Log.e("liao", path + "");
        }
        if (callback != null) callback.uploadSuccess(urls);
    }

    private UploadCallback callback;

    public interface UploadCallback {
        void uploadfiled(int code, String text);

        void uploadSuccess(List<String> urls);
    }
}
