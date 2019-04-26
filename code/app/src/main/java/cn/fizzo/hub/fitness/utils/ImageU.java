package cn.fizzo.hub.fitness.utils;

import android.widget.ImageView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import cn.fizzo.hub.fitness.R;

/**
 * Created by Raul.Fan on 2016/11/8.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class ImageU {

    /**
     * 缓冲用户头像图片
     *
     * @param avatar
     * @param imageView
     */
    public static void loadUserImage(final String avatar, final ImageView imageView) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCrop(true)
                .setLoadingDrawableId(R.drawable.bg_default_user)
                .setFailureDrawableId(R.drawable.bg_default_user)
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
//                .setSize(width, height)
                .build();
        x.image().bind(imageView, avatar, imageOptions);
    }


    /**
     * 缓冲门店图片
     *
     * @param avatar
     * @param imageView
     */
    public static void loadLogoImage(final String avatar, final ImageView imageView) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCrop(true)
                .setLoadingDrawableId(R.drawable.bg_default_company_logo)
                .setFailureDrawableId(R.drawable.bg_default_company_logo)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .build();
        x.image().bind(imageView, avatar, imageOptions);
    }

    /**
     * 缓冲视频封面图片
     *
     * @param avatar
     * @param imageView
     */
    public static void loadCourseVideoImage(final String avatar, final ImageView imageView) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setCrop(true)
                .setLoadingDrawableId(R.drawable.bg_default_course_video)
                .setFailureDrawableId(R.drawable.bg_default_course_video)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .build();
        x.image().bind(imageView, avatar, imageOptions);
    }
}
