/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Joiner$MapJoiner
 *  com.google.common.collect.Maps
 *  com.google.common.net.UrlEscapers
 */
package com.sk89q.worldedit.util.net;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.net.UrlEscapers;
import com.sk89q.worldedit.util.io.Closer;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class HttpRequest
implements Closeable {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final int READ_BUFFER_SIZE = 8192;
    private final Map<String, String> headers = new HashMap<String, String>();
    private final String method;
    private final URL url;
    private String contentType;
    private byte[] body;
    private HttpURLConnection conn;
    private InputStream inputStream;
    private long contentLength = -1L;
    private long readBytes = 0L;

    private HttpRequest(String method, URL url) {
        this.method = method;
        this.url = url;
    }

    public HttpRequest body(String data) {
        this.body = data.getBytes();
        return this;
    }

    public HttpRequest bodyUrlEncodedForm(Form form) {
        this.contentType = "application/x-www-form-urlencoded";
        this.body = form.toUrlEncodedString().getBytes(StandardCharsets.UTF_8);
        return this;
    }

    public HttpRequest bodyMultipartForm(Form form) {
        this.contentType = "multipart/form-data;boundary=" + form.getFormDataSeparator();
        this.body = form.toFormDataString().getBytes(StandardCharsets.UTF_8);
        return this;
    }

    public HttpRequest header(String key, String value) {
        if (key.equalsIgnoreCase("Content-Type")) {
            this.contentType = value;
        } else {
            this.headers.put(key, value);
        }
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
            this.conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Java)");
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
            this.conn.setConnectTimeout(5000);
            this.conn.setReadTimeout(5000);
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
        throw new IOException("Did not get expected response code, got " + responseCode + " for " + String.valueOf(this.url));
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
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = this.inputStream.read()) != -1) {
                bos.write(b);
            }
            BufferedResponse bufferedResponse = new BufferedResponse(this, bos.toByteArray());
            return bufferedResponse;
        }
        finally {
            this.close();
        }
    }

    public HttpRequest saveContent(File file) throws IOException, InterruptedException {
        try (Closer closer = Closer.create();){
            FileOutputStream fos = closer.register(new FileOutputStream(file));
            BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
            this.saveContent(bos);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HttpRequest saveContent(OutputStream out) throws IOException, InterruptedException {
        try {
            long len;
            String field = this.conn.getHeaderField("Content-Length");
            if (field != null && (len = Long.parseLong(field)) >= 0L) {
                this.contentLength = len;
            }
        }
        catch (NumberFormatException field) {
            // empty catch block
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(this.inputStream);
            byte[] data = new byte[8192];
            int len = 0;
            while ((len = bis.read(data, 0, 8192)) >= 0) {
                out.write(data, 0, len);
                this.readBytes += (long)len;
            }
        }
        finally {
            this.close();
        }
        return this;
    }

    @Override
    public void close() {
        if (this.conn != null) {
            this.conn.disconnect();
        }
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
            return URI.create(url).toURL();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL reformat(URL existing) {
        try {
            return existing.toURI().toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            return existing;
        }
    }

    public static final class Form {
        private static final Joiner.MapJoiner URL_ENCODER = Joiner.on((char)'&').withKeyValueSeparator('=');
        private static final Joiner CRLF_JOINER = Joiner.on((String)"\r\n");
        public final Map<String, String> elements = new LinkedHashMap<String, String>();
        private final String formDataSeparator = "EngineHubFormData" + ThreadLocalRandom.current().nextInt(10000, 99999);

        private Form() {
        }

        public Form add(String key, String value) {
            this.elements.put(key, value);
            return this;
        }

        public String getFormDataSeparator() {
            return this.formDataSeparator;
        }

        public String toFormDataString() {
            String separatorWithDashes = "--" + this.formDataSeparator;
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> element : this.elements.entrySet()) {
                CRLF_JOINER.appendTo(builder, (Object)separatorWithDashes, (Object)("Content-Disposition: form-data; name=\"" + element.getKey() + "\""), new Object[]{"", element.getValue(), ""});
            }
            builder.append(separatorWithDashes).append("--");
            return builder.toString();
        }

        public String toUrlEncodedString() {
            return URL_ENCODER.join(this.elements.entrySet().stream().map(e -> Maps.immutableEntry((Object)UrlEscapers.urlFormParameterEscaper().escape((String)e.getKey()), (Object)UrlEscapers.urlFormParameterEscaper().escape((String)e.getValue()))).iterator());
        }

        public static Form create() {
            return new Form();
        }
    }

    public class BufferedResponse {
        private final byte[] data;

        private BufferedResponse(HttpRequest this$0, byte[] data) {
            this.data = data;
        }

        public byte[] asBytes() {
            return this.data;
        }

        public String asString(String encoding) throws IOException {
            return new String(this.data, encoding);
        }

        public BufferedResponse saveContent(File file) throws IOException, InterruptedException {
            try (Closer closer = Closer.create();){
                file.getParentFile().mkdirs();
                FileOutputStream fos = closer.register(new FileOutputStream(file));
                BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
                this.saveContent(bos);
            }
            return this;
        }

        public BufferedResponse saveContent(OutputStream out) throws IOException, InterruptedException {
            out.write(this.data);
            return this;
        }
    }
}

