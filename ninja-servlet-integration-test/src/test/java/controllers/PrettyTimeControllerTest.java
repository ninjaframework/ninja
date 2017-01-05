/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaTest;

import org.junit.Test;

import com.google.common.collect.Maps;

public class PrettyTimeControllerTest extends NinjaTest {

    String TEXT_EN = "1 day ago";
    String TEXT_DE = "vor 1 Tag";
    String TEXT_FR = "il y a 1 jour";
    String TEXT_IT = "1 giorno fa";
    String TEXT_ZH = "1 天 前";
    String TEXT_JA = "1日前";
    String TEXT_KO = "1일 전";

    @Test
    public void testThatPrettyTimeWorksEn() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "en-US");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_EN));

    }

    @Test
    public void testThatPrettyTimeWorksDe() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "de-DE");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_DE));

    }

    @Test
    public void testThatPrettyTimeWorksFr() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "fr-FR");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_FR));

    }

    @Test
    public void testThatPrettyTimeWorksIt() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "it-IT");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_IT));

    }

    @Test
    public void testThatPrettyTimeWorksZh() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "zh-CN");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_ZH));

    }

    @Test
    public void testThatPrettyTimeWorksJa() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "ja-JP");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_JA));

    }

    @Test
    public void testThatPrettyTimeWorksKo() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "ko-KO");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/prettyTime", headers);

        assertTrue(result
                .contains(TEXT_KO));

    }

}
