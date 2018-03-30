package com.ngc.seaside.jellyfish.cli.command.createjavadistribution.dto;

import com.ngc.seaside.jellyfish.api.CommonParameters;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.IParameter;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.multicast.MulticastTransportProviderConfigDto;
import com.ngc.seaside.jellyfish.service.config.api.ITransportConfigurationService;
import com.ngc.seaside.jellyfish.service.config.api.TransportConfigurationType;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class TransportProviderDependenciesUtil {

   public static Set<String> getTransportProviderDependencies(IJellyFishCommandOptions options, 
            ITransportConfigurationService transportConfigService) {
      
      IParameter<?> parameter = options.getParameters().getParameter(CommonParameters.DEPLOYMENT_MODEL.getName());
      if (parameter == null) {
         return Collections.emptySet();
      }
      Optional<IModel> deploymentModel = options.getSystemDescriptor().findModel(parameter.getStringValue());
      if (!deploymentModel.isPresent()) {
         return Collections.emptySet();
      }
      Set<TransportConfigurationType> types = transportConfigService.getConfigurationTypes(options, deploymentModel.get());
      
      Set<String> dependencies = new LinkedHashSet<>();
      
      if (types.contains(TransportConfigurationType.MULTICAST)) {
         MulticastTransportProviderConfigDto multicastDto = new MulticastTransportProviderConfigDto(null);
         dependencies.addAll(multicastDto.getDependencies(true));
      }
      
      return dependencies;
   }
   
}