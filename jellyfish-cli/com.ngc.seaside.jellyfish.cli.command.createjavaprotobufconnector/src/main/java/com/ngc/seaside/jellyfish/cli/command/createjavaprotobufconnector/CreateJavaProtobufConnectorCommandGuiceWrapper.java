package com.ngc.seaside.jellyfish.cli.command.createjavaprotobufconnector;

import com.google.inject.Inject;
import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService;
import com.ngc.seaside.jellyfish.service.template.api.ITemplateService;
import com.ngc.seaside.jellyfish.api.IUsage;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.JellyFishCommandConfiguration;
import com.ngc.seaside.jellyfish.service.codegen.api.IDataFieldGenerationService;
import com.ngc.seaside.jellyfish.service.codegen.api.IJavaServiceGenerationService;
import com.ngc.seaside.jellyfish.service.config.api.ITransportConfigurationService;
import com.ngc.seaside.jellyfish.service.name.api.IPackageNamingService;
import com.ngc.seaside.jellyfish.service.name.api.IProjectNamingService;
import com.ngc.seaside.jellyfish.service.requirements.api.IRequirementsService;
import com.ngc.seaside.jellyfish.service.scenario.api.IScenarioService;

@JellyFishCommandConfiguration(autoTemplateProcessing = false)
public class CreateJavaProtobufConnectorCommandGuiceWrapper implements IJellyFishCommand {

   private final CreateJavaProtobufConnectorCommand delegate = new CreateJavaProtobufConnectorCommand();

   @Inject
   public CreateJavaProtobufConnectorCommandGuiceWrapper(ILogService logService,
                                                         ITemplateService templateService,
                                                         IScenarioService scenarioService,
                                                         ITransportConfigurationService transportConfigService,
                                                         IRequirementsService requirementsService,
                                                         IPackageNamingService packageService,
                                                         IProjectNamingService projectService,
                                                         IJavaServiceGenerationService generationService,
                                                         IDataFieldGenerationService dataFieldService,
                                                         IBuildManagementService buildManagementService) {
      delegate.setLogService(logService);
      delegate.setTemplateService(templateService);
      delegate.setScenarioService(scenarioService);
      delegate.setTransportConfigurationService(transportConfigService);
      delegate.setRequirementsService(requirementsService);
      delegate.setPackageNamingService(packageService);
      delegate.setProjectNamingService(projectService);
      delegate.setJavaServiceGenerationService(generationService);
      delegate.setDataFieldGenerationService(dataFieldService);
      delegate.setBuildManagementService(buildManagementService);
      delegate.activate();
   }

   @Override
   public String getName() {
      return delegate.getName();
   }

   @Override
   public IUsage getUsage() {
      return delegate.getUsage();
   }

   @Override
   public void run(IJellyFishCommandOptions commandOptions) {
      delegate.run(commandOptions);
   }

}