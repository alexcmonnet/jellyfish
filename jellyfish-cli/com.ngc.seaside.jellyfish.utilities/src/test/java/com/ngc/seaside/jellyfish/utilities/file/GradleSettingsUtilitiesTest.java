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
package com.ngc.seaside.jellyfish.utilities.file;

import com.ngc.seaside.jellyfish.api.DefaultParameter;
import com.ngc.seaside.jellyfish.api.IParameter;
import com.ngc.seaside.jellyfish.api.IParameterCollection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class GradleSettingsUtilitiesTest {

   @Rule
   public TemporaryFolder testFolder = new TemporaryFolder();

   @SuppressWarnings("unchecked")
   @Test
   public void doesAddProject() throws IOException, FileUtilitiesException {
      IParameterCollection collection = mock(IParameterCollection.class);
      when(collection.containsParameter("outputDirectory")).thenReturn(true);
      when(collection.containsParameter("groupId")).thenReturn(true);
      when(collection.containsParameter("artifactId")).thenReturn(true);

      File outputDirectory = testFolder.newFolder("output");
      Path settingsGradle = Paths.get(outputDirectory.toString(), "settings.gradle");
      Files.createFile(settingsGradle);

      when(collection.getParameter("outputDirectory"))
            .thenReturn(createParameter("outputDirectory", outputDirectory.getAbsolutePath()));
      when(collection.getParameter("groupId")).thenReturn(createParameter("groupId", "groupIdValue"));
      when(collection.getParameter("artifactId")).thenReturn(createParameter("artifactId", "artifactIdValue"));

      GradleSettingsUtilities.addProject(collection);

      List<String> lines = Files.readAllLines(settingsGradle);

      assertEquals(3, lines.size());

      assertTrue(lines.contains(""));
      assertTrue(lines.contains("include 'groupIdValue.artifactIdValue'"));
      assertTrue(lines.contains("project(':groupIdValue.artifactIdValue').name = 'artifactIdValue'"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testAddDuplicateProject() throws IOException, FileUtilitiesException {
      IParameterCollection collection = mock(IParameterCollection.class);
      when(collection.containsParameter("outputDirectory")).thenReturn(true);
      when(collection.containsParameter("groupId")).thenReturn(true);
      when(collection.containsParameter("artifactId")).thenReturn(true);

      File outputDirectory = testFolder.newFolder("output");
      Path settingsGradle = Paths.get(outputDirectory.toString(), "settings.gradle");
      Files.createFile(settingsGradle);

      when(collection.getParameter("outputDirectory"))
            .thenReturn(createParameter("outputDirectory", outputDirectory.getAbsolutePath()));
      when(collection.getParameter("groupId")).thenReturn(createParameter("groupId", "groupIdValue"));
      when(collection.getParameter("artifactId")).thenReturn(createParameter("artifactId", "artifactIdValue"));

      GradleSettingsUtilities.addProject(collection);
      GradleSettingsUtilities.addProject(collection);

      List<String> lines = Files.readAllLines(settingsGradle);

      assertEquals(3, lines.size());

      assertTrue(lines.contains(""));
      assertTrue(lines.contains("include 'groupIdValue.artifactIdValue'"));
      assertTrue(lines.contains("project(':groupIdValue.artifactIdValue').name = 'artifactIdValue'"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDoesNotThrowExceptionIfFileNotFound() throws IOException, FileUtilitiesException {
      IParameterCollection collection = mock(IParameterCollection.class);
      when(collection.containsParameter("outputDirectory")).thenReturn(true);
      when(collection.containsParameter("groupId")).thenReturn(true);
      when(collection.containsParameter("artifactId")).thenReturn(true);

      File outputDirectory = testFolder.newFolder("output");

      when(collection.getParameter("outputDirectory"))
            .thenReturn(createParameter("outputDirectory", outputDirectory.getAbsolutePath()));
      when(collection.getParameter("groupId")).thenReturn(createParameter("groupId", "groupIdValue"));
      when(collection.getParameter("artifactId")).thenReturn(createParameter("artifactId", "artifactIdValue"));

      GradleSettingsUtilities.tryAddProject(collection);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void test_givenRelativePathOutputDirAndSettingsFileDoesntExist_whenTryAddProject_thenNoExceptionThrown()
         throws IOException, FileUtilitiesException {
      IParameterCollection collection = mock(IParameterCollection.class);
      when(collection.containsParameter("outputDirectory")).thenReturn(true);
      when(collection.containsParameter("groupId")).thenReturn(true);
      when(collection.containsParameter("artifactId")).thenReturn(true);

      File relativePathOutputDirectory = new File("tempRelativePath/");

      when(collection.getParameter("outputDirectory"))
            .thenReturn(createParameter("outputDirectory", relativePathOutputDirectory.getPath()));
      when(collection.getParameter("groupId")).thenReturn(createParameter("groupId", "groupIdValue"));
      when(collection.getParameter("artifactId")).thenReturn(createParameter("artifactId", "artifactIdValue"));

      GradleSettingsUtilities.tryAddProject(collection);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void test_givenAnOutputDirectoryWithNoParentOrSettingsFile_whenTryAddProject_thenNoExceptionThrown()
         throws Throwable {
      IParameterCollection collection = mock(IParameterCollection.class);
      when(collection.containsParameter("outputDirectory")).thenReturn(true);
      when(collection.containsParameter("groupId")).thenReturn(true);
      when(collection.containsParameter("artifactId")).thenReturn(true);

      File relativePathOutputDirectory = new File("/");

      when(collection.getParameter("outputDirectory"))
            .thenReturn(createParameter("outputDirectory", relativePathOutputDirectory.getPath()));
      when(collection.getParameter("groupId")).thenReturn(createParameter("groupId", "groupIdValue"));
      when(collection.getParameter("artifactId")).thenReturn(createParameter("artifactId", "artifactIdValue"));

      GradleSettingsUtilities.tryAddProject(collection);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void doesAddProjectToParent() throws IOException, FileUtilitiesException {

      File outputDirectory = testFolder.newFolder("output", "output2");
      Path settingsGradle = outputDirectory.toPath().getParent().resolve("settings.gradle");
      Files.createFile(settingsGradle);

      IParameterCollection collection = mock(IParameterCollection.class);
      when(collection.containsParameter("outputDirectory")).thenReturn(true);
      when(collection.containsParameter("groupId")).thenReturn(true);
      when(collection.containsParameter("artifactId")).thenReturn(true);
      when(collection.getParameter("outputDirectory"))
            .thenReturn(createParameter("outputDirectory", outputDirectory.getAbsolutePath()));
      when(collection.getParameter("groupId")).thenReturn(createParameter("groupId", "groupIdValue"));
      when(collection.getParameter("artifactId")).thenReturn(createParameter("artifactId", "artifactIdValue"));

      GradleSettingsUtilities.tryAddProject(collection);

      List<String> lines = Files.readAllLines(settingsGradle);

      assertEquals(3, lines.size());

      assertTrue(lines.contains(""));
      assertTrue(lines.stream().collect(Collectors.joining("\n")),
                 lines.contains("include 'output2/groupIdValue.artifactIdValue'"));
      assertTrue(lines.contains("project(':output2/groupIdValue.artifactIdValue').name = 'artifactIdValue'"));
   }

   @SuppressWarnings("rawtypes")
   private IParameter createParameter(String name, String value) {
      return new DefaultParameter<>(name, value);
   }

}
