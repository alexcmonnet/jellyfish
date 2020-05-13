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
package com.ngc.seaside.systemdescriptor.service.impl.gherkin.model;

import com.ngc.seaside.systemdescriptor.service.gherkin.model.api.IGherkinTag;

import gherkin.ast.Tag;

import java.nio.file.Path;

/**
 * Wraps a Gherkin tag.
 */
public class GherkinTag extends AbstractWrappedGherkin<Tag> implements IGherkinTag {

   private final String name;

   /**
    * Creates a new tag.
    *
    * @param wrapped the tag to wrap
    * @param path    the source file that contains the tag
    */
   public GherkinTag(Tag wrapped, Path path) {
      super(wrapped, path);
      // Skip the leading '@' character.
      this.name = wrapped.getName().substring(1);
   }

   @Override
   public String getName() {
      return name;
   }
}
