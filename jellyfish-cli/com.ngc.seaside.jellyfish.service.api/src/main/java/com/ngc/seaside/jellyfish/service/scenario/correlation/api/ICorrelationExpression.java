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
package com.ngc.seaside.jellyfish.service.scenario.correlation.api;

import com.ngc.seaside.systemdescriptor.model.api.INamedChild;
import com.ngc.seaside.systemdescriptor.model.api.IPackage;
import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataPath;

/**
 * Represents a correlation expression as described in a System Descriptor scenario.  An expression is two operands and
 * an operator.  This interface represents both expressions that are part of a {@code when} step as well as expressions
 * that part of a {@code then} step.
 * <p/>
 * As an example, consider the following scenario:
 * <pre>
 *    scenario calculateTrackPriority {
 *      when receiving systemTrack
 *      and receiving impactAssessment
 *      and correlating systemTrack.header.correlationEventId to impactAssessment.header.correlationEventId
 *      then willCorrelate systemTrack.header.correlationEventId to trackPriority.header.correlationEventId
 *      and willPublish trackPriority
 * }
 * </pre>
 * In this case, there is one expression in a when step and and one expression in a then step.  The when step expression
 * is:
 * <pre>
 *    correlating systemTrack.header.correlationEventId to impactAssessment.header.correlationEventId
 * </pre>
 * The left hand operand is the data path {@code systemTrack.header.correlationEventId} and the right hand operand is
 * the data path {@code impactAssessment.header.correlationEventId}.  The operator is equals.
 * The then step expression is:
 * <pre>
 *    willCorrelate systemTrack.header.correlationEventId to trackPriority.header.correlationEventId
 * </pre>
 * The left hand operand is {@code systemTrack.header.correlationEventId} and the right hand operand is {@code
 * trackPriority.header.correlationEventId}.  Again, the operator is equals.
 */
public interface ICorrelationExpression {

   /**
    * Defines the operators that can be used form expressions.
    */
   enum Operator {
      /**
       * Indicates that the left and right hand operands must be equals.
       */
      EQUALS
   }

   /**
    * Gets the operator used by this expression.
    */
   Operator getOperator();

   /**
    * Gets the left hand operand used by this expression. If the correlation contained 1 input and 1 output,
    * then this returns the data path for the input.
    */
   IDataPath getLeftHandOperand();

   /**
    * Gets the right hand operand used by this expression. If the correlation contained 1 input and 1 output,
    * then this returns the data path for the output.
    */
   IDataPath getRightHandOperand();

   /**
    * Gets the data type of the correlation event ID as defined by this correlation.  If the value is {@link
    * DataTypes#DATA} or {@link DataTypes#ENUM}, {@link #getCorrelationEventIdReferenceType()} can be used to obtain the
    * details of the complex type.  Otherwise, the type of the correlation event ID is a primitive.
    *
    * @return the data type of the correlation event ID used by this correlation
    */
   DataTypes getCorrelationEventIdType();

   /**
    * Gets the complex type of the ID of the correlation event used by this correlation.  This value is only set if
    * {@link #getCorrelationEventIdType()} returns {@link DataTypes#DATA} or {@link DataTypes#ENUM}.
    *
    * @return the complex type of this correlation event's ID
    */
   INamedChild<IPackage> getCorrelationEventIdReferenceType();

}
