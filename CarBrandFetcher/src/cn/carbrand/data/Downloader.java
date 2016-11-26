package cn.carbrand.data;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
/**
 * User: canghailan
 * Date: 11-12-9
 * Time: 下午2:03
 */
public class Downloader implements Callable<File> {
    protected int connectTimeout = 30 * 1000; // 连接超时:30s
    protected int readTimeout = 1 * 1000 * 1000; // IO超时:1min
 
    protected int speedRefreshInterval = 500; // 即时速度刷新最小间隔:500ms
 
    protected byte[] buffer;
 
    private URL url;
    private File file;
 
    private float averageSpeed;
    private float currentSpeed;
 
    public Downloader() {
        buffer = new byte[8 * 1024]; // IO缓冲区:8KB
    }
 
    public void setUrlAndFile(URL url, File file) {
        this.url = url;
        this.file = autoRenameIfExist(file);
        this.averageSpeed = 0;
        this.currentSpeed = 0;
    }
 
    public URL getUrl() {
        return url;
    }
 
    public File getFile() {
        return file;
    }
 
    public float getAverageSpeed() {
        return averageSpeed;
    }
 
    public float getCurrentSpeed() {
        return currentSpeed;
    }
 
    @Override
    public File call() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
 
        InputStream in = null;
        OutputStream out = null;
        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();
 
            in = conn.getInputStream();
            out = new FileOutputStream(file);
 
            int time = 0;
            int bytesInTime = 0;
            for (; ; ) {
                watch.split();
                int bytes = in.read(buffer);
                if (bytes == -1) {
                    break;
                }
                out.write(buffer, 0, bytes);
 
                time += watch.getTimeFromSplit();
                if (time >= speedRefreshInterval) {
                    currentSpeed = getSpeed(bytesInTime, time);
                    time = 0;
                    bytesInTime = 0;
                }
            }
        } catch (IOException e) {
            file.delete();
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                }
            }
        }
 
        watch.stop();
        averageSpeed = getSpeed(file.length(), watch.getTime());
 
        return file;
    }
 
    private static float getSpeed(long bytesInTime, long time) {
        return (float) bytesInTime / 1024 / ((float) time / 1000);
    }
 
    private static String getExtension(String string) {
        int lastDotIndex = string.lastIndexOf('.');
        // . ..
        if (lastDotIndex > 0) {
            return string.substring(lastDotIndex + 1);
        } else {
            return "";
        }
    }
 
    private static File autoRenameIfExist(File file) {
        if (file.exists()) {
            String path = file.getAbsolutePath();
 
            String extension = getExtension(path);
            int baseLength = path.length();
            if (extension.length() > 0) {
                baseLength = path.length() - extension.length() - 1;
            }
 
            StringBuilder buffer = new StringBuilder(path);
            for (int index = 1; index < Integer.MAX_VALUE; ++index) {
                buffer.setLength(baseLength);
                buffer.append('(').append(index).append(')');
                if (extension.length() > 0) {
                    buffer.append('.').append(extension);
                }
                file = new File(buffer.toString());
                if (!file.exists()) {
                    break;
                }
            }
 
        }
        return file;
    }
 
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        URL url = new URL("http://static.qichechaoren.com/upload/logo/ZhongXing.png");
        File file = new File("H:/workspace/CarBrandFetcher/WebContent/upload/logo/ZhongXing.png");
 
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Downloader downloader = new Downloader();
        downloader.setUrlAndFile(url, file);
        File downloadFIle = executorService.submit(downloader).get();
        System.out.println("download " + downloadFIle.getName() +
                " from " + url +
                " @" + downloader.getAverageSpeed() + "KB/s");
    }
}