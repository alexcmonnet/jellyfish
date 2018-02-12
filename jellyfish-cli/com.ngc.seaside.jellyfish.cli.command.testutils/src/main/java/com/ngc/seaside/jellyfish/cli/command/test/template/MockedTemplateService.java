package com.ngc.seaside.jellyfish.cli.command.test.template;

import com.google.common.base.Preconditions;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.service.impl.propertyservice.PropertyService;
import com.ngc.seaside.jellyfish.service.impl.templateservice.TemplateIgnoreComponent;
import com.ngc.seaside.jellyfish.service.impl.templateservice.TemplateVisitor;
import com.ngc.seaside.jellyfish.service.property.api.IProperties;
import com.ngc.seaside.jellyfish.service.property.api.IPropertyService;
import com.ngc.seaside.jellyfish.service.template.api.DefaultTemplateOutput;
import com.ngc.seaside.jellyfish.service.template.api.ITemplateOutput;
import com.ngc.seaside.jellyfish.service.template.api.ITemplateService;
import com.ngc.seaside.jellyfish.service.template.api.TemplateServiceException;
import com.ngc.seaside.jellyfish.api.IParameterCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class MockedTemplateService implements ITemplateService {

   private static final String TEMPLATE_FOLDER = "templateContent";
   private static final String TEMPLATE_PROPERTIES = "template.properties";

   private final Map<String, Path> templateNameToPath = new HashMap<>();
   private final Map<String, String> mockedUserInput = new HashMap<>();
   private final Map<String, Object> parameterOverrides = new HashMap<>();
   private boolean useDefaultUserValues = true;
   private IPropertyService propertyService;

   public MockedTemplateService setTemplateDirectory(String templateName, Path directory) {
      Preconditions.checkNotNull(templateName, "templateName may not be null!");
      Preconditions.checkNotNull(directory, "directory may not be null!");
      Preconditions.checkArgument(directory.toFile().isDirectory(), "%s is not a directory!", directory);
      templateNameToPath.put(templateName, directory.toAbsolutePath());
      return this;
   }

   public MockedTemplateService useRealPropertyService() {
      PropertyService ps = new PropertyService();
      ps.setLogService(mock(ILogService.class));
      ps.activate();
      this.propertyService = ps;
      return this;
   }

   public MockedTemplateService setPropertyService(IPropertyService propertyService) {
      this.propertyService = propertyService;
      return this;
   }

   public MockedTemplateService setMockedUserInput(String parameter, String result) {
      mockedUserInput.put(parameter, result);
      return this;
   }

   public MockedTemplateService useDefaultUserValues(boolean useDefaultUserValues) {
      this.useDefaultUserValues = useDefaultUserValues;
      return this;
   }

   public MockedTemplateService overrideParameter(String name, Object value) {
      parameterOverrides.put(name, value);
      return this;
   }

   @Override
   public boolean templateExists(String templateName) {
      return templateNameToPath.get(templateName) != null;
   }

   @Override
   public ITemplateOutput unpack(String templateName,
                                 IParameterCollection parameters,
                                 Path outputDirectory,
                                 boolean clean)
         throws TemplateServiceException {
      Preconditions.checkState(propertyService != null, "property service not configured!");
      ITemplateOutput output;
      try {
         Path templatePath = templateNameToPath.get(templateName);
         Preconditions.checkState(templatePath != null,
                                  "no template directory configured for template name %s.",
                                  templateName);
         TemplateIgnoreComponent templateIgnoreComponent =
               new TemplateIgnoreComponent(templatePath, TEMPLATE_FOLDER, mock(ILogService.class));
         templateIgnoreComponent.parse();

         output = updateTemplate(templatePath, parameters, outputDirectory, clean, templateIgnoreComponent);

      } catch (TemplateServiceException | IOException e) {
         String message = String.format("An error occurred processing the template zip file: %s", templateName);
         throw new TemplateServiceException(message, e);
      }

      return output;
   }

   /**
    * Update the template based on the the visitor pattern. This will replace any Velocity Template
    * parameters with the input values from the properties file.
    *
    * @param templateFolder          the template folder.
    * @param parameters              the parameters that should overwrite any parameter values in the
    *                                template.properties this means that it should
    * @param outputFolder            the output folder.
    * @param clean                   true if this should clean existing directories.
    * @param templateIgnoreComponent used to check files that should be copied instead of evaluated by velocity.
    */
   protected ITemplateOutput updateTemplate(Path templateFolder,
                                            IParameterCollection parameters,
                                            Path outputFolder,
                                            boolean clean,
                                            TemplateIgnoreComponent templateIgnoreComponent)
         throws IOException {
      // Parse template.properties file for each parameter and its default value
      IProperties parametersAndDefaults =
            propertyService.load(templateFolder.resolve(TEMPLATE_PROPERTIES));

      // For each parameter query the user for its value if that property isn't already in the parameters collection.
      Map<String, Object> parametersAndValues = new HashMap<>();
      for (String parameter : parametersAndDefaults.getKeys()) {
         Object value;
         if (parameters.containsParameter(parameter)) {
            //if the value is already passed in by the user, don't ask them for it again.
            value = parameters.getParameter(parameter).getValue();
            value = parameterOverrides.getOrDefault(parameter, value);
         } else {
            value = parametersAndDefaults.get(parameter);
            if (!useDefaultUserValues) {
               value = mockedUserInput.get(parameter);
               Preconditions.checkState(value != null, "no mocked user input for parameter %s configured!", parameter);
            }
         }

         if (!parametersAndDefaults.get(parameter).equals(value)) {
            parametersAndDefaults.put(parameter, value.toString());
            parametersAndDefaults.evaluate();
         }

         parametersAndValues.put(parameter, value);
      }

      // Insert any remaining parameters into the map so they are available to Velocity.
      // If the parameter is not already in the map, it must have not been referenced in the property value.
      // Even if that is the case, we still want to provide it.
      parameters.getAllParameters().forEach(p -> parametersAndValues.putIfAbsent(p.getName(), p.getValue()));

      TemplateVisitor visitor = new TemplateVisitor(parametersAndValues,
                                                    templateFolder.resolve(TEMPLATE_FOLDER),
                                                    outputFolder,
                                                    clean,
                                                    templateIgnoreComponent);

      // Walk through the unzipped template directory in order to generate the
      // instance of the template
      Files.walkFileTree(templateFolder.resolve(TEMPLATE_FOLDER), visitor);

      return new DefaultTemplateOutput()
            .setOutputPath(visitor.getTopLevelFolder())
            .setProperties(parametersAndValues);
   }
}
