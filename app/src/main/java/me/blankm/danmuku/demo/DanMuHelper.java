package me.blankm.danmuku.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;


import me.blankm.danmuku.demo.R;

import me.blankm.danmuku.demo.model.DanmakuEntity;
import me.blankm.danmuku.demo.model.RichTextParse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import me.blankm.danmuku.model.DanMuModel;
import me.blankm.danmuku.model.utils.DimensionUtil;
import me.blankm.danmuku.view.IDanMuParent;
import me.blankm.danmuku.view.OnDanMuTouchCallBackListener;

/**
 * 弹幕库使用帮助类
 * <p>
 * 建议凡是弹幕中涉及到的图片，大小控制在50kb以内，尺寸控制在100x100以内（单位像素）
 * <p>
 * Created by android_ls on 2016/12/18.
 */
public final class DanMuHelper {

    private ArrayList<WeakReference<IDanMuParent>> mDanMuViewParents;
    private WeakReference<Context> mContext;

    public DanMuHelper(Context context) {
        if (mContext == null) {
            this.mContext = new WeakReference<>(context.getApplicationContext());
        }

        this.mDanMuViewParents = new ArrayList<>();
    }

    public void release() {
        if (mDanMuViewParents != null) {
            for (WeakReference<IDanMuParent> danMuViewParentsRef : mDanMuViewParents) {
                if (danMuViewParentsRef != null) {
                    IDanMuParent danMuParent = danMuViewParentsRef.get();
                    if (danMuParent != null)
                        danMuParent.release();
                }
            }
            mDanMuViewParents.clear();
            mDanMuViewParents = null;
        }

        mContext = null;
    }

    public void add(final IDanMuParent danMuViewParent) {
        if (danMuViewParent != null) {
            danMuViewParent.clear();
        }

        if (mDanMuViewParents != null) {
            mDanMuViewParents.add(new WeakReference<>(danMuViewParent));
        }
    }

    public void addDanMu(DanmakuEntity danmakuEntity, boolean broadcast, OnDanMuTouchCallBackListener listener) {
        if (mDanMuViewParents != null) {
            WeakReference<IDanMuParent> danMuViewParent = mDanMuViewParents.get(0);
            DanMuModel danMuView = createDanMuView(danmakuEntity, listener);
            if (danMuViewParent != null && danMuView != null && danMuViewParent.get() != null) {
                danMuViewParent.get().add(danMuView);
            }
        }
    }

    private DanMuModel createDanMuView(final DanmakuEntity entity, OnDanMuTouchCallBackListener listener) {
        final DanMuModel danMuView = new DanMuModel();
        danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
        danMuView.setPriority(DanMuModel.NORMAL);
        danMuView.marginLeft = DimensionUtil.dpToPx(mContext.get(), 30);

        if (entity.getType() == DanmakuEntity.DANMAKU_TYPE_USERCHAT) {
            // 图像
            int avatarSize = DimensionUtil.dpToPx(mContext.get(), 30);
            danMuView.avatarWidth = avatarSize;
            danMuView.avatarHeight = avatarSize;

            String avatarImageUrl = entity.getAvatar();

            Glide.with(mContext.get())
                    .asBitmap()
                    .load(avatarImageUrl)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            danMuView.avatar = resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });


            // 等级
            int level = entity.getLevel();
            int levelResId = LiveUtils.getLevelRes(level + "");
            Drawable drawable = ContextCompat.getDrawable(mContext.get(), levelResId);
            danMuView.levelBitmap = drawable2Bitmap(drawable);
            danMuView.levelBitmapWidth = DimensionUtil.dpToPx(mContext.get(), 33);
            danMuView.levelBitmapHeight = DimensionUtil.dpToPx(mContext.get(), 16);
            danMuView.levelMarginLeft = DimensionUtil.dpToPx(mContext.get(), 5);
            //图片上添加数字
            if (level > 0 && level < 100) {
                danMuView.levelTextColor = ContextCompat.getColor(mContext.get(), R.color.white);
                danMuView.levelTextSize = DimensionUtil.spToPx(mContext.get(), 14);
            }

            // 显示的文本内容
            String name = entity.getName() + "：";
            String content = entity.getText();
            SpannableString spannableString = new SpannableString(name + content);
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext.get(), R.color.white)),
                    0,
                    name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            danMuView.textSize = DimensionUtil.spToPx(mContext.get(), 14);
            danMuView.textColor = ContextCompat.getColor(mContext.get(), R.color.main_color);
            danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext.get(), 5);
            danMuView.text = spannableString;

            // 弹幕文本背景
            danMuView.textBackground = ContextCompat.getDrawable(mContext.get(), R.drawable.corners_danmu);
            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext.get(), 15);
            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext.get(), 3);
            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext.get(), 3);
            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext.get(), 15);

            danMuView.enableTouch(true);
            danMuView.setOnTouchCallBackListener(listener);
        } else {
            // 显示的文本内容
            danMuView.textSize = DimensionUtil.spToPx(mContext.get(), 14);
            danMuView.textColor = ContextCompat.getColor(mContext.get(), R.color.main_color);
            danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext.get(), 5);

            if (entity.getRichText() != null) {
                danMuView.text = RichTextParse.parse(mContext.get(), entity.getRichText(), DimensionUtil.spToPx(mContext.get(), 18), false);
            } else {
                danMuView.text = entity.getText();
            }

            // 弹幕文本背景
            danMuView.textBackground = ContextCompat.getDrawable(mContext.get(), R.drawable.corners_danmu);
            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext.get(), 15);
            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext.get(), 3);
            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext.get(), 3);
            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext.get(), 15);
            danMuView.enableTouch(false);
        }

        return danMuView;
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            // 转换成Bitmap
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            // .9图片转换成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_4444 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, DimensionUtil.dpToPx(mContext.get(), 30), DimensionUtil.dpToPx(mContext.get(), 15));
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }


}