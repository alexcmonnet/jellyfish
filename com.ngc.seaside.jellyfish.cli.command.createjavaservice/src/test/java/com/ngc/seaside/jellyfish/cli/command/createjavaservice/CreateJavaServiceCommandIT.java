package com.ngc.seaside.jellyfish.cli.command.createjavaservice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import com.ngc.blocs.guice.module.LogServiceModule;
import com.ngc.blocs.guice.module.ResourceServiceModule;
import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.blocs.service.resource.api.IResourceService;
import com.ngc.blocs.test.impl.common.log.PrintStreamLogService;
import com.ngc.seaside.bootstrap.service.impl.templateservice.TemplateServiceGuiceModule;
import com.ngc.seaside.bootstrap.service.template.api.ITemplateService;
import com.ngc.seaside.command.api.CommandException;
import com.ngc.seaside.command.api.DefaultParameter;
import com.ngc.seaside.command.api.DefaultParameterCollection;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.cli.command.createjavaservice.CreateJavaServiceCommand;
import com.ngc.seaside.jellyfish.cli.command.createjavaservice.CreateJavaServiceCommandGuiceWrapper;
import com.ngc.seaside.jellyfish.cli.command.test.template.MockedTemplateService;
import com.ngc.seaside.systemdescriptor.model.api.ISystemDescriptor;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.service.api.IParsingResult;
import com.ngc.seaside.systemdescriptor.service.api.ISystemDescriptorService;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.module.XTextSystemDescriptorServiceModule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

public class CreateJavaServiceCommandIT {

   private static final Module TEST_SERVICE_MODULE = new AbstractModule() {
      @Override
      protected void configure() {
         bind(ILogService.class).to(PrintStreamLogService.class);
         MockedTemplateService mockedTemplateService = new MockedTemplateService();
         mockedTemplateService = new MockedTemplateService().useRealPropertyService().useDefaultUserValues(true)
                  .setTemplateDirectory(CreateJavaServiceCommand.class.getPackage().getName(),
                                        Paths.get("src/main/template"));

         bind(ITemplateService.class).toInstance(mockedTemplateService);

         IResourceService mockResource = Mockito.mock(IResourceService.class);
         Mockito.when(mockResource.getResourceRootPath()).thenReturn(Paths.get("src", "main", "resources"));

         bind(IResourceService.class).toInstance(mockResource);
      }
   };
   private static final Injector injector = Guice.createInjector(getModules());
   private IJellyFishCommand cmd = injector.getInstance(CreateJavaServiceCommandGuiceWrapper.class);
   private IJellyFishCommandOptions options = mock(IJellyFishCommandOptions.class);
   private IModel model;
   private Path outputDir;

   /**
    * @param folder must be a folder.
    */
   private static String printDirectoryTree(File folder) {
      if (!folder.isDirectory()) {
         throw new IllegalArgumentException("folder is not a Directory");
      }
      int indent = 0;
      StringBuilder sb = new StringBuilder();
      printDirectoryTree(folder, indent, sb);
      return sb.toString();
   }

   private static void printDirectoryTree(File folder, int indent,
                                          StringBuilder sb) {
      if (!folder.isDirectory()) {
         throw new IllegalArgumentException("folder is not a Directory");
      }
      sb.append(getIndentString(indent));
      sb.append("+--");
      sb.append(folder.getName());
      sb.append("/");
      sb.append("\n");

      for (File file : folder.listFiles()) {
         if (file.isDirectory()) {
            printDirectoryTree(file, indent + 1, sb);
         } else {
            printFile(file, indent + 1, sb);
         }
      }

   }

   private static void printFile(File file, int indent, StringBuilder sb) {
      sb.append(getIndentString(indent));
      sb.append("+--");
      sb.append(file.getName());
      sb.append("\n");
   }

