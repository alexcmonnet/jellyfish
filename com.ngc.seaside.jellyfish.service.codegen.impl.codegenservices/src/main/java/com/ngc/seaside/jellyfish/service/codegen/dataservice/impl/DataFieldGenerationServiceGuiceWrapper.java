package com.ngc.seaside.jellyfish.service.codegen.dataservice.impl;

import com.google.inject.Inject;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.codegen.api.IDataFieldGenerationService;
import com.ngc.seaside.jellyfish.service.codegen.api.IGeneratedJavaField;
import com.ngc.seaside.jellyfish.service.codegen.api.IGeneratedProtoField;
import com.ngc.seaside.systemdescriptor.model.api.data.IDataField;

public class DataFieldGenerationServiceGuiceWrapper implements IDataFieldGenerationService {

   private final DataFieldGenerationService delegate = new DataFieldGenerationService();
   
   @Inject
   public DataFieldGenerationServiceGuiceWrapper() {
   }
   
   @Override
   public IGeneratedJavaField getEventsField(IJellyFishCommandOptions options, IDataField field) {
      return delegate.getEventsField(options, field);
   }

   @Override
   public IGeneratedProtoField getMessagesField(IJellyFishCommandOptions options, IDataField field) {
      return delegate.getMessagesField(options, field);
   }

}
