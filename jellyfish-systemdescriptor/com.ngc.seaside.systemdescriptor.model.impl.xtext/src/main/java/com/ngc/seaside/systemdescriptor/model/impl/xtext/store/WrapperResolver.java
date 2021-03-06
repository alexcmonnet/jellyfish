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

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.IPackage;
import com.ngc.seaside.systemdescriptor.model.api.ISystemDescriptor;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IEnumeration;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperties;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Element;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Enumeration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Properties;
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Simple implementation of {@code IWrapperResolver}.  It largley delegates to the containing {@code ISystemDescriptor}
 * to find wrappers.  It traverses the resource set of the root XText object as returned by XText find XText data.
 */
public class WrapperResolver implements IWrapperResolver {

   private final ISystemDescriptor systemDescriptor;
   private final EObject rootXtextObject;

   public WrapperResolver(ISystemDescriptor systemDescriptor,
                          EObject rootXtextObject) {
      this.systemDescriptor = Preconditions.checkNotNull(systemDescriptor, "systemDescriptor may not be null!");
      this.rootXtextObject = Preconditions.checkNotNull(rootXtextObject, "rootXtextObject may not be null!");
   }

   @Override
   public IEnumeration getWrapperFor(Enumeration enumeration) {
      return systemDescriptor.findEnumeration(((Package) enumeration.eContainer()).getName(), enumeration.getName())
            .orElseThrow(() -> new IllegalStateException("could not find IEnumeration wrapper for " + enumeration));
   }

   @Override
   public IData getWrapperFor(Data data) {
      Preconditions.checkNotNull(data, "data may not be null!");
      return systemDescriptor.findData(((Package) data.eContainer()).getName(), data.getName())
            .orElseThrow(() -> new IllegalStateException("could not find IData wrapper for " + data));
   }

   @Override
   public IModel getWrapperFor(Model model) {
      Preconditions.checkNotNull(model, "model may not be null!");
      return systemDescriptor.findModel(((Package) model.eContainer()).getName(), model.getName())
            .orElseThrow(() -> new IllegalStateException("could not find IModel wrapper for " + model));
   }

   @Override
   public IPackage getWrapperFor(Package systemDescriptorPackage) {
      Preconditions.checkNotNull(systemDescriptorPackage, "systemDescriptorPackage may not be null!");
      return systemDescriptor.getPackages().getByName(systemDescriptorPackage.getName())
            .orElseThrow(() -> new IllegalStateException("could not find IPackage wrapper for "
                                                               + systemDescriptorPackage));
   }

   @Override
   public IProperties getWrapperFor(Properties properties) {
      Preconditions.checkNotNull(properties, "properties may not be null!");
      EObject parent = properties.eContainer();
      if (parent instanceof Model) {
         return getWrapperFor((Model) parent).getProperties();
      }
      throw new IllegalStateException("Cannot find IProperties wrapper for properties " + properties);
   }

   @Override
   public Optional<Enumeration> findXTextEnum(String name, String packageName) {
      Preconditions.checkNotNull(name, "name may not be null!");
      Preconditions.checkArgument(!name.trim().isEmpty(), "name may not be empty!");
      Preconditions.checkNotNull(packageName, "packageName may not be null!");
      Preconditions.checkArgument(!packageName.trim().isEmpty(), "packageName may not be empty!");

      for (Package p : findXTextPackages(packageName)) {
         Element element = p.getElement();
         if (element.eClass().getClassifierID() == SystemDescriptorPackage.ENUMERATION
               && element.getName().equals(name)) {
            return Optional.of((Enumeration) element);
         }
      }

      return Optional.empty();
   }

   @Override
   public Optional<Data> findXTextData(String name, String packageName) {
      Preconditions.checkNotNull(name, "name may not be null!");
      Preconditions.checkArgument(!name.trim().isEmpty(), "name may not be empty!");
      Preconditions.checkNotNull(packageName, "packageName may not be null!");
      Preconditions.checkArgument(!packageName.trim().isEmpty(), "packageName may not be empty!");

      for (Package p : findXTextPackages(packageName)) {
         Element element = p.getElement();
         if (element.eClass().getClassifierID() == SystemDescriptorPackage.DATA
               && element.getName().equals(name)) {
            return Optional.of((Data) element);
         }
      }

      return Optional.empty();
   }

   @Override
   public Optional<Model> findXTextModel(String name, String packageName) {
      Preconditions.checkNotNull(name, "name may not be null!");
      Preconditions.checkArgument(!name.trim().isEmpty(), "name may not be empty!");
      Preconditions.checkNotNull(packageName, "packageName may not be null!");
      Preconditions.checkArgument(!packageName.trim().isEmpty(), "packageName may not be empty!");

      for (Package p : findXTextPackages(packageName)) {
         Element element = p.getElement();
         if (element.eClass().getClassifierID() == SystemDescriptorPackage.MODEL
               && element.getName().equals(name)) {
            return Optional.of((Model) element);
         }
      }

      return Optional.empty();
   }

   /**
    * Finds the XTest packages with the given name.  There may be more than one package with the given name.
    */
   private Collection<Package> findXTextPackages(String name) {
      Collection<Package> packages = new ArrayList<>();

      Resource resource = rootXtextObject.eResource();
      if (resource != null) {
         ResourceSet set = resource.getResourceSet();
         for (Resource r : set.getResources()) {
            for (EObject o : r.getContents()) {
               if (o instanceof Package) {
                  Package p = (Package) o;
                  if (p.getName().equals(name)) {
                     packages.add(p);
                  }
               }
            }
         }
      }

      return packages;
   }

}
