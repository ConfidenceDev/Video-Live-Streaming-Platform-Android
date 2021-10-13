package me.vebbo.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Translation {

    public static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
        return out.toByteArray();
    }

    public static byte[] YUV420toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }

    public static Bitmap resizeBitmap(Bitmap bm, int width, int height){
        double ratioSquare;
        int MAX_SIZE = width * height;
        int bitHeight, bitWidth;
        bitHeight = bm.getHeight();
        bitWidth = bm.getWidth();
        ratioSquare = (bitHeight * bitWidth) / MAX_SIZE;
        if (ratioSquare <= 1)
            return bm;
        double ratio = Math.sqrt(ratioSquare);
        int reqHeight = (int) Math.round(bitHeight / ratio);
        int reqWidth = (int) Math.round(bitWidth / ratio);
        return Bitmap.createScaledBitmap(bm, reqWidth, reqHeight, true);
    }

    public static Bitmap setCamStream(byte[] decompressed, int rotationDegrees) {
        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bm = BitmapFactory.decodeByteArray(decompressed, 0, decompressed.length, bitmap_options);
        Bitmap bitmap = bm;

        if (rotationDegrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm,
                    (int) (bm.getWidth() / 1.8), (int)(bm.getHeight() / 1.8), true);
            bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    public static byte[] compressor(byte[] in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(in.length);
            Deflater deflater = new Deflater();
            deflater.setInput(in);
            deflater.setLevel(Deflater.BEST_COMPRESSION);
            deflater.finish();

            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                out.write(buffer, 0, count);
            }
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decompressor(byte[] in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(in.length);
            Inflater inflater = new Inflater();
            inflater.setInput(in);

            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                out.write(buffer, 0, count);
            }
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getBytes(Context context, Uri uri) throws IOException {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            return getBytes(inputStream);
        }
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        byte[] bytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            bytes = baos.toByteArray();
        }
        return bytes;
    }

    public static String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        /*if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if( hours >= 24) {
                int days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days,hours%24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }*/
        //return String.format("00:%02d:%02d", minutes, seconds);
        return String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
    }

    protected static final String ALGO = "AES";
    protected static final String SHA = "VEBBOSHA1PRNG";
    protected static byte[] keyValue = SHA.getBytes();

    public static byte[] encrypt(byte[] data) throws Exception{
        Key key = generateKey();
        Cipher c =  Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(data);
    }

    public static byte[] decrypt(byte[] data) throws Exception{
        Key key = generateKey();
        Cipher c =  Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        return c.doFinal(data);
    }

    protected static Key generateKey() throws Exception{
        /*byte[] keyStart = "vebbo-key".getBytes();
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGO);
        SecureRandom sr = SecureRandom.getInstance(SHA);
        sr.setSeed(keyStart);
        keyGenerator.init(128, sr);
        byte[] key = keyGenerator.generateKey().getEncoded();*/
        return new SecretKeySpec(keyValue, ALGO);
    }
}
