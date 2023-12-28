
package de.waldheinz.fs.util;

import android.util.Log;

import de.waldheinz.fs.fat.FatFile;
import de.waldheinz.fs.fat.FatFileSystem;
import de.waldheinz.fs.fat.FatLfnDirectory;
import de.waldheinz.fs.fat.FatLfnDirectoryEntry;
import de.waldheinz.fs.fat.FatType;
import de.waldheinz.fs.fat.SuperFloppyFormatter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Matthias Treydte &lt;mt at waldheinz.de&gt;
 */
public final class ImageBuilder {
    
    public static ImageBuilder of(File[] files) throws IOException {
        return new ImageBuilder(files);
    }

    private void copyContents(File f, FatFile file)
            throws IOException {
        
        final RandomAccessFile raf = new RandomAccessFile(f, "r");
        
        try {
            final FileChannel fc = raf.getChannel();
            long dstOffset = 0;

            while (true) {
                final int read = fc.read(this.buffer);

                if (read >= 0) {
                    this.buffer.flip();
                    file.write(dstOffset, this.buffer);
                    this.buffer.clear();
                    dstOffset += read;
                } else {
                    break;
                }
            }
        } finally {
            this.buffer.clear();
            raf.close();
        }
    }

    private final File[] files;
    private final ByteBuffer buffer;

    private ImageBuilder(File[] files) {
        this.files = files;
        this.buffer = ByteBuffer.allocate(1024 * 1024);
    }

    public void createDiskImage(File outFile, long size) throws IOException {
        FileDisk fd = FileDisk.create(outFile, size);
        final FatFileSystem fs = SuperFloppyFormatter
                .get(fd).setFatType(FatType.FAT32).setVolumeLabel("X16 Android").format();

        final int FIRST_SECTOR = 512;

        byte[] mbr = new byte[FIRST_SECTOR];
        mbr[0x1c2] = 0x0c; // Partition type: FAT32 with LBA
        mbr[0x1c6] = 0x01; // LBA of first absolute sector

        try {
            this.copyRec(this.files, fs.getRoot());
        } finally {
            fs.close();
            fd.close();
        }

        // I don't know why we have to do this song and dance
        // but it can't be done in try before closing file.
        fd = new FileDisk(outFile, false);
        ByteBuffer buffer = ByteBuffer.allocate((int)size);
        fd.read(0, buffer);
        buffer.rewind();
        fd.write(FIRST_SECTOR, buffer);

        ByteBuffer buffer2 = ByteBuffer.wrap(mbr);
        fd.write(0, buffer2);

        fd.close();
    }

//    public void addToDisk() throws IOException {
//        FileDisk fd = new FileDisk(imageRoot, false);
//        FatFileSystem fs = new FatFileSystem(fd, false);
//        this.copyRec(this.imageRoot, fs.getRoot());
//    }
    
    private void copyRec(File[] files, FatLfnDirectory dst) throws IOException {
        for (File f : files) {
            if (f.isDirectory()) {
                Log.e("tough", "not implemented");
                //final FatLfnDirectoryEntry de = dst.addDirectory(f.getName());
                //copyRec(f, de.getDirectory());
            } else if (f.isFile()) {
                final FatLfnDirectoryEntry de = dst.addFile(f.getName());
                final FatFile file = de.getFile();
                copyContents(f, file);
            }
            
        }
    }
    
}
