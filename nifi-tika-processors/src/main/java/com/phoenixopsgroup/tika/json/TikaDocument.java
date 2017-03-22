/**
 * $Id$
 * <p>
 * Copyright (c) 2017
 */
package com.phoenixopsgroup.tika.json;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * com.phoenixopsgroup.tika.json.TikaDocument
 * <p/>
 * Classification:       NO CLASSIFICATION
 */
public class TikaDocument
{

    private String filename;
    private String path;
    private String date;
    private String uuid;
    private Map<String,String> metadata = new HashMap<>();
    private String content;

    public TikaDocument(String filename, String path, String date, String uuid, Map<String,String> metadata, String content)
    {
        this.setFilename(filename);
        this.setPath(path);
        this.setDate(date);
        this.setUuid(uuid);
        this.setMetadata(metadata);
        this.setContent(content);
    }
    
    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    
    public boolean equals(TikaDocument other)
    {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder
                .toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }


    public Map<String, String> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata)
    {
        this.metadata = metadata;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
}
