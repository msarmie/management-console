
package org.duracloud.duradmin.domain;

/**
 * Stores content metadata.
 * 
 * @author Bill Branan
 */
public class ContentMetadata {

    private String mimetype;

    private String size;

    private String checksum;

    private String modified;

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
