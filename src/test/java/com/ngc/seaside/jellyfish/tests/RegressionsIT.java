package com.ngc.seaside.jellyfish.tests;

import com.ngc.seaside.jellyfish.cli.gradle.JellyFishProjectGenerator;
import org.gradle.api.logging.Logger;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RegressionsIT {

   // Class variable to hold regressions directory
   String regressionTestsDir = "";

   @Before
   public void setup() throws Throwable {
      // Set the regressions directory variable
      regressionTestsDir = System.getProperty("user.dir") + File.separator + "regressions";
   }

   /**
    * This is the start method for the regression test
    * 
    * @throws IOException - Looking through directories and File reading
    */
   @Test
   public void testGenerationAndDiff() throws IOException {

      File[] subs = new File(regressionTestsDir).listFiles();

      if (subs.length == 0) {
         System.err.println("Error: There are no directories under the 'regressions' folder. This test"
            + "looks for projects under the 'regressions' folder to perform tests.");
      } else {

         // Loop through the subdirectories under 'regressions' and perform operations
         for (File subDir : subs) {
            if (subDir.isDirectory()) {

               // Call to method that runs jellyfish
               generateJellyfishProject(subDir);
            }
         }
      }
   }

   /**
    * Run Jellyfish using .properties file information
    * 
    * @param directory - the directory of the regression test (ex: 1, 2, etc)
    * @throws IOException
    */
   private static void generateJellyfishProject(File directory) throws IOException {
      System.out.println("Generating jelly fish project in directory: " + directory);

      // Parse the properties file and store the relevant data
      Map<String, String> propertiesValues = readJellyfishPropertiesFile(directory.getAbsolutePath());
      String relPath = File.separator + propertiesValues.get("inputDir").replace('/', File.separatorChar);
      String fullInputDirPath = directory + relPath;

      Map<String, String> generatorArguments = new HashMap<String, String>();
      generatorArguments.put("outputDirectory", directory.getAbsolutePath());
      generatorArguments.put("version", propertiesValues.get("version"));
      generatorArguments.put("model", propertiesValues.get("model"));
      generatorArguments.put("projectName", "generatedProject");

      // Create the generation project object
      Logger log = Mockito.mock(Logger.class);
      JellyFishProjectGenerator proj = new JellyFishProjectGenerator(log)
               .setCommand(propertiesValues.get("command"))
               .setInputDir(fullInputDirPath)
               .setArguments(generatorArguments);

      // Generate the object with a firey passion
      proj.generate();
      
      // At this point, the project has been generated. Now, run 'gradle clean build -x text' on each subproject
      runGradleCleanBuildOnProjects(directory.getAbsolutePath());
      
      
      // TODO: Clean up. I suppose it'd be best to return the folder to it's pre-build state. i.e. - remove
      //    the build folders from each subdir, the .gradle folder, and possibly the generated project folder. 
      
//      cleanUpDirectories(directory);

   }


   /**
    * Run 'gradle clean build -x test' command for the generated subproject
    * 
    * @param directory - the directory of the newly generated regression test project
    * @throws IOException
    */
   private static void runGradleCleanBuildOnProjects(String directory) throws IOException {
      
      String givenProject = "";
      for (File file : new File(directory).listFiles()) {
         if (file.getName().contains("com.ngc")) {
            givenProject = file.getAbsolutePath();
            break;
         }
      }
      
      Assert.assertTrue(givenProject != "");
      String generatedProj = directory + File.separator + "generatedProject";
      
      String gradleHome = System.getenv("GRADLE_HOME");
      Assert.assertNotNull("GRADLE_HOME not set", gradleHome);

      // Perform the 'gradle clean build -x test' command
      // TODO: This build below is what was failing due to a "windows file path too long" bug. 

      System.out.println("Running 'gradle clean build -x test' on given project: " + givenProject);
      ProjectConnection connectionToGivenProj = GradleConnector.newConnector()
               .useInstallation(Paths.get(gradleHome).toFile())
               .forProjectDirectory(new File(givenProject))
               .connect();

      try {
         BuildLauncher build = connectionToGivenProj.newBuild();
         build.forTasks("clean", "build");
         build.withArguments("-x", "test");
         build.setStandardError(System.err).setStandardOutput(System.out);

         build.run();
      } finally {
         connectionToGivenProj.close();
      }
      
      System.out.println("Running 'gradle clean build -x test' on newly generated project: " + generatedProj);
      // Perform the 'gradle clean build -x test' command
      ProjectConnection connectionToGeneratedProj = GradleConnector.newConnector()
               .useInstallation(Paths.get(gradleHome).toFile())
               .forProjectDirectory(new File(generatedProj))
               .connect();

      try {
         BuildLauncher build = connectionToGeneratedProj.newBuild();
         build.forTasks("clean", "build");
         build.withArguments("-x", "test");
         build.setStandardError(System.err).setStandardOutput(System.out);

         build.run();
      } finally {
         connectionToGeneratedProj.close();
      }

      diffDefaultAndGeneratedProjectTrees(generatedProj);
   }

   /**
    * Perform a diff on the generated project and the given project. They should match
    * 
    * @param tmp - the directory of the newly generated regression test project
    * @throws IOException
    */
   private static void diffDefaultAndGeneratedProjectTrees(String generated) throws IOException {
      String base = generated.replace(File.separator + "generatedProject", "");
      Assert.assertTrue("Base folder doesn't exist: " + base, new File(base).exists());

      File defaultProj = null;
      for (File file : new File(base).listFiles()) {
         if (file.getName().contains("com.ngc")) {
            defaultProj = file;
            break;
         }
      }

      Assert.assertNotNull("Error - Missing 'com.ngc...' subdirectory under " + base,
         defaultProj);

      Assert.assertTrue("Generated folder doesn't exist: " + generated, new File(generated).exists());
      Assert.assertTrue("Given folder doesn't exist: " + defaultProj, defaultProj.exists());
      File genDir = new File(generated);

      System.out.println("\nComparing Project Trees:\n------------------------");
      System.out.println("Default Project  : " + defaultProj);
      System.out.println("Generated Project: " + generated + "\n");

      // TODO: There is a bug here. It does not fail when it encounters a mismatch. It just logs it and moves on. This is not
      //    the expected behavior. 
      Assert.assertTrue(compareDirectories(defaultProj, genDir));
//      compareDirectories(defaultProj, genDir);

   }

   private static boolean compareDirectories(File defaultProj, File genProj) throws IOException {

      boolean equality = true;

      Set<String> ignoreFiles = new HashSet<String>();
      //ignoreFiles.add(".gradle");

      ArrayList<File> defaultFiles = new ArrayList<File>();
      ArrayList<File> genProjFiles = new ArrayList<File>();

      for (File file : defaultProj.listFiles()) {
         if (!ignoreFiles.contains(file.getName())) {
            defaultFiles.add(file);
         }
      }

      for (File file : genProj.listFiles()) {
         if (!ignoreFiles.contains(file.getName())) {
            genProjFiles.add(file);
         }
      }

      if (defaultFiles.size() != genProjFiles.size()) {
         prettyPrintFolderContainDifferentListFiles(defaultProj, genProj);
         equality = false;
      } else {
         for (int i = 0; i < defaultFiles.size(); i++) {
            File subFileDefDir = defaultFiles.get(i);
            File subFileGenDir = genProjFiles.get(i);

            if (subFileDefDir.isFile() && subFileGenDir.isDirectory() ||
               subFileDefDir.isDirectory() && subFileGenDir.isFile()) {
               prettyPrintFilesDifferentType(subFileDefDir, subFileGenDir);
               equality = false;
            }

            if (subFileDefDir.isFile() && subFileGenDir.isFile()) {
               if (!FileUtils.contentEquals(subFileDefDir, subFileGenDir)) { // This could throw an IO Exception
                  equality = validFileDifferences(subFileDefDir, subFileGenDir);
               }
            }

            if (subFileDefDir.isDirectory() && subFileGenDir.isDirectory()) {
               compareDirectories(subFileDefDir, subFileGenDir);
            }
         }
      }

      return equality;
   }

   private static void prettyPrintFolderContainDifferentListFiles(File defProj, File genProj) {
      System.out.println("--------------------------\nDirectories are not equal:");

      File[] subDir1 = defProj.listFiles();
      File[] subDir2 = genProj.listFiles();

      System.out.println("Folder: " + defProj.getAbsolutePath());
      for (int i = 0; i < subDir1.length; i++) {
         System.out.println("\t- " + subDir1[i].getName());
      }

      System.out.println("\nFolder: " + genProj.getAbsolutePath());
      for (int i = 0; i < subDir2.length; i++) {
         System.out.println("\t- " + subDir2[i].getName());
      }

      System.out.println("");
   }

   private static boolean validFileDifferences(File defFile, File genFile) throws IOException {
      boolean noValidDifferencesFound = true;

      String generatedName = "generatedProject";
      ArrayList<String> defPath = new ArrayList<String>();

      defPath.add(defFile.getName());
      File parent = defFile.getParentFile();
      while (parent != null) {
         defPath.add(0, parent.getName());
         parent = parent.getParentFile();
      }

      String projectName = "";
      for (int i = 0; i < defPath.size(); i++) {
         if (defPath.get(i).contains("com.ngc.")) {
            projectName = defPath.get(i);
            break;
         }
      }

      BufferedReader defaultBr = new BufferedReader(new FileReader(defFile));
      BufferedReader generateBr = new BufferedReader(new FileReader(genFile));

      try {
         String defaultFileLine;
         String generatedFileLine;
         int lineNum = 0;
         while ((defaultFileLine = defaultBr.readLine()) != null
            && (generatedFileLine = generateBr.readLine()) != null) {
            lineNum++;

            // It isn't really an error if the only difference is the project name not matching with "generateProject".
            // This is expected. So let's work around this by temporarily changing the generatedProject files
            // to say the default project name, rather than 'generatedProject'.
            boolean switchNames = false;
            if (defaultFileLine.contains(projectName) && generatedFileLine.contains(generatedName)) {
               generatedFileLine = generatedFileLine.replace(generatedName, projectName);
               switchNames = true;
            }

            if (!defaultFileLine.equals(generatedFileLine)) {

               if (switchNames) {
                  generatedFileLine = generatedFileLine.replace(projectName, generatedName);
               }
               noValidDifferencesFound = false;
               prettyPrintFilesNotEqual(defFile, genFile, lineNum, defaultFileLine, generatedFileLine);
            }

            if (switchNames) {
               generatedFileLine = generatedFileLine.replace(projectName, generatedName);
            }
         }
      } finally {
         defaultBr.close();
         generateBr.close();
      }

      return noValidDifferencesFound;
   }

   private static void prettyPrintFilesNotEqual(File defFile, File genFile, int diffLineNum,
            String defLine, String genLine)
      throws IOException {
      System.out.println("--------------------\nFiles are not equal:");
      System.out.println("Default File: " + defFile.getAbsolutePath());
      System.out.println("Generated File: " + genFile.getAbsolutePath() + "\n");

      System.out.println("Line " + diffLineNum + ":");
      System.out.println("Default file\t: " + defLine);
      System.out.println("Generated file\t: " + genLine + "\n");
   }

   private static void prettyPrintFilesDifferentType(File file1, File file2) {
      if (file1.isFile() && file2.isDirectory()) {
         System.out.println("File " + file1.getName() + " is a FILE and File " + file2.getName() + " is a DIRECTORY");
      } else {
         System.out.println("File " + file1.getName() + " is a DIRECTORY and File " + file2.getName() + " is a FILE");
      }
   }
   

   /**
    * CleanUpDirectories - we need to revert the given project back to its pre-built state. T
    * @param directory
    */
   private static void cleanUpDirectories(File directory) {
      System.out.println("Cleaning up directory: " + directory);
      String projName = "";
      for (File file : directory.listFiles()) {
         if (file.getName().contains("com.ngc")) {
            projName = file.getName();
            break;
         }
      }
      
      Assert.assertTrue(projName != "");
      File givenProj = new File(directory.getAbsoluteFile() + File.separator + projName);
      File generatedProj = new File(directory.getAbsoluteFile() + File.separator + "generatedProject");
      Assert.assertTrue(givenProj.exists());
      Assert.assertTrue(generatedProj.exists());
      
      for (File file : generatedProj.listFiles()) {
         if (file.getName().equals(".gradle")) {
            deleteDir(file);
         }
      }
      
      System.out.println("");
      // TODO Auto-generated method stub
      
   }

   private static Map<String, String> readJellyfishPropertiesFile(String dir) throws IOException {
      Map<String, String> props = new HashMap<String, String>();
      File fin = new File(dir + "\\jellyfish.properties");
      FileInputStream fis = new FileInputStream(fin);

      // Construct BufferedReader from InputStreamReader
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));

      String line = null;
      while ((line = br.readLine()) != null) {
         String[] splitLine = line.split("=");
         for (int i = 0; i < splitLine.length; i++) {
            props.put(splitLine[0], splitLine[1]);
         }
      }

      br.close();
      return props;
   }

   static void deleteDir(File file) {
      File[] contents = file.listFiles();
      if (contents != null) {
          for (File f : contents) {
              deleteDir(f);
          }
      }
      file.delete();
  }
   
}
