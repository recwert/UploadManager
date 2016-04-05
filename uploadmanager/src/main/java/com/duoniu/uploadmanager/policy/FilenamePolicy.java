package com.duoniu.uploadmanager.policy;

/**
 * Created by Huolongguo on 16/3/31.
 */
public interface FilenamePolicy {

    String generatorFilename(String fileUri);
}
