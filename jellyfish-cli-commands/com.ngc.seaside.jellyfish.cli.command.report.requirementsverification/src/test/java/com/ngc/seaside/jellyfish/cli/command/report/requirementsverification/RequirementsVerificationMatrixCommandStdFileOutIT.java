/**
 * UNCLASSIFIED
 *
 * Copyright 2020 Northrop Grumman Systems Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ngc.seaside.jellyfish.cli.command.report.requirementsverification;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.ngc.seaside.jellyfish.api.DefaultParameter;
import com.ngc.seaside.jellyfish.api.DefaultParameterCollection;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.feature.api.IFeatureService;
import com.ngc.seaside.jellyfish.service.feature.impl.featureservice.FeatureService;
import com.ngc.seaside.jellyfish.service.requirements.api.IRequirementsService;
import com.ngc.seaside.jellyfish.service.requirements.impl.requirementsservice.RequirementsService;
import com.ngc.seaside.jellyfish.utilities.console.impl.stringtable.MultiLineRow;
import com.ngc.seaside.jellyfish.utilities.console.impl.stringtable.StringTable;
import com.ngc.seaside.systemdescriptor.model.api.ISystemDescriptor;
import com.ngc.seaside.systemdescriptor.scenario.impl.module.StepsSystemDescriptorServiceModule;
import com.ngc.seaside.systemdescriptor.service.api.IParsingResult;
import com.ngc.seaside.systemdescriptor.service.api.ISystemDescriptorService;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.module.XTextSystemDescriptorServiceModule;
import com.ngc.seaside.systemdescriptor.service.log.api.ILogService;
import com.ngc.seaside.systemdescriptor.service.log.api.PrintStreamLogService;
import com.ngc.seaside.systemdescriptor.service.repository.api.IRepositoryService;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class RequirementsVerificationMatrixCommandStdFileOutIT {

   private static final PrintStreamLogService logger = new PrintStreamLogService();
   private static final FeatureService featureService = new FeatureService();
   private static final RequirementsService requirementsService = new RequirementsService();
   private static final Injector injector = Guice.createInjector(getModules());
   private static final String TESTFOLDER = "build/test/verification-matrix/results/";
   private static final String TESTREGEX = "([\\n\\r]+\\s*)*$";
   private RequirementsVerificationMatrixCommand cmd;
   private DefaultParameterCollection parameters;
   @Mock(answer = Answers.RETURNS_DEEP_STUBS)
   private IJellyFishCommandOptions jellyFishCommandOptions;
   private StringTable<Requirement> table;

   private static Collection<Module> getModules() {
      Collection<Module> modules = new ArrayList<>();
      modules.add(XTextSystemDescriptorServiceModule.forStandaloneUsage());
      modules.add(new StepsSystemDescriptorServiceModule());
      modules.add(new AbstractModule() {
         @Override
         protected void configure() {
            bind(ILogService.class).toInstance(logger);
            bind(IFeatureService.class).toInstance(featureService);
            bind(IRequirementsService.class).toInstance(requirementsService);
            bind(IRepositoryService.class).toInstance(mock(IRepositoryService.class));
         }
      });
      return modules;
   }

   @Before
   public void setup() throws IOException {
      parameters = new DefaultParameterCollection();
      when(jellyFishCommandOptions.getParameters()).thenReturn(parameters);
      when(jellyFishCommandOptions.getParsingResult().getTestSourcesRoot())
               .thenReturn(Paths.get("src", "test", "resources", "src", "test", "gherkin"));

      // Setup class under test
      cmd = new RequirementsVerificationMatrixCommand() {
         @Override
         protected Path evaluateOutput(IJellyFishCommandOptions commandOptions) {
            Path output;
            if (commandOptions.getParameters().containsParameter(OUTPUT_PROPERTY)) {
               String outputUri = commandOptions.getParameters().getParameter(OUTPUT_PROPERTY).getStringValue();
               output = Paths.get(outputUri);
               if (!output.isAbsolute()) {
                  output = Paths.get("build/test/verification-matrix/results/").resolve(output.getFileName());
               }
               return output;
            } else {
               return null;
            }
         }

         @Override
         protected StringTable<Requirement> generateDefaultVerificationMatrix(Collection<Requirement> requirements,
                  Collection<String> features) {
            table = super.generateDefaultVerificationMatrix(requirements, features);
            return table;
         }
      };
      cmd.setLogService(logger);
      cmd.setRequirementsService(requirementsService);
      cmd.setFeatureService(featureService);

      // Setup mock system descriptor
      Path sdDir = Paths.get("src", "test", "resources");
      ISystemDescriptorService sdService = injector.getInstance(ISystemDescriptorService.class);
      IParsingResult result = sdService.parseProject(sdDir);
      Assert.assertTrue(result.getIssues().toString(), result.isSuccessful());
      ISystemDescriptor sd = result.getSystemDescriptor();
      Mockito.when(jellyFishCommandOptions.getSystemDescriptor()).thenReturn(sd);
   }

   @Test
   public void testOutputWithAbsolutePathAndWithDefaultStereotypes() throws IOException {
      Path outputDir = Paths.get("build/matrix-verification/tests/results/my/test/test1_default");
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_FORMAT_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OUTPUT_FORMAT_PROPERTY));
      parameters.addParameter(
               new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_PROPERTY,
                        outputDir.toAbsolutePath().toString()));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.VALUES_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_VALUES_PROPERTY));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OPERATOR_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OPERATOR_PROPERTY));

      cmd.run(jellyFishCommandOptions);

      // Verify table structure
      List<MultiLineRow> rows = table.getRows();
      assertEquals(4, rows.size());
      rows.forEach(row -> {
         assertEquals(1, row.getNumberOfLines());
         assertEquals(9, row.getCells().size());
      });

      // Verify output string
      File result = Paths.get(outputDir.toAbsolutePath().toString()).toFile();
      String test = FileUtils.readFileToString(result, Charset.defaultCharset());

      String expected = table.toString().replaceAll(TESTREGEX, "");
      String actual = test.replaceAll(TESTREGEX, "");

      assertEquals(expected, actual);
      // Uncomment to view files
      Files.delete(result.toPath());
   }

   @Test
   public void testOutputWithRelativePathAndWithDefaultStereotypes() throws IOException {
      Path outputDir = Paths.get("src/main/sd/test/results/test2_default");
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_FORMAT_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OUTPUT_FORMAT_PROPERTY));
      parameters.addParameter(
               new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_PROPERTY, outputDir.toString()));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.VALUES_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_VALUES_PROPERTY));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OPERATOR_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OPERATOR_PROPERTY));

      cmd.run(jellyFishCommandOptions);

      // Verify table structure
      List<MultiLineRow> rows = table.getRows();
      assertEquals(4, rows.size());
      rows.forEach(row -> {
         assertEquals(1, row.getNumberOfLines());
         assertEquals(9, row.getCells().size());
      });

      // Verify output string
      File result = Paths.get(TESTFOLDER).resolve(outputDir.getFileName()).toFile();
      String test = FileUtils.readFileToString(result, Charset.defaultCharset());

      String expected = table.toString().replaceAll(TESTREGEX, "");
      String actual = test.replaceAll(TESTREGEX, "");

      assertEquals(expected, actual);
      // Uncomment to view files
      Files.delete(result.toPath());
   }

   @Test
   public void testOutputWithRelativePathAndWithAdditionalStereotype() throws IOException {
      Path outputDir = Paths.get("src/main/sd/test/results/a/test3_default.txt");
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_FORMAT_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OUTPUT_FORMAT_PROPERTY));
      parameters.addParameter(
               new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_PROPERTY, outputDir.toString()));
      parameters.addParameter(
               new DefaultParameter<>(RequirementsVerificationMatrixCommand.VALUES_PROPERTY, "service,system"));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OPERATOR_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OPERATOR_PROPERTY));

      cmd.run(jellyFishCommandOptions);

      // Verify table structure
      List<MultiLineRow> rows = table.getRows();
      assertEquals(5, rows.size());
      rows.forEach(row -> {
         assertEquals(1, row.getNumberOfLines());
         assertEquals(10, row.getCells().size());
      });

      // Verify output string
      File result = Paths.get(TESTFOLDER).resolve(outputDir.getFileName()).toFile();
      String test = FileUtils.readFileToString(result, Charset.defaultCharset());

      String expected = table.toString().replaceAll(TESTREGEX, "");
      String actual = test.replaceAll(TESTREGEX, "");

      assertEquals(expected, actual);
      // Uncomment to view files
      Files.delete(result.toPath());
   }

   @Test
   public void testOutputWithRelativePathAndWithAbsentStereotype() throws IOException {
      Path outputDir = Paths.get("src/main/sd/test/results/test4_default");
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_FORMAT_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OUTPUT_FORMAT_PROPERTY));
      parameters.addParameter(
               new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_PROPERTY, outputDir.toString()));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.VALUES_PROPERTY, "model"));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OPERATOR_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OPERATOR_PROPERTY));

      cmd.run(jellyFishCommandOptions);

      // Verify table structure
      List<MultiLineRow> rows = table.getRows();
      assertEquals(0, rows.size());

      // Verify output string
      File result = Paths.get(TESTFOLDER).resolve(outputDir.getFileName()).toFile();
      String test = FileUtils.readFileToString(result, Charset.defaultCharset());

      String expected = table.toString().replaceAll(TESTREGEX, "");
      String actual = test.replaceAll(TESTREGEX, "");

      assertEquals(expected, actual);
      // Uncomment to view files
      Files.delete(result.toPath());
   }

   @Test
   public void testOutputWithRelavtivePathButWithoutDefaultStereotype() throws IOException {
      Path outputDir = Paths.get("src/main/sd/test/results/test5_default");
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_FORMAT_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_OUTPUT_FORMAT_PROPERTY));
      parameters.addParameter(
               new DefaultParameter<>(RequirementsVerificationMatrixCommand.OUTPUT_PROPERTY, outputDir.toString()));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.VALUES_PROPERTY,
               RequirementsVerificationMatrixCommand.DEFAULT_VALUES_PROPERTY));
      parameters.addParameter(new DefaultParameter<>(RequirementsVerificationMatrixCommand.OPERATOR_PROPERTY, "NOT"));

      cmd.run(jellyFishCommandOptions);

      // Verify table structure
      List<MultiLineRow> rows = table.getRows();
      assertEquals(1, rows.size());

      // Verify output string
      File result = Paths.get(TESTFOLDER).resolve(outputDir.getFileName()).toFile();
      String test = FileUtils.readFileToString(result, Charset.defaultCharset());

      String expected = table.toString().replaceAll(TESTREGEX, "");
      String actual = test.replaceAll(TESTREGEX, "");

      assertEquals(expected, actual);
   }
}
