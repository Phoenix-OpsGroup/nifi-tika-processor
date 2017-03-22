/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phoenixopsgroup.tika;

import com.google.gson.Gson;
import com.phoenixopsgroup.tika.json.TikaDocument;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Tags({"tika", "tiki", "doc", "docx", "ppt", "xls", "xlst", "pdf"})
@CapabilityDescription("Extract text from most file types")
public class TikaProcessor extends AbstractProcessor
{

    public static final PropertyDescriptor OUTPUT_JSON = new PropertyDescriptor.Builder()
            .name("Output JSON")
            .description("Indicates whether to write content and metadata as JSON")
            .required(true)
            .allowableValues("true", "false")
            .defaultValue("true")
            .build();

    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("SUCCESS")
            .description("Success relationship")
            .build();
    public static final Relationship FAILURE = new Relationship.Builder()
            .name("FAILURE")
            .description("Failure relationship")
            .build();
    public static final Relationship UNRECOGNIZED = new Relationship.Builder()
            .name("UNRECOGNIZED")
            .description("Unrecognized format relationship")
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context)
    {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        descriptors.add(OUTPUT_JSON);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(SUCCESS);
        relationships.add(FAILURE);
        relationships.add(UNRECOGNIZED);

        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships()
    {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors()
    {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context)
    {

    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session)
            throws ProcessException
    {
        final AtomicReference<String> value = new AtomicReference<>();
        final AtomicReference<Metadata> atomicMetadata = new AtomicReference<>();


        FlowFile flowFile = session.get();
        if (flowFile == null)
        {
            return;
        }

        session.read(flowFile, new InputStreamCallback()
        {
            public void process(InputStream in)
                    throws IOException
            {
                try
                {
                    AutoDetectParser parser = new AutoDetectParser();
                    BodyContentHandler handler = new BodyContentHandler(-1);
                    Metadata metadata = new Metadata();

                    parser.parse(in, handler, metadata);
                    atomicMetadata.set(metadata);
                    value.set(handler.toString());
                } catch (Exception ex)
                {
                    getLogger().error("TIKA failed to read/process file:" + ex.getMessage());
                }
            }
        });

        if (null != value.get())
        {

            for (String name : atomicMetadata.get().names())
            {
                if (null != atomicMetadata.get().get(name))
                {
                    flowFile = session.putAttribute(flowFile, "tika.metadata." + name, atomicMetadata.get().get(name));
                }
            }


            if (context.getProperty(OUTPUT_JSON).asBoolean().equals(true))
            {
                // JSON

                final Gson gson = new Gson();

                Map<String, String> metadata = new HashMap<>();
                for (String name : atomicMetadata.get().names())
                {
                    metadata.put(name, atomicMetadata.get().get(name));
                }

                final TikaDocument tikaDocument = new TikaDocument(
                        flowFile.getAttribute("filename"),
                        flowFile.getAttribute("path"),
                        flowFile.getAttribute("entryDate"),
                        flowFile.getAttribute("uuid"),
                        metadata,
                        value.get()
                );

                flowFile = session.write(flowFile, new OutputStreamCallback()
                {
                    @Override
                    public void process(OutputStream out)
                            throws IOException
                    {
                        out.write(gson.toJson(tikaDocument).getBytes());
                    }
                });

                session.transfer(flowFile, SUCCESS);

            }
            else
            {
                // TEXT
                flowFile = session.write(flowFile, new OutputStreamCallback()
                {
                    @Override
                    public void process(OutputStream out)
                            throws IOException
                    {
                        out.write(value.get().getBytes());
                    }
                });

                session.transfer(flowFile, SUCCESS);
            }

        }
        else
        {
            session.transfer(flowFile, UNRECOGNIZED);
        }
    }
}
