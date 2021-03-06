/**
 * UNCLASSIFIED
 *
 * Copyright 2020 Northrop Grumman Systems Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ngc.seaside.jellyfish.service.impl.propertyservice;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 *
 */
public class PropertiesTest {

   private Properties fixture;

   @Test
   public void doesLoad() throws URISyntaxException, IOException {
      fixture = new Properties();
      URL propertiesURL = getClass().getClassLoader().getResource("dynamic.properties");
      assertNotNull(propertiesURL);
      File file = new File(propertiesURL.toURI());

      fixture.load(Paths.get(file.getAbsolutePath()));
      fixture.evaluate();

      assertEquals("MyClass", fixture.get("classname"));
      assertEquals("com.ngc.seaside", fixture.get("groupId"));
      assertEquals("myclass", fixture.get("artifactId"));
      assertEquals("com.ngc.seaside.myclass", fixture.get("package"));
      assertEquals("com-ngc-seaside-myclass", fixture.get("dashPackage"));
   }


   @Test
   public void doesReevaluate() throws URISyntaxException, IOException {
      fixture = new Properties();
      URL propertiesURL = getClass().getClassLoader().getResource("dynamic.properties");
      assertNotNull(propertiesURL);
      File file = new File(propertiesURL.toURI());

      fixture.load(Paths.get(file.getAbsolutePath()));
      fixture.evaluate();

      assertEquals("MyClass", fixture.get("classname"));
      assertEquals("my-class", fixture.get("hyphens"));
      assertEquals("com.ngc.seaside", fixture.get("groupId"));
      assertEquals("myclass", fixture.get("artifactId"));
      assertEquals("com.ngc.seaside.myclass", fixture.get("package"));
      assertEquals("com-ngc-seaside-myclass", fixture.get("dashPackage"));

      fixture.put("classname", "ADiffClass");
      fixture.evaluate();

      assertEquals("ADiffClass", fixture.get("classname"));
      assertEquals("adiffclass", fixture.get("artifactId"));
   }

}
