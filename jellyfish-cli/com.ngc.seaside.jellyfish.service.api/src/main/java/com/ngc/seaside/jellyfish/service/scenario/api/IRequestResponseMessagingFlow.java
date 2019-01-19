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
package com.ngc.seaside.jellyfish.service.scenario.api;

import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IModelReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenario;

import java.util.Optional;

/**
 * A type of flow that is used as part of the {@link MessagingParadigm#REQUEST_RESPONSE request/response} messaging
 * paradigm.  A request/response flow comes in two forms: the client side flow that initiations a request and receives
 * the response and the server side flow that receives a request and responds.
 */
public interface IRequestResponseMessagingFlow extends IMessagingFlow {

   /**
    * Defines the different kinds of request/response messaging flows.
    */
   enum FlowType {
      /**
       * A client flow indicates that this flow describes a client sending a request to a server and waiting for the
       * response from the server.  This flow is from the perspective of the client.  The output of these types of
       * flows is the request to the server and the input is the response from the server.
       */
      CLIENT,
      /**
       * A server flow indicates that this flow describes a server receiving a request and responding to it.  This flow
       * is from the perspective of the server.  The input of these types of flows is the request to the server from the
       * client and the output of these types of flows is the response from the server to the client.
       */
      SERVER
   }

   /**
    * Gets the input fields of this flow.  These are fields that are declared in the model that contains the
    * scenario that is associated with this flow.
    *
    * @return the input fields of this flow
    */
   IDataReferenceField getInput();

   /**
    * Gets the output fields of this flow.  These are fields that are declared in the model that contains the
    * scenario that is associated with this flow.
    *
    * @return the output fields of this flow
    */
   IDataReferenceField getOutput();

   /**
    * Gets the type of request/response flow this flow is.
    *
    * @return the type of request/response flow this flow is
    */
   FlowType getFlowType();

   /**
    * Gets the part declaration that points to the model of the server side component that is being invoked by a client.
    * The {@link IModelReferenceField} is a field that is declared in the model for the {@link #getScenario() scenario}
    * that is associated with this flow. This value is only set if this is a {@link FlowType#CLIENT client-side} flow.
    *
    * @return an optional that contains the model of the invoked server side component if this flow is a client side
    * flow; otherwise the optional is empty.
    */
   Optional<IModelReferenceField> getInvokedServerSideComponent();

   /**
    * Gets the scenario that invoked on the server component.  This is not a scenario that is declared in the {@link
    * #getScenario() scenario that is assoicated with this flow}, but the scenario that is associated with the
    * server-side flow.  This value is only set if this is a {@link FlowType#CLIENT client-side} flow.
    *
    * @return an optional that contains the invoked scenario on server side if this flow is a client side flow;
    * otherwise the optional is empty.
    */
   Optional<IScenario> getInvokedServerSideScenario();

   @Override
   default MessagingParadigm getMessagingParadigm() {
      return MessagingParadigm.REQUEST_RESPONSE;
   }
}
