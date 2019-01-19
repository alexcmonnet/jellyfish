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
package com.ngc.seaside.jellyfish.service.codegen.api.proto;

import com.ngc.seaside.jellyfish.service.codegen.api.IGeneratedField;
import com.ngc.seaside.jellyfish.service.codegen.api.java.IGeneratedJavaField;

/**
 * Interface for generating fields for protocol buffers
 */
public interface IGeneratedProtoField extends IGeneratedField {

   /**
    * @return the name of the protocol buffers field
    */
   String getProtoFieldName();
   
   /**
    * @return the type of the protocol buffers field
    */
   String getProtoType();
   
   /**
    * @return the {@link IGeneratedJavaField} representing the generated java code from this protocol buffers field 
    */
   IGeneratedJavaProtoField getJavaField();

}
