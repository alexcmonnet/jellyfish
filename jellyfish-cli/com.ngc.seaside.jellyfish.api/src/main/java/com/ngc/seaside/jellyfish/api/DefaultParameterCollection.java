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
package com.ngc.seaside.jellyfish.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of the {@link IParameterCollection} interface that is backed by a {@link
 * java.util.LinkedHashMap}
 */
public class DefaultParameterCollection implements IParameterCollection {

   private Map<String, IParameter<?>> parameterMap = new LinkedHashMap<>();

   public DefaultParameterCollection() {
   }

   public DefaultParameterCollection(IParameterCollection collection) {
      parameterMap.putAll(collection.getParameterMap());
   }

   @Override
   public boolean isEmpty() {
      return parameterMap.isEmpty();
   }

   @Override
   public boolean containsParameter(String parameterName) {
      return parameterMap.containsKey(parameterName);
   }

   @Override
   public IParameter<?> getParameter(String parameterName) {
      return parameterMap.get(parameterName);
   }

   @Override
   public List<IParameter<?>> getAllParameters() {
      return new ArrayList<>(parameterMap.values());
   }

   @Override
   public Map<String, IParameter<?>> getParameterMap() {
      return new LinkedHashMap<>(parameterMap);
   }

   /**
    * Add parameters to the collection.
    *
    * @param parameter the parameter to add.
    * @return this collection
    */
   public DefaultParameterCollection addParameter(IParameter<?> parameter) {
      parameterMap.put(parameter.getName(), parameter);
      return this;
   }

   /**
    * Add the given parameters to the collection in the order that they are in the list.
    *
    * @param parameters the parameters to add.
    * @return this collection
    */
   public DefaultParameterCollection addParameters(List<IParameter<?>> parameters) {
      parameters.forEach(this::addParameter);
      return this;
   }

   @Override
   public String toString() {
      return String.format("parameterMap: %s", parameterMap);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof IParameterCollection)) {
         return false;
      }
      IParameterCollection that = (IParameterCollection) obj;
      return Objects.equals(parameterMap, that.getParameterMap());
   }

   @Override
   public int hashCode() {
      return Objects.hash(parameterMap);
   }
}
