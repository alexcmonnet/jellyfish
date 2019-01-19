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
package com.ngc.seaside.jellyfish.service.config.impl.transportconfigurationservice;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.CommonParameters;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.IParameter;
import com.ngc.seaside.jellyfish.service.config.api.ITransportConfigurationService;
import com.ngc.seaside.jellyfish.service.config.api.TransportConfigurationType;
import com.ngc.seaside.jellyfish.service.config.api.dto.MulticastConfiguration;
import com.ngc.seaside.jellyfish.service.config.api.dto.RestConfiguration;
import com.ngc.seaside.jellyfish.service.config.api.dto.zeromq.ZeroMqConfiguration;
import com.ngc.seaside.jellyfish.service.config.impl.transportconfigurationservice.utils.MulticastConfigurationUtils;
import com.ngc.seaside.jellyfish.service.config.impl.transportconfigurationservice.utils.RestConfigurationUtils;
import com.ngc.seaside.jellyfish.service.config.impl.transportconfigurationservice.utils.TransportConfigurationServiceUtils;
import com.ngc.seaside.jellyfish.service.config.impl.transportconfigurationservice.utils.ZeroMqConfigurationUtils;
import com.ngc.seaside.jellyfish.service.scenario.api.IMessagingFlow;
import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.IReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.link.IModelLink;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IProperty;
import com.ngc.seaside.systemdescriptor.model.api.model.properties.IPropertyDataValue;
import com.ngc.seaside.systemdescriptor.service.api.ISystemDescriptorService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An implementation of {@code ITransportConfigurationService}.
 */
@Component(service = ITransportConfigurationService.class)
public class TransportConfigurationService implements ITransportConfigurationService {

   private static final Pattern[] PATTERNS = {Pattern.compile("([a-z\\d])([A-Z]+)"),
                                              Pattern.compile("([A-Z])([A-Z][a-z\\d])")};
   private static final String[] REPLACEMENTS = {"$1_$2", "$1_$2"};

   private ILogService logService;
   private ISystemDescriptorService sdService;

   @Override
   public String getTransportTopicName(IMessagingFlow flow, IDataReferenceField field) {
      String topic = field.getType().getName();

      for (int i = 0; i < PATTERNS.length; i++) {
         topic = PATTERNS[i].matcher(topic).replaceAll(REPLACEMENTS[i]);
      }

      return topic.toUpperCase();
   }

   @Override
   public Set<TransportConfigurationType> getConfigurationTypes(IJellyFishCommandOptions options, IModel model) {
      Set<TransportConfigurationType> types = new LinkedHashSet<>();
      IParameter<?> deploymentParam = options.getParameters().getParameter(CommonParameters.DEPLOYMENT_MODEL.getName());
      IModel deploymentModel = options.getSystemDescriptor().findModel(deploymentParam.getStringValue()).orElse(null);
      IModel aggregatedDeploymentModel = deploymentModel == null ? null : sdService.getAggregatedView(deploymentModel);
      if (aggregatedDeploymentModel != null) {
         for (IModelLink<? extends IReferenceField> link : aggregatedDeploymentModel.getLinks()) {
            for (IProperty property : link.getProperties()) {
               if (property.getType() == DataTypes.DATA) {
                  IData type = property.getReferencedDataType();
                  if (MulticastConfigurationUtils.isMulticastConfiguration(type)) {
                     types.add(TransportConfigurationType.MULTICAST);
                  } else if (RestConfigurationUtils.isRestConfiguration(type)) {
                     types.add(TransportConfigurationType.REST);
                  } else if (ZeroMqConfigurationUtils.isZeroMqConfiguration(type)) {
                     types.add(TransportConfigurationType.ZERO_MQ);
                  }
               }
            }
         }
      }
      return types;
   }

   @Override
   public Collection<MulticastConfiguration> getMulticastConfiguration(IJellyFishCommandOptions options,
                                                                       IDataReferenceField field) {
      return getLinkConfigurations(options,
                               field,
                               MulticastConfigurationUtils.MULTICAST_CONFIGURATION_QUALIFIED_NAME,
                               MulticastConfigurationUtils::getMulticastConfiguration);
   }

