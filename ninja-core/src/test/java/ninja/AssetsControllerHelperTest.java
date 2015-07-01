/**
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FilenameUtils.class)
public class AssetsControllerHelperTest {

    AssetsControllerHelper assetsControllerHelper;

    @Before
    public void setup() {
        assetsControllerHelper = new AssetsControllerHelper();
    }

    @Test
    public void testNormalizePathWithoutLeadingSlash() {
        assertEquals("dir1/test.test", assetsControllerHelper.normalizePathWithoutLeadingSlash("/dir1/test.test", true));
        assertEquals("dir1/test.test", assetsControllerHelper.normalizePathWithoutLeadingSlash("dir1/test.test", true));
        assertEquals(null, assetsControllerHelper.normalizePathWithoutLeadingSlash("/../test.test", true));
        assertEquals(null, assetsControllerHelper.normalizePathWithoutLeadingSlash("../test.test", true));
        assertEquals("dir2/file.test", assetsControllerHelper.normalizePathWithoutLeadingSlash("/dir1/../dir2/file.test", true));
        assertEquals(null, assetsControllerHelper.normalizePathWithoutLeadingSlash(null, true));
        assertEquals("", assetsControllerHelper.normalizePathWithoutLeadingSlash("", true));
    }

    @Test
    public void testNormalizePathWithoutLeadingSlashCorrectFilnameUtilStaticMethodsCalled() {
        PowerMockito.mockStatic(FilenameUtils.class, Mockito.CALLS_REAL_METHODS);

        assetsControllerHelper.normalizePathWithoutLeadingSlash("/dir1/test.test", false);
        PowerMockito.verifyStatic();
        FilenameUtils.normalize("/dir1/test.test");

        assetsControllerHelper.normalizePathWithoutLeadingSlash("/dir1/test.test", true);
        PowerMockito.verifyStatic();
        FilenameUtils.normalize("/dir1/test.test", true);
    }
}
