package in.srain.cube.image.iface;

import java.io.OutputStream;

import in.srain.cube.image.ImageTask;

public interface ImageDownloader {

    public boolean downloadToStream(ImageTask imageTask,
                                    String url,
                                    OutputStream outputStream,
                                    ProgressUpdateHandler progressUpdateHandler);
}