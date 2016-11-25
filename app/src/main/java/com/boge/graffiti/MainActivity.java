package com.boge.graffiti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.graffiti)
    FrameLayout graffiti;
    @Bind(R.id.ll_buttom)
    LinearLayout llButtom;
    @Bind(R.id.iv_line)
    ImageView ivLine;
    @Bind(R.id.iv_color)
    ColorImage ivColor;

    private int fontSize = 5;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initGraffiti();
    }

    private GraffitiView graffitiView;

    private void initGraffiti() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int screenWidth = mDisplayMetrics.widthPixels;
        int screenHeight = mDisplayMetrics.heightPixels;
        graffitiView = new GraffitiView(this, screenWidth, screenHeight);
        graffiti.addView(graffitiView);
    }

    private boolean isLine = true;

    @OnClick({R.id.btn_y, R.id.btn_n, R.id.iv_prev, R.id.iv_recover, R.id.iv_font, R.id.iv_color, R.id.iv_line, R.id.iv_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_y:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("是否保存").setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                graffitiView.saveBitmap();
                            }
                        }).show();
                break;
            case R.id.btn_n:
                break;
            case R.id.iv_prev://撤销
                graffitiView.undo();
                break;
            case R.id.iv_recover://恢复
                graffitiView.recover();
                break;
            case R.id.iv_font://设置字体
                openFont();
                break;
            case R.id.iv_color://设置颜色
                openColor();
                break;
            case R.id.iv_line://设置是否是橡皮擦
                if (isLine) {
                    ivLine.setImageResource(R.mipmap.eraser);
                } else {
                    ivLine.setImageResource(R.mipmap.line);
                }
                graffitiView.setEraser(isLine);
                isLine = !isLine;
                break;
            case R.id.iv_clear://清除
                graffitiView.clear();
                break;
        }
    }

    private void openColor() {
        View view = View.inflate(this, R.layout.popup_color, null);
        ColorImage iv_black = (ColorImage) view.findViewById(R.id.iv_black);
        ColorImage iv_green = (ColorImage) view.findViewById(R.id.iv_green);
        ColorImage iv_blue = (ColorImage) view.findViewById(R.id.iv_blue);
        ColorImage iv_red = (ColorImage) view.findViewById(R.id.iv_red);
        ColorImage iv_yellow = (ColorImage) view.findViewById(R.id.iv_yellow);
        switch (index) {
            case 0:
                iv_black.setHasArc(true);
                break;
            case 1:
                iv_green.setHasArc(true);
                break;
            case 2:
                iv_blue.setHasArc(true);
                break;
            case 3:
                iv_red.setHasArc(true);
                break;
            case 4:
                iv_yellow.setHasArc(true);
                break;
        }
        int[] location = new int[2];
        llButtom.getLocationOnScreen(location);
        final PopupWindow colorPopup = new PopupWindow(view, graffiti.getWidth(), 160, true);
        colorPopup.setTouchable(true);
        colorPopup.setBackgroundDrawable(new BitmapDrawable());
        colorPopup.showAtLocation(llButtom, Gravity.NO_GRAVITY, location[0], location[1] - colorPopup.getHeight() - 24 * 4);
        iv_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 0;
                ivColor.setBackColor(Color.BLACK);
                graffitiView.setPaintColor(Color.BLACK);
                colorPopup.dismiss();
            }
        });
        iv_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 1;
                ivColor.setBackColor(Color.GREEN);
                graffitiView.setPaintColor(Color.GREEN);
                colorPopup.dismiss();
            }
        });
        iv_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 2;
                ivColor.setBackColor(Color.BLUE);
                graffitiView.setPaintColor(Color.BLUE);
                colorPopup.dismiss();
            }
        });
        iv_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 3;
                ivColor.setBackColor(Color.RED);
                graffitiView.setPaintColor(Color.RED);
                colorPopup.dismiss();
            }
        });
        iv_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 4;
                ivColor.setBackColor(Color.YELLOW);
                graffitiView.setPaintColor(Color.YELLOW);
                colorPopup.dismiss();
            }
        });
    }

    /**
     * 打开字体设置
     */
    private void openFont() {
        View view = View.inflate(this, R.layout.popup_font, null);
        final TextView tvFont = (TextView) view.findViewById(R.id.tv_font);
        final SeekBar sbFont = (SeekBar) view.findViewById(R.id.sb_font);
        sbFont.setMax(31);
        sbFont.setProgress(fontSize - 5);
        tvFont.setText(fontSize + "");
        sbFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fontSize = seekBar.getProgress() + 5;
                tvFont.setText(fontSize + "");
                graffitiView.setSize(fontSize);
            }
        });
        int[] location = new int[2];
        llButtom.getLocationOnScreen(location);
        PopupWindow fontPopup = new PopupWindow(view, graffiti.getWidth(), 90, true);
        fontPopup.setTouchable(true);
        fontPopup.setBackgroundDrawable(new BitmapDrawable());
        fontPopup.showAtLocation(llButtom, Gravity.NO_GRAVITY, location[0], location[1] - fontPopup.getHeight() - 24 * 4);
    }

}
