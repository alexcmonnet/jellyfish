/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2019, Northrop Grumman Systems Corporation
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Northrop Grumman Systems Corporation. The intellectual and technical concepts
 * contained herein are proprietary to Northrop Grumman Systems Corporation and
 * may be covered by U.S. and Foreign Patents or patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Northrop Grumman.
 */
package com.ngc.seaside.jellyfish.service.sequence.api;

import com.ngc.seaside.jellyfish.service.scenario.api.IMessagingFlow;
import com.ngc.seaside.jellyfish.service.scenario.api.IPublishSubscribeMessagingFlow;
import com.ngc.seaside.jellyfish.service.scenario.api.IRequestResponseMessagingFlow;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;

import java.util.Collection;
import java.util.Optional;

/**
 * A sequence flow wraps an {@link IMessagingFlow} and adds additional information about how that flow is actually
 * implemented.  A sequence flow that has no implementation is usually generated by the present of a scenario in a
 * model.  A flow that this is implemented is implemented by delegating to its parts or requirements to actually
 * perform the behavior.  In this case, the model which contains the flow is usually a system.
 *
 * <p/>
 * A single {@link ISequence sequence} may contain multiple flows.  Flows within the same sequence which contains the
 * same {@link #getSequenceNumber() sequence number} are considered to happen concurrently.
 */
public interface ISequenceFlow {

   /**
    * Gets the messaging flow that is being wrapped.
    *
    * @return the messaging flow that is being wrapped
    */
   IMessagingFlow getMessagingFlow();

   /**
    * Gets the sequence number of this flow.  This value can be used to order the flows of an {@code ISequence}.  Flows
    * with lesser values happen before flows with greater values.  Flows which have the same sequence number happen
    * concurrently within the containing sequence.  Note that sequence numbers of flows contained by different sequences
    * <i>cannot</i> be compared.
    *
    * @return the sequence number of this flow
    */
   int getSequenceNumber();

   /**
    * Gets the inputs to this flow.
    *
    * @return the inputs to this flow
    * @see IPublishSubscribeMessagingFlow#getInputs()
    * @see IRequestResponseMessagingFlow#getInput()
    */
   Collection<IDataReferenceField> getInputs();

   /**
    * Gets the outputs of this flow.
    *
    * @return the outputs of this flow
    * @see IPublishSubscribeMessagingFlow#getOutputs()
    * @see IRequestResponseMessagingFlow#getOutput()
    */
   Collection<IDataReferenceField> getOutputs();

   /**
    * Gets an optional which contains the implementation of this flow if this flow is implemented.
    *
    * @return an optional which contains the implementation of this flow or an empty optional if this flow is not
    * implemented
    */
   Optional<ISequenceFlowImplementation> getImplementation();
}
