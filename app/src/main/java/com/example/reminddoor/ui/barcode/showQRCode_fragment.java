package com.example.reminddoor.ui.barcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.example.reminddoor.R;

public class showQRCode_fragment extends DialogFragment {
    String msg;

    public showQRCode_fragment(String msg) {
        this.msg = msg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qrcode, container);
        ImageView imageView = v.findViewById(R.id.iv_barcode);
        Bitmap qrcode = QRCodeUtil.generateQRCode(msg, 600, 600);
        imageView.setImageBitmap(qrcode);
        return v;
    }
}
