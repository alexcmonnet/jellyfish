package com.ngc.seaside.jellyfish.cli.command.test.service;

import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.codegen.api.IJavaServiceGenerationService;
import com.ngc.seaside.jellyfish.service.codegen.api.dto.ClassDto;
import com.ngc.seaside.jellyfish.service.codegen.api.dto.EnumDto;
import com.ngc.seaside.jellyfish.service.name.api.IPackageNamingService;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.Set;
import java.util.TreeSet;

/**
 * A basic implementation of {@link IJavaServiceGenerationService} for tests
 */
public class MockedJavaServiceGenerationService implements IJavaServiceGenerationService {

   private final IPackageNamingService packageService;
   
   public MockedJavaServiceGenerationService() {
      this(new MockedPackageNamingService());
   }
   
   public MockedJavaServiceGenerationService(IPackageNamingService packageService) {
      this.packageService = packageService;
   }
   
   @Override
   public ClassDto getServiceInterfaceDescription(IJellyFishCommandOptions options, IModel model) {
      ClassDto dto = new ClassDto();
      dto.setPackageName(packageService.getServiceInterfacePackageName(options, model));
      dto.setName("I" + model.getName());
      return dto;
   }

   @Override
   public ClassDto getBaseServiceDescription(IJellyFishCommandOptions options, IModel model) {
      ClassDto dto = new ClassDto();
      dto.setPackageName(packageService.getServiceBaseImplementationPackageName(options, model));
      dto.setName("Abstract" + model.getName());
      return dto;
   }

   @Override
   public EnumDto getTransportTopicsDescription(IJellyFishCommandOptions options, IModel model) {
      EnumDto dto = new EnumDto();
      dto.setPackageName(packageService.getTransportTopicsPackageName(options, model));
      dto.setName(model.getName() + "TransportTopics");
      Set<String> topics = new TreeSet<>();
      model.getInputs().forEach(input -> topics.add(input.getType().getName().toUpperCase()));
      model.getOutputs().forEach(input -> topics.add(input.getType().getName().toUpperCase()));
      dto.setValues(topics);
      return dto;
   }

}