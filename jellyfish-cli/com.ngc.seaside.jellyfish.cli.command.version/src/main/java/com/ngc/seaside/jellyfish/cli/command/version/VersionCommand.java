/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2018, Northrop Grumman Systems Corporation
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

package com.ngc.seaside.jellyfish.cli.command.version;

import com.google.common.base.Preconditions;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.DefaultUsage;
import com.ngc.seaside.jellyfish.api.ICommand;
import com.ngc.seaside.jellyfish.api.ICommandOptions;
import com.ngc.seaside.jellyfish.api.IUsage;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

@Component(service = ICommand.class)
public class VersionCommand implements ICommand<ICommandOptions> {

   /**
    * The command name
    */
   public static final String COMMAND_NAME = "version";

   /**
    * The command usage
    */
   public static final IUsage COMMAND_USAGE = new DefaultUsage(
         "Prints the current Jellyfish version along with environment information.");

   /**
    * The environment variables that will be displayed along with the version information
    */
   public static final Map<String, String> ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES = new HashMap<>();

   private static final String USER_HOME = System.getProperty("user.home");
   private static final String DEFAULT_JELLYFISH_USER_HOME = USER_HOME + "/.jellyfish/plugins";
   private static final String DEFAULT_GRADLE_USER_HOME = USER_HOME + "/.gradle";
   private static final String DEFAULT_M2_USER_HOME = USER_HOME + "/.m2";

   private static final String PROPERTIES_FILE = "com.ngc.seaside.jellyfish.cli.command.version.properties";
   private static final String VERSION_PROPERTY = "version";

   private ILogService logService;
   private String version;

   /**
    * Initialize environment variables to display along with their default values that will be displayed if the
    * variables are not set in the user's environment.
    */
   public VersionCommand() {
      ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.put("JELLYFISH_USER_HOME", DEFAULT_JELLYFISH_USER_HOME);
      ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.put("GRADLE_USER_HOME", DEFAULT_GRADLE_USER_HOME);
      ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.put("M2_HOME", DEFAULT_M2_USER_HOME);
      ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.put("JAVA_HOME", "");
   }

   @Override
   public String getName() {
      return COMMAND_NAME;
   }

   @Override
   public IUsage getUsage() {
      return COMMAND_USAGE;
   }

   @Override
   public void run(ICommandOptions commandOptions) {
      displayVersions();
      logBlankLine();

      displayEnvironmentVariables();
      logBlankLine();
   }

   @Reference(
         cardinality = ReferenceCardinality.MANDATORY,
         policy = ReferencePolicy.STATIC,
         unbind = "removeLogService")
   public void setLogService(ILogService ref) {
      logService = ref;
   }

   public void removeLogService(ILogService ref) {
      setLogService(null);
   }

   private void displayVersions() {
      logItem("Jellyfish", this::getJellyfishVersion);
      logItem("Java", this::getJavaVersion);
   }

   private void logItem(String name, Supplier<String> function) {
      logService.info(getClass(), name + getSpaces(name) + function.get());
   }

   private String getSpaces(String name) {
      final int DEFAULT_NUM_EXTRA_SPACES = 5;

      int longestEnvVarNameLength = ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.keySet()
            .stream()
            .mapToInt(String::length)
            .max()
            .orElseGet(null);

      return StringUtils.repeat(' ', longestEnvVarNameLength + DEFAULT_NUM_EXTRA_SPACES - name.length());
   }

   private void logBlankLine() {
      logService.info(getClass(), "");
   }

   private String getJellyfishVersion() {
      loadProperty();
      return version;
   }

   private void loadProperty() {
      try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
         Properties props = new Properties();
         props.load(is);
         version = props.getProperty(VERSION_PROPERTY);
         Preconditions.checkState(version != null,
                                  "property '%s' not set in configuration properties!",
                                  VERSION_PROPERTY);
      } catch (IOException e) {
         throw new IllegalStateException("failed to load configuration properties from classpath!", e);
      }
   }

   private String getJavaVersion() {
      return System.getProperty("java.version");
   }

   private void displayEnvironmentVariables() {
      ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.keySet()
            .stream()
            .sorted()
            .forEach(name -> logItem(name, () -> getEnvironmentVariableValueOrDefault(name)));
   }

   private String getEnvironmentVariableValueOrDefault(String name) {
      String value = System.getenv(name);

      if (value == null) {
         return ENVIRONMENT_VARIABLE_NAMES_AND_DEFAULT_VALUES.get(name) + " (default)";
      }

      return value;
   }
}
