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
package com.ngc.seaside.systemdescriptor.service.impl.xtext.source.chained.gherkin;

import com.ngc.seaside.systemdescriptor.model.api.INamedChildCollection;
import com.ngc.seaside.systemdescriptor.service.gherkin.model.api.IFeature;
import com.ngc.seaside.systemdescriptor.service.gherkin.model.api.IGherkinScenario;
import com.ngc.seaside.systemdescriptor.service.gherkin.model.api.IGherkinTag;
import com.ngc.seaside.systemdescriptor.service.impl.gherkin.model.GherkinFeature;
import com.ngc.seaside.systemdescriptor.service.impl.gherkin.model.GherkinScenario;
import com.ngc.seaside.systemdescriptor.service.impl.gherkin.model.GherkinTag;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.source.DetailedSourceLocation;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.source.chained.ChainedMethodCallContext;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.source.chained.common.CollectionChainedMethodCall;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.source.chained.common.NamedChildCollectionChainedMethodCall;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.source.chained.common.StringChainedMethodCall;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.source.location.IDetailedSourceLocation;
import com.ngc.seaside.systemdescriptor.service.source.api.IChainedMethodCall;

import gherkin.ast.Feature;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureChainedMethodCall extends AbstractGherkinChainedMethodCall<GherkinFeature, Feature> {

   private static final Pattern SHORT_DESCRIPTION_PATTERN = Pattern.compile("^\\s*Feature:\\s*(?<name>.*?)\\s*$");

   /**
    * @param feature feature
    * @param context context
    */
   public FeatureChainedMethodCall(GherkinFeature feature, ChainedMethodCallContext context) {
      super(feature, context);
      try {
         register(IFeature.class.getMethod("getShortDescription"), this::thenGetShortDescription);
         register(IFeature.class.getMethod("getDescription"), this::thenGetDescription);
         register(IFeature.class.getMethod("getTags"), this::thenGetTags);
         register(IFeature.class.getMethod("getScenarios"), this::thenGetScenarios);
      } catch (NoSuchMethodException e) {
         throw new AssertionError(e);
      }
   }

   private IChainedMethodCall<String> thenGetShortDescription() {
      String line = getLine(element.getLineNumber());
      Matcher m = SHORT_DESCRIPTION_PATTERN.matcher(line);
      if (m.matches()) {
         String description = m.group("name");
         int column = m.start("name") + 1;
         IDetailedSourceLocation location = DetailedSourceLocation.of(element.getPath(), element.getLineNumber(),
                  column, description.length());
         return new StringChainedMethodCall(location, context);
      }
      throw new IllegalStateException(
               "Cannot find feature on line " + element.getLineNumber() + " in " + element.getPath());
   }

   private IChainedMethodCall<String> thenGetDescription() {
      INamedChildCollection<IFeature, IGherkinScenario> scenarios = element.getScenarios();
      int endLineNumber = Integer.MAX_VALUE;
      if (!scenarios.isEmpty()) {
         GherkinScenario scenario = (GherkinScenario) scenarios.iterator().next();
         if (scenario.getBackground().isPresent()) {
            scenario = (GherkinScenario) scenario.getBackground().get();
         }
         endLineNumber = scenario.getTags().stream().map(GherkinTag.class::cast).mapToInt(GherkinTag::getLineNumber)
                  .min().orElse(scenario.getLineNumber());
      }
      int startLineNumber = element.getLineNumber() + 1;
      List<String> lines = getLines(startLineNumber, endLineNumber - 1);
      for (ListIterator<String> itr = lines.listIterator(); itr.hasNext();) {
         if (itr.next().trim().isEmpty()) {
            startLineNumber++;
            itr.remove();
         } else {
            break;
         }
      }
      for (ListIterator<String> itr = lines.listIterator(lines.size()); itr.hasPrevious();) {
         if (itr.previous().trim().isEmpty()) {
            itr.remove();
         } else {
            break;
         }
      }
      if (lines.isEmpty()) {
         throw new IllegalStateException("Feature description is empty for " + element.getPath());
      }
      DetailedSourceLocation location =
               DetailedSourceLocation.of(element.getPath(), startLineNumber, 1, element.getDescription().length());
      return new StringChainedMethodCall(location, context);
   }

   private IChainedMethodCall<Collection<IGherkinTag>> thenGetTags() {
      List<Entry<IGherkinTag, IChainedMethodCall<IGherkinTag>>> list = new ArrayList<>();

      for (IGherkinTag tag : element.getTags()) {
         IChainedMethodCall<IGherkinTag> methodCall = context.getChainedMethodCallForElement(tag);
         list.add(new SimpleImmutableEntry<>(tag, methodCall));
      }

      IDetailedSourceLocation location = list.stream().map(Entry::getValue).map(IChainedMethodCall::getLocation)
               .map(DetailedSourceLocation.Builder::new).map(DetailedSourceLocation.Builder::build)
               .reduce(DetailedSourceLocation::merge)
               .orElseThrow(() -> new IllegalStateException("Feature does not have tags"));
      return new CollectionChainedMethodCall<>(location, element.getTags(), list, context);
   }

   private IChainedMethodCall<INamedChildCollection<IFeature, IGherkinScenario>> thenGetScenarios() {
      DetailedSourceLocation location = element.getScenarios().stream().map(context::getChainedMethodCallForElement)
               .map(IChainedMethodCall::getLocation).map(DetailedSourceLocation.Builder::new)
               .map(DetailedSourceLocation.Builder::build).reduce(DetailedSourceLocation::merge)
               .orElseThrow(() -> new IllegalStateException("Feature does not have any scenarios"));
      return new NamedChildCollectionChainedMethodCall<>(location, element.getScenarios(),
               context::getChainedMethodCallForElement, context);
   }
}
