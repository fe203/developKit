package com.lyne.developkit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.lyne.fw.image.ImageUtils;
import com.lyne.uiview.imageView.BaseNetworkRoundedImageView;

/**
 * Created by liht on 2018/8/29.
 */

public class ShowPicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_show_pic);

        final ImageView imgNormal = findViewById(R.id.img_normal);
        final BaseNetworkRoundedImageView imgRound = findViewById(R.id.img_round);

        String imgUrl = "https://i3.meishichina.com/attachment/recipe/2017/02/12/2017021214868864303048471547.JPG?x-oss-process=style/c320";

        imgRound.setImageUrl(imgUrl, imgUrl);

        imgRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = drawContentView(imgRound);
                    ImageUtils.saveBitmap(ShowPicActivity.this, bitmap, "picpic.jpeg", true);
                    imgNormal.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private Bitmap drawContentView(View contentView){
        Bitmap bitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        contentView.draw(c);
        return bitmap;
    }
}
