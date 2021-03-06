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
package com.ngc.seaside.systemdescriptor.model.impl.xtext.data;

import com.google.common.base.Preconditions;

import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IDataField;
import com.ngc.seaside.systemdescriptor.model.api.data.IEnumeration;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.declaration.WrappedDeclarationDefinition;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.store.IWrapperResolver;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.util.ConversionUtil;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Enumeration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.ReferencedDataModelFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorFactory;
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage;

/**
 * Adapts a {@link WrappedReferencedEnumField} that references enumerations instance to an {@link
 * IDataField}.
 * This class is not threadsafe.
 */
public class WrappedReferencedEnumField extends AbstractWrappedDataField<ReferencedDataModelFieldDeclaration>
      implements IDataField {

   // Note the "implements IDataField" is redundant since the base class implements the interface as well.  But this
   // avoid an issue in ProxyingValidationContext when that class tries to create a dynamic proxy of this class.
   // It uses dataField.getClass().getInterfaces() to determine which interfaces the proxy should implement but
   // getInterfaces() only returns the interfaces declared by the class directly and not the interfaces of the sub-
   // class.

   /**
    * Creates a new enum field.
    */
   public WrappedReferencedEnumField(IWrapperResolver resolver, ReferencedDataModelFieldDeclaration wrapped) {
      super(resolver, wrapped);
      Preconditions.checkArgument(
            wrapped.getDataModel().eClass().getClassifierID() == SystemDescriptorPackage.ENUMERATION,
            "can only wrap a field with an enum type!");
   }

   @Override
   public DataTypes getType() {
      return DataTypes.ENUM; // Only enum types can be referenced.
   }

   @Override
   public IDataField setType(DataTypes type) {
      Preconditions.checkNotNull(type, "type may not be null!");
      Preconditions.checkArgument(type == DataTypes.ENUM,
                                  "the type of this field must be an enumeration, it cannot be changed to reference"
                                        + " primitives or data types!");
      // We don't actually have to do anything here.
      return this;
   }

   @Override
   public IData getReferencedDataType() {
      return null; // This type can only reference enumerations.
   }

   @Override
   public IDataField setReferencedDataType(IData dataType) {
      throw new IllegalStateException("the type of this field must be an enumeration, it cannot be changed to"
                                            + " reference other data types!");
   }

   @Override
   public IEnumeration getReferencedEnumeration() {
      // Cast is safe because we only allow construction of this type for wrapping enumerations.
      return resolver.getWrapperFor((Enumeration) wrapped.getDataModel());
   }

   @Override
   public IDataField setReferencedEnumeration(IEnumeration enumeration) {
      Preconditions.checkNotNull(enumeration, "enumeration may not be null!");
      wrapped.setDataModel(resolver.findXTextEnum(enumeration.getName(), enumeration.getParent().getName()).get());
      return this;
   }

   /**
    * Creates a new {@code ReferencedDataModelFieldDeclaration} that is equivalent
    * to the given data ref which references an enumeration. Changes to the {@code IReferencedDataField} are
    * not reflected in the returned {@code ReferencedDataModelFieldDeclaration}
    * after construction.
    */
   public static ReferencedDataModelFieldDeclaration toXtext(IWrapperResolver resolver,
                                                             IDataField dataRef) {
      Preconditions.checkNotNull(dataRef, "dataRef may not be null!");
      Preconditions.checkArgument(
            dataRef.getType() == DataTypes.ENUM,
            "cannot create a ReferencedDataModelFieldDeclaration for an IDataField that references a primitive type"
                  + " or a data type!");
      ReferencedDataModelFieldDeclaration x =
            SystemDescriptorFactory.eINSTANCE.createReferencedDataModelFieldDeclaration();
      x.setName(dataRef.getName());
      x.setDefinition(WrappedDeclarationDefinition.toXtext(resolver, dataRef.getMetadata(), null));
      x.setDataModel(resolver.findXTextEnum(dataRef.getReferencedEnumeration().getName(),
                                            dataRef.getReferencedEnumeration().getParent().getName()).get());
      x.setCardinality(ConversionUtil.convertCardinalityToXtext(dataRef.getCardinality()));
      return x;
   }
}
