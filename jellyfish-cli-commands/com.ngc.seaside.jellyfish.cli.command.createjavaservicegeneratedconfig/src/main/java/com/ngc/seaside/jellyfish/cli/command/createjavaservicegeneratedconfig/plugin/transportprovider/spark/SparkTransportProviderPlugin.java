package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.spark;

import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.CreateJavaServiceGeneratedConfigCommand;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.ConfigurationContext;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.ITransportProviderConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.ITransportTopicConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.rest.RestConfigurationDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

public class SparkTransportProviderPlugin
         implements ITransportProviderConfigurationPlugin<SparkTemplateDto> {

   private Set<ITransportTopicConfigurationPlugin<RestConfigurationDto>> plugins;

   @Inject
   public SparkTransportProviderPlugin(Set<ITransportTopicConfigurationPlugin<RestConfigurationDto>> plugins) {
      this.plugins = plugins;
   }

   @Override
   public Optional<SparkTemplateDto> getConfigurationDto(ConfigurationContext context) {
      SparkTemplateDto dto = new SparkTemplateDto(context);

      for (ITransportTopicConfigurationPlugin<RestConfigurationDto> plugin : plugins) {
         plugin.getTopicConfigurations(context).forEach(dto::addTopic);
      }

      if (dto.getTopics().isEmpty()) {
         return Optional.empty();
      }

      return Optional.of(dto);
   }

   @Override
   public String getTemplate(ConfigurationContext context) {
      return CreateJavaServiceGeneratedConfigCommand.class.getPackage().getName() + "-spark";
   }

   @Override
   public Set<String> getDependencies(ConfigurationContext context, DependencyType dependencyType) {
      switch (dependencyType) {
         case COMPILE:
            return Collections.singleton("com.ngc.seaside:service.transport.impl.topic.spark");
         case BUNDLE:
            return new LinkedHashSet<>(Arrays.asList(
                     "com.ngc.seaside:service.transport.impl.provider.spark",
                     "com.ngc.seaside:service.log.impl.common.sl4jlogservicebridge",
                     "com.sparkjava:spark-core"));
         case MODULE:
            return Collections.singleton("com.ngc.seaside:service.transport.impl.provider.spark.module");
         default:
            throw new AssertionError();
      }
   }

}
