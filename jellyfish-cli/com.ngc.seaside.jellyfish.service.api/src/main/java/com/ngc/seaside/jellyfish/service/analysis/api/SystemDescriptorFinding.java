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
package com.ngc.seaside.jellyfish.service.analysis.api;

import com.google.common.base.Preconditions;
import com.ngc.seaside.jellyfish.service.analysis.api.ISystemDescriptorFindingType.Severity;
import com.ngc.seaside.systemdescriptor.service.source.api.ISourceLocation;

import java.util.Optional;

/**
 * Documents a finding found within a System Descriptor project. A finding could represent an error or
 * warning found in the project, or it could simply document information about the System Descriptor project itself.
 */
public class SystemDescriptorFinding<T extends ISystemDescriptorFindingType> {

   private final T type;
   private final String message;
   private final ISourceLocation location;
   private final double complexity;

   /**
    * Constructs a System Descriptor finding.
    * 
    * @param type the type of finding
    * @param message the finding's message, formatted in markdown
    * @param location the source code location, may be null
    * @param complexity the complexity of the finding
    */
   SystemDescriptorFinding(T type, String message, ISourceLocation location, double complexity) {
      this.type = Preconditions.checkNotNull(type, "type must not be null!");
      this.message = Preconditions.checkNotNull(message, "message must not be null");
      this.location = location;
      Preconditions.checkArgument(complexity >= 0, "complexity cannot be negative");
      this.complexity = complexity;
   }

   /**
    * Returns the type of finding for categorizing this finding with other findings.
    * 
    * @return an identifier for categorizing this finding with other findings
    */
   public T getType() {
      return type;
   }

   /**
    * Returns a message detailing the finding, formatted in markdown. T
    * 
    * @return a message detailing the finding, formatted in markdown
    */
   public String getMessage() {
      return message;
   }

   /**
    * Returns the source code location of the finding, or {@link Optional#empty()} if the finding is not associated
    * with any particular source code.
    * 
    * @return the source code location of the finding
    */
   public Optional<ISourceLocation> getLocation() {
      return Optional.ofNullable(location);
   }

   /**
    * Returns a measure of the complexity required to fix this finding. The value of the complexity is discretionary,
    * but the values of different findings should be relatively proportional to their perceived complexity.
    * A complexity of {@code 0} represents no fix is necessary and should be returned when the
    * {@link ISystemDescriptorFindingType#getSeverity() severity} is {@link Severity#INFO}.
    * 
    * @return a measure of the complexity required to fix this finding
    */
   public double getComplexity() {
      return complexity;
   }

}
