package com.example.reminddoor.ui.barcode;

import android.graphics.Bitmap;
import android.graphics.Color;

import android.text.TextUtils;


import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;


public class QRCodeUtil {

    /**
     * Default method generate QR Code
     * @param content   String, the input message
     * @param width     int, width of QR Code
     * @param height    int, height of QR Code
     * @return  QR Code(Bitmap)
     */
    @Nullable
    static Bitmap generateQRCode(String content, int width, int height){
        return generateQRCode(content, width, height, "UTF-8", "H", "2", Color.BLACK, Color.WHITE);
    }


    /**
     * Customer generate QR code
     * @param content               String, the input message
     * @param width                 int, width of QR Code
     * @param height                int, height of QR Code
     * @param character_set         character set (Support: {@link CharacterSetECI})
     * @param error_correction      Error Correction Level {@link ErrorCorrectionLevel}
     * @param margin                default = 4, >= 0
     * @param color_black           color of black blocks
     * @param color_white           color of white blocks
     * @return                      QR Code
     */
    @Nullable
    static Bitmap generateQRCode(String content, int width, int height,
                                            @Nullable String character_set, @Nullable String error_correction, @Nullable String margin,
                                            @ColorInt int color_black, @ColorInt int color_white){

        if(TextUtils.isEmpty(content)){
            return null;
        }

        if(width < 0 || height < 0){
            return null;
        }

        try {
            // Generate bit matrix
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();

            if(!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set); // 字符转码格式设置
            }

            if(!TextUtils.isEmpty(error_correction)){
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction); // 容错级别设置
            }

            if(!TextUtils.isEmpty(margin)){
                hints.put(EncodeHintType.MARGIN, margin); // 空白边距设置
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // generate pixel matrix using bit matrix
            int[] pixels = new int[width * height];
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    if(bitMatrix.get(x, y)){
                        pixels[y * width + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white; // 白色色块像素设置
                    }
                }
            }

            // generates bit map
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }







}
