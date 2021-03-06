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
package com.ngc.seaside.systemdescriptor.model.impl.xtext.model.properties;

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyValues;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.exception.UnrecognizedXtextTypeException;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.store.IWrapperResolver;
import com.ngc.seaside.systemdescriptor.systemDescriptor.DataModel;
import com.ngc.seaside.systemdescriptor.systemDescriptor.EnumPropertyValue;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitivePropertyFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Properties;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PropertyFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PropertyValueAssignment;
import com.ngc.seaside.systemdescriptor.systemDescriptor.ReferencedPropertyFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage;

import java.util.Collections;

/**
 * A factory for creating different implementations of {@code IProperty}.
 */
public class WrapperPropertyFactory {

   private final IWrapperResolver resolver;

   /**
    * Creates a new factory.
    */
   public WrapperPropertyFactory(IWrapperResolver resolver) {
      this.resolver = resolver;
   }

   /**
    * Creates a new property from the given assignment.
    */
   public AbstractWrappedProperty<?> newProperty(PropertyValueAssignment assignment) {
      switch (assignment.getExpression().getDeclaration().eClass().getClassifierID()) {
         case SystemDescriptorPackage.PRIMITIVE_PROPERTY_FIELD_DECLARATION:
            return newPrimitiveProperty(assignment);
         case SystemDescriptorPackage.REFERENCED_PROPERTY_FIELD_DECLARATION:
            DataModel data = ((ReferencedPropertyFieldDeclaration) assignment.getExpression().getDeclaration())
                  .getDataModel();
            switch (data.eClass().getClassifierID()) {
               case SystemDescriptorPackage.ENUMERATION:
                  return newEnumProperty(assignment);
               case SystemDescriptorPackage.DATA:
                  return newDataProperty(assignment);
               default:
                  throw new UnrecognizedXtextTypeException(data);
            }
         default:
            throw new UnrecognizedXtextTypeException(assignment.getExpression().getDeclaration());
      }
   }

   /**
    * Creates a new property from the given declaration.
    */
   public AbstractWrappedProperty<?> newProperty(PropertyFieldDeclaration declaration) {
      switch (declaration.eClass().getClassifierID()) {
         case SystemDescriptorPackage.PRIMITIVE_PROPERTY_FIELD_DECLARATION:
            return newPrimitiveProperty((PrimitivePropertyFieldDeclaration) declaration);
         case SystemDescriptorPackage.REFERENCED_PROPERTY_FIELD_DECLARATION:
            ReferencedPropertyFieldDeclaration refDef = (ReferencedPropertyFieldDeclaration) declaration;
            DataModel data = refDef.getDataModel();
            switch (data.eClass().getClassifierID()) {
               case SystemDescriptorPackage.ENUMERATION:
                  return newEnumProperty(refDef);
               case SystemDescriptorPackage.DATA:
                  return newDataProperty(refDef);
               default:
                  throw new UnrecognizedXtextTypeException(data);
            }
         default:
            throw new UnrecognizedXtextTypeException(declaration);
      }
   }

   /**
    * Creates a new primitive property from an assignment.
    */
   public WrappedPrimitiveProperty newPrimitiveProperty(PropertyValueAssignment assignment) {
      Preconditions.checkNotNull(assignment, "assignment may not be null!");
      checkExpressionDeclarationIs(assignment, PrimitivePropertyFieldDeclaration.class);

      return new WrappedPrimitiveProperty(
            resolver,
            (PrimitivePropertyFieldDeclaration) assignment.getExpression().getDeclaration(),
            valuesOf(new WrappedPrimitivePropertyValue(resolver, assignment.getValue())));
   }

   /**
    * Creates a new primitive property from a declaration.
    */
   public WrappedPrimitiveProperty newPrimitiveProperty(PrimitivePropertyFieldDeclaration declaration) {
      return new WrappedPrimitiveProperty(resolver, declaration, IPropertyValues.emptyPropertyValues());
   }

   /**
    * Creates a new enum property from an assignment.
    */
   public WrappedEnumerationProperty newEnumProperty(PropertyValueAssignment assignment) {
      Preconditions.checkNotNull(assignment, "assignment may not be null!");
      checkExpressionDeclarationIs(assignment, ReferencedPropertyFieldDeclaration.class);

      return new WrappedEnumerationProperty(
            resolver,
            (ReferencedPropertyFieldDeclaration) assignment.getExpression().getDeclaration(),
            valuesOf(new WrappedEnumerationPropertyValue(resolver, (EnumPropertyValue) assignment.getValue())));
   }

   /**
    * Creates a new enum property from a declaration.
    */
   public WrappedEnumerationProperty newEnumProperty(ReferencedPropertyFieldDeclaration declaration) {
      return new WrappedEnumerationProperty(resolver, declaration, IPropertyValues.emptyPropertyValues());
   }

   /**
    * Creates a new data property from an assignment.
    */
   public WrappedDataProperty newDataProperty(PropertyValueAssignment assignment) {
      Preconditions.checkNotNull(assignment, "assignment may not be null!");
      checkExpressionDeclarationIs(assignment, ReferencedPropertyFieldDeclaration.class);
      ReferencedPropertyFieldDeclaration declaration =
            (ReferencedPropertyFieldDeclaration) assignment.getExpression().getDeclaration();
      return new WrappedDataProperty(
            resolver,
            declaration,
            valuesOf(new WrappedDataPropertyValue(resolver, declaration, (Properties) assignment.eContainer())));
   }

   /**
    * Creates a new data property from a declaration.
    */
   public WrappedDataProperty newDataProperty(ReferencedPropertyFieldDeclaration declaration) {
      return new WrappedDataProperty(resolver, declaration, IPropertyValues.emptyPropertyValues());
   }

   private static void checkExpressionDeclarationIs(PropertyValueAssignment assignment,
                                                    Class<?> clazz) {

      Class<?> actual = assignment.getExpression().getDeclaration().getClass();
      Preconditions.checkArgument(clazz.isAssignableFrom(actual),
                                  "declaration referenced in assignment must of type %s but found %s!",
                                  clazz.getName(),
                                  actual);
   }

   private static <T> IPropertyValues<T> valuesOf(T value) {
      return IPropertyValues.of(Collections.singleton(value));
   }
}