   @Override
   public Collection<RestConfiguration> getRestConfiguration(IJellyFishCommandOptions options,
                                                             IDataReferenceField field) {
      return getLinkConfigurations(options,
                               field,
                               RestConfigurationUtils.REST_CONFIGURATION_QUALIFIED_NAME,
                               RestConfigurationUtils::getRestConfiguration);
   }

   @Override
   public Collection<ZeroMqConfiguration> getZeroMqConfiguration(IJellyFishCommandOptions options,
                                                                 IDataReferenceField field) {
      List<ZeroMqConfiguration> configurations = new ArrayList<>();
      configurations.addAll(getLinkConfigurations(options,
                                              field,
                                              ZeroMqConfigurationUtils.ZERO_MQ_TCP_CONFIGURATION_QUALIFIED_NAME,
                                              ZeroMqConfigurationUtils::getZeroMqTcpConfiguration));
      configurations.addAll(getLinkConfigurations(options,
                                              field,
                                              ZeroMqConfigurationUtils.ZERO_MQ_IPC_CONFIGURATION_QUALIFIED_NAME,
                                              ZeroMqConfigurationUtils::getZeroMqIpcConfiguration));
      configurations.addAll(getLinkConfigurations(options,
                                              field,
                                              ZeroMqConfigurationUtils.ZERO_MQ_PGM_CONFIGURATION_QUALIFIED_NAME,
                                              ZeroMqConfigurationUtils::getZeroMqPgmConfiguration));
      configurations.addAll(getLinkConfigurations(options,
                                              field,
                                              ZeroMqConfigurationUtils.ZERO_MQ_EPGM_CONFIGURATION_QUALIFIED_NAME,
                                              ZeroMqConfigurationUtils::getZeroMqEpgmConfiguration));
      configurations.addAll(getLinkConfigurations(options,
                                              field,
                                              ZeroMqConfigurationUtils.ZERO_MQ_INPROC_CONFIGURATION_QUALIFIED_NAME,
                                              ZeroMqConfigurationUtils::getZeroMqInprocConfiguration));
      return configurations;
   }

   /**
    * Returns the collection of configurations for the given field.
    *
    * @param options             jellyfish options
    * @param field               field
    * @param configQualifiedName fully qualified name of configuration sd data type
    * @param function            function to convert {@link IPropertyDataValue} to the configuration type
    * @return the collection of configurations for the given field
    */
   private <T> Collection<T> getLinkConfigurations(IJellyFishCommandOptions options,
                                               IDataReferenceField field, String configQualifiedName,
                                               Function<IPropertyDataValue, T> function) {
      IModel deploymentModel = sdService.getAggregatedView(
            TransportConfigurationServiceUtils.getDeploymentModel(options));
      Collection<IModelLink<?>> links = TransportConfigurationServiceUtils.findLinks(deploymentModel, field);
      Collection<T> configurations = new LinkedHashSet<>();
      for (IModelLink<?> link : links) {
         configurations.addAll(TransportConfigurationServiceUtils.getConfigurations(link::getProperties, 
               configQualifiedName,
               function,
               () -> String.format("Configuration is not completely set for link %s%s -> %s",
                     link.getName().orElse("") + " ",
                     link.getSource().getName(),
                     link.getTarget().getName())));
      }
      return configurations;
   }

   @Activate
   public void activate() {
      logService.debug(getClass(), "activated");
   }

   @Deactivate
   public void deactivate() {
      logService.debug(getClass(), "deactivated");
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeLogService")
   public void setLogService(ILogService ref) {
      this.logService = ref;
   }

   public void removeLogService(ILogService ref) {
      setLogService(null);
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeSystemDescriptorService")
   public void setSystemDescriptorService(ISystemDescriptorService ref) {
      this.sdService = ref;
   }

   public void removeSystemDescriptorService(ISystemDescriptorService ref) {
      setSystemDescriptorService(null);
   }

}
