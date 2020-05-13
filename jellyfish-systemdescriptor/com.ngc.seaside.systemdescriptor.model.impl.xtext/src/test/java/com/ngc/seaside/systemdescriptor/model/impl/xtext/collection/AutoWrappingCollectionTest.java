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
package com.ngc.seaside.systemdescriptor.model.impl.xtext.collection;

import com.ngc.seaside.systemdescriptor.model.api.data.IDataField;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.AbstractWrappedXtextTest;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.data.WrappedPrimitiveDataField;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data;
import com.ngc.seaside.systemdescriptor.systemDescriptor.DataFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitiveDataFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitiveDataType;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutoWrappingCollectionTest extends AbstractWrappedXtextTest {

   private AutoWrappingCollection<DataFieldDeclaration, IDataField> wrapped;

   private Data data;

   @Before
   public void setup() throws Throwable {
      data = factory().createData();

      wrapped = new AutoWrappingCollection<>(
            data.getFields(),
            f -> new WrappedPrimitiveDataField(resolver(), (PrimitiveDataFieldDeclaration) f),
            AutoWrappingCollection.defaultUnwrapper());

      PrimitiveDataFieldDeclaration field = factory().createPrimitiveDataFieldDeclaration();
      field.setName("field1");
      field.setType(PrimitiveDataType.STRING);
      data.getFields().add(field);
   }

   @Test
   public void testDoesWrapXtextList() throws Throwable {
      assertEquals("size not correct!",
                   1,
                   wrapped.size());
      IDataField field = wrapped.iterator().next();

      assertFalse("isEmpty not correct!",
                  wrapped.isEmpty());
      assertTrue("contains not correct!",
                 wrapped.contains(field));

      Iterator<IDataField> i = wrapped.iterator();
      assertTrue("iterator.hasNext() not correct!",
                 i.hasNext());
      assertEquals("iterator.next() not correct!",
                   field.getName(),
                   i.next().getName());
      i.remove();
      assertFalse("iterator.remove() not correct!",
                  i.hasNext());

      assertTrue("did not return true if added!",
                 wrapped.add(field));
      assertFalse("add not correct!",
                  wrapped.isEmpty());
      assertTrue("did not return true if removed!",
                 wrapped.remove(field));
      assertTrue("remove not correct!",
                 wrapped.isEmpty());
   }
}
