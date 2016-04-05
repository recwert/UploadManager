package com.duoniu.uploadmanager.policy;

/**
 * Created by Huolongguo on 16/3/31.
 */
public class DefaultFilenamePolicy implements FilenamePolicy {

    @Override
    public String generatorFilename(String fileUri) {
        return fileUri;
    }
}
