package com.xx.javademo.IO;

import android.util.Log;

import com.xx.commonlib.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IOCase2 {
    private static final String content =
                    "有人以高价出售木炭，让卫斯理感到疑惑和好奇，经多番探问下才发现木炭的秘密，涉及当年太平天国战士林玉声灵魂出窍一事。\n" +
                    "林玉声隶属忠王李秀成麾下，当太平天国快将灭亡时，替忠王收藏一批宝物于萧县猫爪坳一棵树干中，事成后却险些遭到灭口。大难不死的林玉声，灵魂竟两度离开身躯，进入树中。\n" +
                    "他醒来后，把经过一一记下，临终时再回到树中。他的后人林子渊得悉一切后，决定像祖先般寻找生命第二形式，但树干已被砍下，并投进炭窑。\n" +
                    "林子渊遂在生火的一刻，跳进了炭窑，他的灵魂进入一块被烧剩的木炭内，而林玉声的灵魂却离开了，进入他的第三形式。\n" +
                    "炭帮帮主四叔死后，声势日渐没落，其遗孀四婶于是把木炭卖给了卫斯理。\n" +
                    "卫对木炭进行研究，发现木炭发出高频率的声波，欲与外人沟通。\n" +
                    "友人陈长青从声波图中得知林子渊要求他们把木炭烧了，使他能够进入第三形式的生命，但自此各人已无法跟子渊再联络上了。\n\n\n";

    public void writeContentToFile() {
        FileOutputStream outputStream = null;

        String fileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_Content.txt";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        long start = System.currentTimeMillis();
        FileChannel channel = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(fileName);
            channel = outputStream.getChannel();
            ByteBuffer wrap = ByteBuffer.wrap(content.getBytes());
            for (int i = 0; i < 200; i++) {
                channel.write(wrap);
                wrap.flip();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("MyCodeHubs_IO_Case2", "writeContentToFile: 耗时 = " + (System.currentTimeMillis() - start));
    }

    public void writeContentToFileV2() {
        Writer writer = null;

        String fileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_Content.txt";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        long start = System.currentTimeMillis();

        try {
            writer = new FileWriter(fileName);
            for (int i = 0; i < 200; i++) {
                writer.write(content);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("MyCodeHubs_IO_Case2", "writeContentToFileV2: 耗时 = " + (System.currentTimeMillis() - start));
    }

    public void readContentFromFile() {
        String fileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_Content.txt";
        File file = new File(fileName);
        if (file.exists()) {
            FileInputStream inputStream = null;
            long start = System.currentTimeMillis();
            FileChannel channel = null;
            try {
                inputStream = new FileInputStream(fileName);
                channel = inputStream.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                byte[] data = new byte[byteBuffer.capacity()];
                StringBuilder stringBuilder = new StringBuilder();
                while (channel.read(byteBuffer) > 0) {
                    byteBuffer.flip();
                    byteBuffer.get(data, 0, byteBuffer.limit());
                    stringBuilder.append(new String(data, 0, byteBuffer.limit()));
                    byteBuffer.clear();
                }
                Log.e("MyCodeHubs_IO_Case2", "readContentFromFile: \n" + stringBuilder.toString());
                Log.e("MyCodeHubs_IO_Case2", "readContentFromFile: 耗时 = " + (System.currentTimeMillis() - start));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (channel != null) {
                        channel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyContentToNewFile() {
        long start = System.currentTimeMillis();
        String fileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_Content.txt";
        File file = new File(fileName);
        if (file.exists()) {
            FileChannel in = null;
            FileChannel out = null;
            try {
                in = new FileInputStream(fileName).getChannel();

                String newFileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_New_Content.txt";
                file = new File(newFileName);
                if (file.exists() || file.createNewFile()) {
                    out = new FileOutputStream(newFileName).getChannel();
                    in.transferTo(0, in.size(), out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("MyCodeHubs_IO_Case2", "copyContentToNewFile: 耗时 = " + (System.currentTimeMillis() - start));
    }

    public void copyContentToNewFileV2() {
        long start = System.currentTimeMillis();
        String fileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_Content.txt";
        File file = new File(fileName);
        if (file.exists()) {
            BufferedReader in = null;
            BufferedWriter out = null;
            try {
                in = new BufferedReader(new FileReader(fileName));

                String newFileName = FileUtil.getSDPath() + "MyCodeHubs_IO_Case2_New_Content.txt";
                file = new File(newFileName);
                if (file.exists() || file.createNewFile()) {
                    out = new BufferedWriter(new FileWriter(newFileName));
                    String s;
                    while ((s = in.readLine()) != null) {
                        out.write(s, 0, s.length());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("MyCodeHubs_IO_Case2", "copyContentToNewFileV2: 耗时 = " + (System.currentTimeMillis() - start));
    }
}
