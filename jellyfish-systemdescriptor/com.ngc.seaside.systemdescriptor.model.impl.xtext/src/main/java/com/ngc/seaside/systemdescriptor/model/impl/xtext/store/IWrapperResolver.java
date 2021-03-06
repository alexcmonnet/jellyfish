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
package com.ngc.seaside.systemdescriptor.model.impl.xtext.store;

import com.ngc.seaside.systemdescriptor.model.api.IPackage;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IEnumeration;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperties;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Enumeration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Properties;

import java.util.Optional;

/**
 * An {@code IWrapperResolver} is responsible for locating existing instances of wrappers for XText data.  It may also
 * be used to find wrapped XText data directly.
 */
public interface IWrapperResolver {

   /**
    * Finds the {@code IEnumeration} wrapper for the given XText enum.
    *
    * @return the wrapper for the data (never {@code null})
    * @throws IllegalStateException if no wrapper for the given enumeration could be found.
    */
   IEnumeration getWrapperFor(Enumeration enumeration);

   /**
    * Finds the {@code IData} wrapper for the given XText data.
    *
    * @return the wrapper for the data (never {@code null})
    * @throws IllegalStateException if no wrapper for the given data could be found.
    */
   IData getWrapperFor(Data data);

   /**
    * Finds the {@code IModel} wrapper for the given XText model.
    *
    * @return the wrapper for the model (never {@code null})
    * @throws IllegalStateException if no wrapper for the given model could be found.
    */
   IModel getWrapperFor(Model model);

   /**
    * Finds the {@code IPackage} wrapper for the given XText package.
    *
    * @return the wrapper for the package (never {@code null})
    * @throws IllegalStateException if no wrapper for the given package could be found.
    */
   IPackage getWrapperFor(Package systemDescriptorPackage);

   /**
    * Finds the {@code IProperties} wrapper for the given XText properties.
    *
    * @return the wrapper for the properties (never {@code null})
    * @throws IllegalStateException if no wrapper for the given properties could be found.
    */
   IProperties getWrapperFor(Properties properties);

   /**
    * Attempts to find the XText {@link Enumeration} with the given name and package.  If no enum is found, an empty
    * {@code Optional} is returned.
    */
   Optional<Enumeration> findXTextEnum(String name, String packageName);

   /**
    * Attempts to find the XText {@link Data} with the given name and package.  If no data is found, an empty {@code
    * Optional} is returned.
    */
   Optional<Data> findXTextData(String name, String packageName);

   /**
    * Attempts to find the XText {@link Model} with the given name and package.  If no model is found, an empty {@code
    * Optional} is returned.
    */
   Optional<Model> findXTextModel(String name, String packageName);
}

