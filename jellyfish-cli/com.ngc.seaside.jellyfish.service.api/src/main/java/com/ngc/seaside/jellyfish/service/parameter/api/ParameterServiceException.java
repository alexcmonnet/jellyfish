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
package com.ngc.seaside.jellyfish.service.parameter.api;

/**
 * Any exception in the IPromptUserService implementation.
 */
public class ParameterServiceException extends RuntimeException {

   /**
    * Default constructor with default exit code.
    */
   public ParameterServiceException() {
      super();
   }

   /**
    * Error exception constructor with a message.
    *
    * @param message the exception description
    */
   public ParameterServiceException(String message) {
      super(message);
   }

   /**
    * Error exception constructor with a message and cause.
    *
    * @param message the exception description
    */
   public ParameterServiceException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Error exception constructor with the cause.
    *
    * @param cause the exception
    */
   public ParameterServiceException(Throwable cause) {
      super(cause);
   }
}
