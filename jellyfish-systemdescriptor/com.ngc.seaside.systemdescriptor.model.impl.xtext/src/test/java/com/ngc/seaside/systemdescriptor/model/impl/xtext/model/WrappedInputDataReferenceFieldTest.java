package com.ngc.seaside.systemdescriptor.model.impl.xtext.model;

import com.ngc.seaside.systemdescriptor.model.api.IPackage;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.FieldCardinality;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.AbstractWrappedXtextTest;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Cardinality;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Input;
import com.ngc.seaside.systemdescriptor.systemDescriptor.InputDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WrappedInputDataReferenceFieldTest extends AbstractWrappedXtextTest {

   private WrappedInputDataReferenceField wrapped;

   private InputDeclaration inputDeclaration;

   private Data dataType;

   @Mock
   private IModel parent;

   @Mock
   private IData wrappedDataType;

   @Before
   public void setup() throws Throwable {
      dataType = factory().createData();

      inputDeclaration = factory().createInputDeclaration();
      inputDeclaration.setName("input1");
      inputDeclaration.setCardinality(Cardinality.MANY);
      inputDeclaration.setType(dataType);

      Input input = factory().createInput();
      input.getDeclarations().add(inputDeclaration);

      Model model = factory().createModel();
      model.setInput(input);

      when(resolver().getWrapperFor(model)).thenReturn(parent);
      when(resolver().getWrapperFor(dataType)).thenReturn(wrappedDataType);
   }

   @Test
   public void testDoesWrapXtextObject() throws Throwable {
      wrapped = new WrappedInputDataReferenceField(resolver(), inputDeclaration);
      assertEquals("name not correct!",
                   wrapped.getName(),
                   inputDeclaration.getName());
      assertEquals("parent not correct!",
                   parent,
                   wrapped.getParent());
      assertEquals("cardinality not correct!",
                   FieldCardinality.MANY,
                   wrapped.getCardinality());
      assertEquals("type not correct!",
                   wrappedDataType,
                   wrapped.getType());
   }

   @Test
   public void testDoesUpdateXtextObject() throws Throwable {
      Data newXtextType = factory().createData();
      IData newType = mock(IData.class);
      IPackage p = mock(IPackage.class);
      when(newType.getName()).thenReturn("NewData");
      when(newType.getParent()).thenReturn(p);
      when(p.getName()).thenReturn("some.package");
      when(resolver().findXTextData(newType.getName(), p.getName())).thenReturn(Optional.of(newXtextType));

      wrapped = new WrappedInputDataReferenceField(resolver(), inputDeclaration);
      wrapped.setCardinality(FieldCardinality.SINGLE);
      assertEquals("cardinality not updated!",
                   Cardinality.DEFAULT,
                   inputDeclaration.getCardinality());

      wrapped.setType(newType);
      assertEquals("type not updated!",
                   newXtextType,
                   inputDeclaration.getType());
   }

   @Test(expected = IllegalStateException.class)
   public void testDoesNotAllowMissingType() throws Throwable {
      IData newType = mock(IData.class);
      IPackage p = mock(IPackage.class);
      when(newType.getName()).thenReturn("NewData");
      when(newType.getParent()).thenReturn(p);
      when(p.getName()).thenReturn("some.package");
      when(resolver().findXTextData(newType.getName(), p.getName())).thenReturn(Optional.empty());

      wrapped = new WrappedInputDataReferenceField(resolver(), inputDeclaration);
      wrapped.setType(newType);
   }
}