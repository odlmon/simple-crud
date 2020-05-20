package zip;

import service.ExtensionData;
import service.ObjectCodec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipCodec implements ObjectCodec {

    @Override
    public ExtensionData getExtensionData() {
        return new ExtensionData("Zip files (*.zip)", "*.zip");
    }

    public void encode(File file) {
        try (var fos = new FileOutputStream(file.getCanonicalPath().concat(".zip"));
             var zipOut = new ZipOutputStream(fos);
             var fis = new FileInputStream(file)) {
            var zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(file.delete()?"delete":"fail");
    }

    //check some shit exception, idk, prevent writing outside target folder
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        var destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    public void decode(File file) {
        try {
            String fileZip = file.getCanonicalPath();
            File destDir = new File(fileZip.replace(file.getName(), ""));
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(file.delete()?"delete":"fail");
    }
}

