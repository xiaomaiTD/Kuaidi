package com.ins.kuaidi.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.ins.kuaidi.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2016/10/27.
 */

public class GlideUtil {
    public static void loadCircleImg(Context context, ImageView imageView, int errorSrc, String url) {
        DrawableRequestBuilder<Integer> error = Glide.with(context).load(errorSrc).bitmapTransform(new CropCircleTransformation(context));
        Glide.with(context).load(url).thumbnail(error).bitmapTransform(new CropCircleTransformation(context)).crossFade().into(imageView);
    }

    public static void loadImg(Context context, ImageView imageView, int errorSrc, String url) {
        DrawableRequestBuilder<Integer> error = Glide.with(context).load(errorSrc);
        Glide.with(context).load(url).thumbnail(error).crossFade().into(imageView);
    }

    public static void loadCircleImg(Context context, ImageView imageView, int src) {
        Glide.with(context).load(src).bitmapTransform(new CropCircleTransformation(context)).crossFade().into(imageView);
    }

    public static void loadImg(Context context, ImageView imageView, int src) {
        Glide.with(context).load(src).crossFade().into(imageView);
    }

    public static void LoadCircleImgTest(Context context, View imageView) {
        loadCircleImg(context, (ImageView) imageView, R.drawable.test, "http://tupian.qqjay.com/tou3/2016/0725/037697b0e2cbb48ccb5a8c4d1ef0f65c.jpg");
    }
}
