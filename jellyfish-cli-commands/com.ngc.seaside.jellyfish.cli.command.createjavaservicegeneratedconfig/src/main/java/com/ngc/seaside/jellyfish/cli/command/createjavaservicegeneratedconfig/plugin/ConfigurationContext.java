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
package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin;

import com.ngc.seaside.jellyfish.api.CommonParameters;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.name.api.IProjectInformation;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.Optional;

/**
 * Context for plugins adding generated configuration.
 */
public class ConfigurationContext {

   private IJellyFishCommandOptions options;
   private IModel model;
   private IModel deploymentModel;
   private IProjectInformation projectInformation;
   private String basePackage;
   private ConfigurationType type;

   /**
    * Returns the jellyfish options.
    * 
    * @return the jellyfish options
    */
   public IJellyFishCommandOptions getOptions() {
      return options;
   }

   public ConfigurationContext setOptions(IJellyFishCommandOptions options) {
      this.options = options;
      return this;
   }

   /**
    * Returns the model to be configured.
    * 
    * @return the model to be configured
    */
   public IModel getModel() {
      return model;
   }

   public ConfigurationContext setModel(IModel model) {
      this.model = model;
      return this;
   }

   /**
    * Returns the deployment model, or {@link Optional#empty()} if there is none.
    * 
    * @return the deployment model
    */
   public Optional<IModel> getDeploymentModel() {
      return Optional.ofNullable(deploymentModel);
   }

   public ConfigurationContext setDeploymentModel(IModel deploymentModel) {
      this.deploymentModel = deploymentModel;
      return this;
   }

   /**
    * Returns the project information for the configuration.
    * 
    * @return the project information for the configuration
    */
   public IProjectInformation getProjectInformation() {
      return projectInformation;
   }

   public ConfigurationContext setProjectInformation(IProjectInformation projectInformation) {
      this.projectInformation = projectInformation;
      return this;
   }

   /**
    * Returns the base package for the configuration project.
    * 
    * @return the base package for the configuration project
    */
   public String getBasePackage() {
      return basePackage;
   }

   public ConfigurationContext setBasePackage(String basePackage) {
      this.basePackage = basePackage;
      return this;
   }

   /**
    * Returns the type of configuration being generated.
    * 
    * @return the type of configuration being generated
    */
   public ConfigurationType getConfigurationType() {
      return type;
   }

   public ConfigurationContext setConfigurationType(ConfigurationType type) {
      this.type = type;
      return this;
   }

   public boolean isSystemModel() {
      return CommonParameters.evaluateBooleanParameter(options.getParameters(), CommonParameters.SYSTEM.getName(),
               false);
   }
}
