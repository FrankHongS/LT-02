package com.hon.librarytest02.camera.camerax;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ImageUploader extends AsyncTask<Bitmap, Void, String> {
    public static final int MAX_DIMENSION = 3000;//px
    private static final String TAG = "ImageUploader";
    private static final int UPLOAD_MAX_SIZE = 1024 * 1024; // 1 MB
    private static final int TIME_OUT = 10 * 10000000; //超时时间
    private static final String LINE_END = "\r\n";
    private static final String PREFIX = "--";
    private static final String REQUEST_URL = "https://www.bing.com/images/detail/upload";
    private Callback mCallback;

    public ImageUploader(Context context, Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            mCallback.onRequest();
        }
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        return uploadFile(bitmaps[0]);
    }


    @Override
    protected void onPostExecute(String result) {
        if (mCallback != null) {
            mCallback.onResult(result);
        }
    }

    private String uploadFile(Bitmap bitmap) {
        HttpURLConnection conn = null;
        Writer writer = null;
        try {
            String base64Image = compressAndToBase64(bitmap);
            String BOUNDARY = UUID.randomUUID().toString();
            String CONTENT_TYPE = "multipart/form-data";
            URL url = new URL(REQUEST_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
            conn.setRequestProperty("X-Search-UILang", "en-US");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept-Encoding", "gzip");

            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + PREFIX + BOUNDARY);
            writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            String bodyString = PREFIX + BOUNDARY + LINE_END +
                    "Content-Disposition: form-data; name=\"imageBin\"" + LINE_END + LINE_END +
                    base64Image + LINE_END +
                    PREFIX + BOUNDARY + PREFIX;
            writer.write(bodyString);
            writer.flush();
            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream input = conn.getInputStream();
                String responseString = zipInputStream(input);
                JSONObject jsonObject = new JSONObject(responseString);
                return jsonObject.optString("url");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String toBase64Image(ByteArrayOutputStream baos) {
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private String zipInputStream(InputStream is) throws IOException {
        GZIPInputStream gzip = new GZIPInputStream(is);
        BufferedReader in = new BufferedReader(new InputStreamReader(gzip, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
            sb.append(line);
        is.close();
        return sb.toString();
    }

    private int getDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            //do nothing
        }

        return degree;
    }

    // compress and convert into base64
    private String compressAndToBase64(Bitmap bitmap) {
        Log.d(TAG, "compress: " + bitmap.getHeight() + "," + bitmap.getWidth());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String base64Image = "";
        for (int quality = 100; quality > 0; quality -= 5) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            //body size must be smaller than UPLOAD_MAX_SIZE
            if (baos.size() < UPLOAD_MAX_SIZE - 30000) {
                base64Image = toBase64Image(baos);
                if (base64Image.length() < UPLOAD_MAX_SIZE - 30000) {
                    break;
                }
            }
        }
        return base64Image;
    }

    interface Callback {
        void onRequest();

        void onResult(String url);
    }
}
