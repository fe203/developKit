package com.lyne.developkit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.lyne.uiview.imageView.BaseNetworkImageView;
import com.lyne.uiview.imageView.BaseNetworkRoundedImageView;

/**
 * Created by liht on 2018/8/29.
 */

public class ShowPicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_show_pic);

        BaseNetworkImageView imgNormal = findViewById(R.id.img_normal);
        BaseNetworkRoundedImageView imgRound = findViewById(R.id.img_round);

        String imgUrl = "https://i3.meishichina.com/attachment/recipe/2017/02/12/2017021214868864303048471547.JPG?x-oss-process=style/c320";

        imgNormal.setImageUrl(imgUrl, imgUrl);
        imgRound.setImageUrl(imgUrl, imgUrl);

    }
}
