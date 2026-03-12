package com.vol.solunote.comm;

import org.springframework.core.io.ByteArrayResource;

public class FilenameAwareByteArrayResource extends ByteArrayResource {
	
    private final String filename;

    public FilenameAwareByteArrayResource(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

}