   private static String getIndentString(int indent) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < indent; i++) {
         sb.append("|  ");
      }
      return sb.toString();
   }

   private static Collection<Module> getModules() {
      Collection<Module> modules = new ArrayList<>();
      modules.add(TEST_SERVICE_MODULE);
      for (Module dynamicModule : ServiceLoader.load(Module.class)) {
         if (!(dynamicModule instanceof LogServiceModule) && !(dynamicModule instanceof ResourceServiceModule)
             && !(dynamicModule instanceof TemplateServiceGuiceModule)) {
            modules.add(dynamicModule);
         }
      }

      modules.removeIf(m -> m instanceof XTextSystemDescriptorServiceModule);
      modules.add(XTextSystemDescriptorServiceModule.forStandaloneUsage());
      return modules;
   }

   @Before
   public void setup() throws IOException {
      // Setup test resources
      Properties props = System.getProperties();
      props.setProperty("NG_FW_HOME", Paths.get("src/main").toAbsolutePath().toString());

      Path outputDirectory = Paths.get("build/test-template");
      outputDir = Files.createDirectories(outputDirectory);

      // Use the testable template service.
      MockedTemplateService mockedTemplateService = new MockedTemplateService()
               .useRealPropertyService()
               .useDefaultUserValues(true)
               .setTemplateDirectory(CreateJavaServiceCommand.class.getPackage().getName(),
                                     Paths.get("src/main/template"));

      // Setup mock system descriptor
      Path sdDir = Paths.get("src", "test", "sd");
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.sd");
      Collection<Path> sdFiles = Files.walk(sdDir).filter(matcher::matches).collect(Collectors.toSet());
      ISystemDescriptorService sdService = injector.getInstance(ISystemDescriptorService.class);
      IParsingResult result = sdService.parseFiles(sdFiles);
      Assert.assertTrue(result.getIssues().toString(), result.isSuccessful());
      ISystemDescriptor sd = result.getSystemDescriptor();
      String modelId = "com.ngc.seaside.test1.Model1";
      model = sd.findModel(modelId).orElseThrow(() -> new CommandException("Unknown model:" + modelId));
      Mockito.when(options.getSystemDescriptor()).thenReturn(sd);

   }

   @Test
   public void testCommand() throws IOException {
      createSettings();

      runCommand(CreateJavaServiceCommand.MODEL_PROPERTY, "com.ngc.seaside.test1.Model1",
                 CreateJavaServiceCommand.OUTPUT_DIRECTORY_PROPERTY, outputDir.toString()
                // CreateJavaServiceCommand.GENERATE_DELEGATE_PROPERTY, "false"
                 );

      Mockito.verify(options, Mockito.times(1)).getParameters();
      Mockito.verify(options, Mockito.times(1)).getSystemDescriptor();

      // Verify tests
      printOutputFolderStructure(outputDir);
      checkGeneratedServiceFiles(outputDir);
      checkGeneratedServiceDelegateFile(outputDir);
      checkGeneratedBaseFiles(outputDir);
   }

   private void runCommand(String... keyValues) {
      DefaultParameterCollection collection = new DefaultParameterCollection();

      for (int n = 0; n + 1 < keyValues.length; n += 2) {
         collection.addParameter(new DefaultParameter<String>(keyValues[n]).setValue(keyValues[n + 1]));
      }

      Mockito.when(options.getParameters()).thenReturn(collection);

      cmd.run(options);
   }

   private void printOutputFolderStructure(Path outputDir) {
      String ouputFolderTree = printDirectoryTree(outputDir.toFile());
      Assert.assertTrue(ouputFolderTree.contains(model.getName() + ".java"));
      Assert.assertTrue(ouputFolderTree.contains(model.getName() + "Test.java"));
      System.out.println(ouputFolderTree);
   }

   private void checkGeneratedServiceDelegateFile(Path projectDir) throws IOException {
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**" + model.getName() + "GuiceWrapper.java");
      Collection<Path> file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());

      // There should only be one wrapper file generated
      Assert.assertEquals(1, file.size());
      Path generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue(model.getName() + "GuiceWrapper.java is missing", Files.isRegularFile(generatedFile));
      String actualContents = new String(Files.readAllBytes(generatedFile));

      Path
               expectedFile =
               Paths.get("src/test/resources/expectedfiles/" + model.getName() + "GuiceWrapper.java.expected");
      String expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);
   }

   private void checkGeneratedServiceFiles(Path projectDir) throws IOException {
      // Check build.gradle
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher(
               "glob:**" + model.getParent() + "." + model.getName().toLowerCase() + "/build.gradle");
      Collection<Path> file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());

      // There should only be one build.gradle file generated for the service bundle
      Assert.assertEquals(1, file.size());
      Path generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue(model.getParent() + "." + model.getName().toLowerCase() + "/build.gradle is missing",
                        Files.isRegularFile(generatedFile));
      String actualContents = new String(Files.readAllBytes(generatedFile));

      Path expectedFile =
               Paths.get("src/test/resources/expectedfiles/" + model.getName() + ".build.gradle.service.expected");
      String expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);

      // Checked Service file
      matcher = FileSystems.getDefault().getPathMatcher("glob:**" + model.getName() + ".java");
      file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());

      // Filter out anything that is from base
      ArrayList<Path> test = new ArrayList<>();
      for (Path path : file) {
         if (path.toAbsolutePath().toString().contains("base")) {
            test.add(path);
         }
      }
      file.removeAll(test);

      // There should only be one service file generated for the service
      Assert.assertEquals(1, file.size());
      generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue(model.getName() + ".java is missing", Files.isRegularFile(generatedFile));
      actualContents = new String(Files.readAllBytes(generatedFile));
      expectedFile = Paths.get("src/test/resources/expectedfiles/" + model.getName() + ".java.expected");
      expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);

      // Checked Service test file
      matcher = FileSystems.getDefault().getPathMatcher("glob:**" + model.getName() + "Test.java");
      file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());
      // There should only be one test file generated for the service
      Assert.assertEquals(1, file.size());
      generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue(model.getName() + "Test.java is missing", Files.isRegularFile(generatedFile));
      actualContents = new String(Files.readAllBytes(generatedFile));
      expectedFile = Paths.get("src/test/resources/expectedfiles/" + model.getName() + "Test.java.expected");
      expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);
   }

   private void checkGeneratedBaseFiles(Path projectDir) throws IOException {
      // Check build.gradle
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher(
               "glob:**" + model.getParent() + "." + model.getName().toLowerCase() + ".base/build.gradle");
      Collection<Path> file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());

      // There should only be one build.gradle file generated for the service bundle
      Assert.assertEquals(1, file.size());
      Path generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue(model.getParent() + "." + model.getName().toLowerCase() + ".base/build.gradle is missing",
                        Files.isRegularFile(generatedFile));
      String actualContents = new String(Files.readAllBytes(generatedFile));

      Path expectedFile =
               Paths.get("src/test/resources/expectedfiles/" + model.getName() + ".build.gradle.base.expected");
      String expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);

      // Checked service api file
      matcher = FileSystems.getDefault().getPathMatcher("glob:**I" + model.getName() + ".java");
      file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());

      // There should only be one service file generated for the service
      Assert.assertEquals(1, file.size());
      generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue("I"+model.getName() + ".java is missing", Files.isRegularFile(generatedFile));
      actualContents = new String(Files.readAllBytes(generatedFile));
      expectedFile = Paths.get("src/test/resources/expectedfiles/I" + model.getName() + ".java.expected");
      expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);

      // Checked Service transport topics file
      matcher = FileSystems.getDefault().getPathMatcher("glob:**" + model.getName() + "TransportTopics.java");
      file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());
      // There should only be one test file generated for the service
      Assert.assertEquals(1, file.size());
      generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue(model.getName() + "TransportTopicsTest.java is missing", Files.isRegularFile(generatedFile));
      actualContents = new String(Files.readAllBytes(generatedFile));
      expectedFile = Paths.get("src/test/resources/expectedfiles/" + model.getName() + "TransportTopics.java.expected");
      expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);

      // Checked Service transport topics file
      matcher = FileSystems.getDefault().getPathMatcher("glob:**Abstract" + model.getName() + ".java");
      file = Files.walk(projectDir).filter(matcher::matches).collect(Collectors.toSet());
      // There should only be one test file generated for the service
      Assert.assertEquals(1, file.size());
      generatedFile = Paths.get(file.toArray()[0].toString());
      Assert.assertTrue("Abstract"+model.getName() + ".java is missing", Files.isRegularFile(generatedFile));
      actualContents = new String(Files.readAllBytes(generatedFile));
      expectedFile = Paths.get("src/test/resources/expectedfiles/Abstract" + model.getName() + ".java.expected");
      expectedContents = new String(Files.readAllBytes(expectedFile));

      Assert.assertEquals(expectedContents, actualContents);
   }

   private void createSettings() throws IOException {
      try {
         Files.createFile(outputDir.resolve("settings.gradle"));
      } catch (FileAlreadyExistsException e) {
         // ignore
      }
   }
}
