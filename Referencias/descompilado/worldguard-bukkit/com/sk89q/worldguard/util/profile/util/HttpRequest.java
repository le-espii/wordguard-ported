/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.simple.JSONValue
 */
package com.sk89q.worldguard.util.profile.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.json.simple.JSONValue;

public class HttpRequest
implements Closeable {
    private static final Logger log = Logger.getLogger(HttpRequest.class.getCanonicalName());
    private static final int READ_TIMEOUT = 600000;
    private static final int READ_BUFFER_SIZE = 8192;
    private final Map<String, String> headers = new HashMap<String, String>();
    private final String method;
    private final URL url;
    private String contentType;
    private byte[] body;
    private HttpURLConnection conn;
    private InputStream inputStream;

    private HttpRequest(String method, URL url) {
        this.method = method;
        this.url = url;
    }

    public static HttpRequest get(URL url) {
        return HttpRequest.request("GET", url);
    }

    public static HttpRequest post(URL url) {
        return HttpRequest.request("POST", url);
    }

    public static HttpRequest request(String method, URL url) {
        return new HttpRequest(method, url);
    }

    public static URL url(String url) {
        try {
            return new URL(url);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL reformat(URL existing) {
        try {
            URL url = new URL(existing.toString());
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            return url;
        }
        catch (MalformedURLException | URISyntaxException e) {
            return existing;
        }
    }

    private static void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public HttpRequest bodyJson(Object object) throws IOException {
        this.contentType = "application/json";
        this.body = JSONValue.toJSONString((Object)object).getBytes();
        return this;
    }

    public HttpRequest bodyForm(Form form) {
        this.contentType = "application/x-www-form-urlencoded";
        this.body = form.toString().getBytes();
        return this;
    }

    public HttpRequest header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HttpRequest execute() throws IOException {
        boolean successful = false;
        try {
            if (this.conn != null) {
                throw new IllegalArgumentException("Connection already executed");
            }
            this.conn = (HttpURLConnection)HttpRequest.reformat(this.url).openConnection();
            if (this.body != null) {
                this.conn.setRequestProperty("Content-Type", this.contentType);
                this.conn.setRequestProperty("Content-Length", Integer.toString(this.body.length));
                this.conn.setDoInput(true);
            }
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                this.conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            this.conn.setRequestMethod(this.method);
            this.conn.setUseCaches(false);
            this.conn.setDoOutput(true);
            this.conn.setReadTimeout(600000);
            this.conn.connect();
            if (this.body != null) {
                DataOutputStream out = new DataOutputStream(this.conn.getOutputStream());
                out.write(this.body);
                out.flush();
                out.close();
            }
            this.inputStream = this.conn.getResponseCode() == 200 ? this.conn.getInputStream() : this.conn.getErrorStream();
            successful = true;
        }
        finally {
            if (!successful) {
                this.close();
            }
        }
        return this;
    }

    public HttpRequest expectResponseCode(int ... codes) throws IOException {
        int responseCode = this.getResponseCode();
        for (int code : codes) {
            if (code != responseCode) continue;
            return this;
        }
        this.close();
        throw new IOException("Did not get expected response code, got " + responseCode + " for " + this.url);
    }

    public int getResponseCode() throws IOException {
        if (this.conn == null) {
            throw new IllegalArgumentException("No connection has been made");
        }
        return this.conn.getResponseCode();
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedResponse returnContent() throws IOException, InterruptedException {
        if (this.inputStream == null) {
            throw new IllegalArgumentException("No input stream available");
        }
        try {
            int b;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((b = this.inputStream.read()) != -1) {
                HttpRequest.checkInterrupted();
                bos.write(b);
            }
            BufferedResponse bufferedResponse = new BufferedResponse(bos.toByteArray());
            return bufferedResponse;
        }
        finally {
            this.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HttpRequest saveContent(File file) throws IOException, InterruptedException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            this.saveContent(bos);
        }
        catch (Throwable throwable) {
            HttpRequest.closeQuietly(bos);
            HttpRequest.closeQuietly(fos);
            throw throwable;
        }
        HttpRequest.closeQuietly(bos);
        HttpRequest.closeQuietly(fos);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HttpRequest saveContent(OutputStream out) throws IOException, InterruptedException {
        try {
            int len;
            BufferedInputStream bis = new BufferedInputStream(this.inputStream);
            byte[] data = new byte[8192];
            while ((len = bis.read(data, 0, 8192)) >= 0) {
                out.write(data, 0, len);
                HttpRequest.checkInterrupted();
            }
        }
        finally {
            this.close();
        }
        return this;
    }

    @Override
    public void close() throws IOException {
        if (this.conn != null) {
            this.conn.disconnect();
        }
    }

    public static final class Form {
        public final List<String> elements = new ArrayList<String>();

        private Form() {
        }

        public static Form form() {
            return new Form();
        }

        public Form add(String key, String value) {
            try {
                this.elements.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
                return this;
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String element : this.elements) {
                if (first) {
                    first = false;
                } else {
                    builder.append("&");
                }
                builder.append(element);
            }
            return builder.toString();
        }
    }

    public static class BufferedResponse {
        private final byte[] data;

        private BufferedResponse(byte[] data) {
            this.data = data;
        }

        public byte[] asBytes() {
            return this.data;
        }

        public String asString(String encoding) throws IOException {
            return new String(this.data, encoding);
        }

        public Object asJson() throws IOException {
            return JSONValue.parse((String)this.asString("UTF-8"));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public BufferedResponse saveContent(File file) throws IOException, InterruptedException {
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            file.getParentFile().mkdirs();
            try {
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                this.saveContent(bos);
            }
            catch (Throwable throwable) {
                HttpRequest.closeQuietly(bos);
                HttpRequest.closeQuietly(fos);
                throw throwable;
            }
            HttpRequest.closeQuietly(bos);
            HttpRequest.closeQuietly(fos);
            return this;
        }

        public BufferedResponse saveContent(OutputStream out) throws IOException, InterruptedException {
            out.write(this.data);
            return this;
        }
    }
}

