package com.ngc.seaside.jellyfish.cli.command.createjavacucumbertests;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.CommandException;
import com.ngc.seaside.jellyfish.api.CommonParameters;
import com.ngc.seaside.jellyfish.api.DefaultParameter;
import com.ngc.seaside.jellyfish.api.DefaultParameterCollection;
import com.ngc.seaside.jellyfish.api.DefaultUsage;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.IUsage;
import com.ngc.seaside.jellyfish.cli.command.createjavacucumbertests.dto.CucumberDto;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService;
import com.ngc.seaside.jellyfish.service.codegen.api.IJavaServiceGenerationService;
import com.ngc.seaside.jellyfish.service.name.api.IPackageNamingService;
import com.ngc.seaside.jellyfish.service.name.api.IProjectInformation;
import com.ngc.seaside.jellyfish.service.name.api.IProjectNamingService;
import com.ngc.seaside.jellyfish.service.template.api.ITemplateService;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;

@Component(service = IJellyFishCommand.class)
public class CreateJavaCucumberTestsCommand implements IJellyFishCommand {

   private static final String NAME = "create-java-cucumber-tests";
   private static final IUsage USAGE = createUsage();

   public static final String OUTPUT_DIRECTORY_PROPERTY = CommonParameters.OUTPUT_DIRECTORY.getName();
   public static final String MODEL_PROPERTY = CommonParameters.MODEL.getName();
   public static final String CLEAN_PROPERTY = CommonParameters.CLEAN.getName();
   public static final String DEPLOYMENT_MODEL = CommonParameters.DEPLOYMENT_MODEL.getName();
   public static final String REFRESH_FEATURE_FILES_PROPERTY = "refreshFeatureFiles";
   public static final String BUILD_TEMPLATE_SUFFIX = "build";
   public static final String CONFIG_TEMPLATE_SUFFIX = "config";

   public static final String MODEL_OBJECT_PROPERTY = "modelObject";

   private ILogService logService;
   private ITemplateService templateService;
   private IProjectNamingService projectNamingService;
   private IPackageNamingService packageNamingService;
   private IJavaServiceGenerationService generationService;
   private IBuildManagementService buildManagementService;

