package sevenzip;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import service.ExtensionData;
import service.ObjectCodec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SevenZCodec implements ObjectCodec {

    @Override
    public ExtensionData getExtensionData() {
        return new ExtensionData("7z files (*.7z)", "*.7z");
    }

    private void addToArchiveCompression(SevenZOutputFile out, File file, String dir) {
        try {
            String name = dir + File.separator + file.getName();
            if (file.isFile()){
                SevenZArchiveEntry entry = out.createArchiveEntry(file, name);
                out.putArchiveEntry(entry);
                FileInputStream in = new FileInputStream(file);
                byte[] b = new byte[1024];
                int count = 0;
                while ((count = in.read(b)) > 0) {
                    out.write(b, 0, count);
                }
                out.closeArchiveEntry();
            } else if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null){
                    for (File child : children){
                        addToArchiveCompression(out, child, name);
                    }
                }
            } else {
                System.out.println(file.getName() + " is not supported");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encode(File file) {
        try (var out = new SevenZOutputFile(new File(file.getCanonicalPath().concat(".7z")))){
            addToArchiveCompression(out, file, ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(file.delete()?"delete":"fail");
    }

    public void decode(File file) {
        try {
            String destination = file.getCanonicalPath().replace(file.getName(), "");
            SevenZFile sevenZFile = new SevenZFile(file);
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null){
                if (entry.isDirectory()){
                    continue;
                }
                File curFile = new File(destination, entry.getName());
                File parent = curFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(curFile);
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
    }
}

