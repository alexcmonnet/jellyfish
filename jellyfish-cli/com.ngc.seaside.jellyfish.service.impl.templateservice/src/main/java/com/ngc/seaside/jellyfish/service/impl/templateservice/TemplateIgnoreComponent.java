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
package com.ngc.seaside.jellyfish.service.impl.templateservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ngc.seaside.systemdescriptor.service.log.api.ILogService;

/**
 * Ignore the files in the templateContent that are listed in the templateContent's ignore file.
 * These files are still copied, they just aren't run through the Velocity engine which will
 * usually corrupt any binary files such as zips.
 */
public class TemplateIgnoreComponent {

   private static final String TEMPLATE_IGNORE_FILENAME = "template.ignore";

   private final Path templateFile;
   private final Path templateFolder;
   private final Map<Path, String> pathToIgnoreKey = new LinkedHashMap<>();
   private final ILogService logService;

   /**
    * Constructor
    *
    * @param templateDir    the directory of the templateContent.
    * @param templateFolder the templateContent directory within the templateContent.
    * @param logService     the log service.
    */
   public TemplateIgnoreComponent(Path templateDir, String templateFolder, ILogService logService) {
      this.templateFolder = templateDir.resolve(templateFolder);
      this.templateFile = templateDir.resolve(TEMPLATE_IGNORE_FILENAME);
      this.logService = logService;
   }

   /**
    * Parses the templateContent's ignore file, collecting the list of files that
    * velocity should ignore.
    *
    * @return This instance.
    */
   public TemplateIgnoreComponent parse() throws IOException {
      pathToIgnoreKey.clear();

      // Only parse the templateContent file if it exists
      if (templateFile.toFile().exists()) {
         for (String ignorePath : Files.readAllLines(templateFile)) {
            if (!ignorePath.trim().startsWith("#")) {
               final Path eachPath = templateFolder.resolve(
                        ignorePath.replace("[", "").replace("]", ""));
               pathToIgnoreKey.put(eachPath, ignorePath);
            }
         }
      } else {
         logService.trace(getClass(),
                          "Template ignore file (%s) does not exist, no files will be ignored.",
                          TEMPLATE_IGNORE_FILENAME);
      }

      return this;
   }

   /**
    * Get all of the keys that are to be ignored.
    *
    * @return list of keys to ignore.
    */
   public List<String> getAllKeys() {
      return new ArrayList<>(pathToIgnoreKey.values());
   }

   /**
    * Get the key used in order to create the given path.
    *
    * @param path the path
    * @return The key or null if the path doesn't exists.
    */
   public String getKey(Path path) {
      return pathToIgnoreKey.get(path);
   }

   /**
    * Checks if the specified path is to be ignored.
    *
    * @param path The path to check.
    * @return true if the path should be ignored, false otherwise.
    */
   public boolean contains(Path path) {
      return pathToIgnoreKey.containsKey(path);
   }

}