   @Override
   public void run(IJellyFishCommandOptions commandOptions) {

      DefaultParameterCollection parameters = new DefaultParameterCollection();
      parameters.addParameters(commandOptions.getParameters().getAllParameters());

      String modelId = parameters.getParameter(MODEL_PROPERTY).getStringValue();
      final IModel model = commandOptions.getSystemDescriptor()
            .findModel(modelId)
            .orElseThrow(() -> new CommandException("Unknown model:" + modelId));
      parameters.addParameter(new DefaultParameter<>(MODEL_OBJECT_PROPERTY, model));

      final Path outputDirectory = Paths.get(parameters.getParameter(OUTPUT_DIRECTORY_PROPERTY).getStringValue());
      doCreateDirectories(outputDirectory);

      IProjectInformation info = projectNamingService.getCucumberTestsProjectName(commandOptions, model);

      final String packageName = packageNamingService.getCucumberTestsPackageName(commandOptions, model);
      final String projectName = info.getDirectoryName();
      final boolean clean = CommonParameters.evaluateBooleanParameter(commandOptions.getParameters(), CLEAN_PROPERTY);

      boolean isConfigGenerated = parameters.getParameter(CommonParameters.DEPLOYMENT_MODEL.getName()) != null
                                  && parameters.getParameter(CommonParameters.DEPLOYMENT_MODEL.getName()).getValue()
                                  != null;

      CucumberDto dto = new CucumberDto(buildManagementService, commandOptions)
            .setProjectName(projectName)
            .setPackageName(packageName)
            .setClassName(model.getName())
            .setTransportTopicsClass(
                  generationService.getTransportTopicsDescription(commandOptions, model).getFullyQualifiedName())
            .setConfigGenerated(isConfigGenerated)
            .setDependencies(new LinkedHashSet<>(Arrays.asList(
                  projectNamingService.getMessageProjectName(commandOptions, model)
                        .getArtifactId(),
                  projectNamingService.getBaseServiceProjectName(commandOptions, model)
                        .getArtifactId())));
      String configModule = packageNamingService.getCucumberTestsConfigPackageName(commandOptions, model) + "."
                            + model.getName() + "TestConfigurationModule";
      dto.setConfigModule(configModule);
      parameters.addParameter(new DefaultParameter<>("dto", dto));

      if (isConfigGenerated) {
         String pkg = packageNamingService.getCucumberTestsConfigPackageName(commandOptions, model);
         dto.setConfigPackageName(pkg);
         dto.getDependencies()
               .add(projectNamingService.getCucumberTestsConfigProjectName(commandOptions, model).getArtifactId());
      } else {
         dto.setConfigPackageName(dto.getPackageName() + ".config");
      }

      templateService.unpack(CreateJavaCucumberTestsCommand.class.getPackage().getName() + "-" + BUILD_TEMPLATE_SUFFIX,
                             parameters,
                             outputDirectory,
                             clean);
      if (!isConfigGenerated) {
         templateService.unpack(
               CreateJavaCucumberTestsCommand.class.getPackage().getName() + "-" + CONFIG_TEMPLATE_SUFFIX,
               parameters,
               outputDirectory,
               false);
      }
      buildManagementService.registerProject(commandOptions, info);
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public IUsage getUsage() {
      return USAGE;
   }

   @Activate
   public void activate() {
      logService.trace(getClass(), "Activated");
   }

   @Deactivate
   public void deactivate() {
      logService.trace(getClass(), "Deactivated");
   }

   /**
    * Sets log service.
    *
    * @param ref the ref
    */
   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeLogService")
   public void setLogService(ILogService ref) {
      this.logService = ref;
   }

   /**
    * Remove log service.
    */
   public void removeLogService(ILogService ref) {
      setLogService(null);
   }

   /**
    * Sets template service.
    *
    * @param ref the ref
    */
   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeTemplateService")
   public void setTemplateService(ITemplateService ref) {
      this.templateService = ref;
   }

   /**
    * Remove template service.
    */
   public void removeTemplateService(ITemplateService ref) {
      setTemplateService(null);
   }

   /**
    * Sets project naming service.
    *
    * @param ref the ref
    */
   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeProjectNamingService")
   public void setProjectNamingService(IProjectNamingService ref) {
      this.projectNamingService = ref;
   }

   /**
    * Remove project naming service.
    */
   public void removeProjectNamingService(IProjectNamingService ref) {
      setProjectNamingService(null);
   }

   /**
    * Sets package naming service.
    *
    * @param ref the ref
    */
   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removePackageNamingService")
   public void setPackageNamingService(IPackageNamingService ref) {
      this.packageNamingService = ref;
   }

   /**
    * Remove package naming service.
    */
   public void removePackageNamingService(IPackageNamingService ref) {
      setPackageNamingService(null);
   }

   /**
    * Sets java service generation service.
    *
    * @param ref the ref
    */
   @Reference(cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeJavaServiceGenerationService")
   public void setJavaServiceGenerationService(IJavaServiceGenerationService ref) {
      this.generationService = ref;
   }

   /**
    * Remove java service generation service.
    */
   public void removeJavaServiceGenerationService(IJavaServiceGenerationService ref) {
      setJavaServiceGenerationService(null);
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void setBuildManagementService(IBuildManagementService ref) {
      this.buildManagementService = ref;
   }

   public void removeBuildManagementService(IBuildManagementService ref) {
      setBuildManagementService(null);
   }

   protected void doCreateDirectories(Path outputDirectory) {
      try {
         Files.createDirectories(outputDirectory);
      } catch (IOException e) {
         logService.error(CreateJavaCucumberTestsCommand.class, e);
         throw new CommandException(e);
      }
   }

   /**
    * Create the usage for this command.
    *
    * @return the usage.
    */
   @SuppressWarnings("rawtypes")
   private static IUsage createUsage() {
      return new DefaultUsage("Generates the gradle distribution project for a Java application",
                              CommonParameters.GROUP_ID,
                              CommonParameters.ARTIFACT_ID,
                              CommonParameters.OUTPUT_DIRECTORY.required(),
                              CommonParameters.MODEL.required(),
                              CommonParameters.CLEAN,
                              new DefaultParameter(REFRESH_FEATURE_FILES_PROPERTY).setDescription(
                                    "If true, only copy the feature files and resources from the system descriptor "
                                    + "project into src/main/resources.")
                                    .setRequired(false));
   }

}