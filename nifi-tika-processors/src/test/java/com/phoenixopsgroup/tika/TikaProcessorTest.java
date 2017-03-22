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
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class TikaProcessorTest {

    private TestRunner testRunner;

    @Before
    public void init() {
        testRunner = TestRunners.newTestRunner(TikaProcessor.class);
    }

    @Test @Ignore
    public void testProcessor() {

        Gson gson = new Gson();

        Map<String,String> metadata = new HashMap<>();
        
        metadata.put("tiki.metadata.orgfile","myTest.doc");
        metadata.put("tiki.metadata.fileType","MS Word");

        //(String filename, String path, String date, String uuid, Map<String,String> metadata, String content)

        TikaDocument tikaDocument = new TikaDocument(
                "theFilename",
                "dir/subdir1/subdir2",
                "2017-03-22T13:25:42+0000",
                "1bd086ce-fd4f-4a93-b2bf-a0c654333c6a",
                metadata,
                "This is the content"
        );

        System.out.println(gson.toJson(tikaDocument));


    }

}
