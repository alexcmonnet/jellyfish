package com.ngc.seaside.jellyfish;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import com.ngc.seaside.jellyfish.api.IJellyFishCommandProvider;
import com.ngc.seaside.systemdescriptor.service.impl.xtext.module.XTextSystemDescriptorServiceModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

public class JellyFish {

   private final Collection<Module> modules;

   /**
    * Main to run the JellyFish application.
    *
    * @param args the program arguments. The first argument should always be the name of the command in which to run
    *             followed by a list of parameters for that command.
    */
   public static void main(String[] args) {
      Preconditions.checkNotNull(args, "args may not be null!");
      try {
         run(args);
      } catch (IllegalArgumentException e) {
         System.err.format("%nERROR: %s%nTry running \"jellyfish help\" for usage information.%n", e.getMessage());
      }
   }

   /**
    * Run the JellyFish application.
    *
    * @param args the program arguments. The first argument should always be the name of the command in which to run
    *             followed by a list of parameters for that command.
    */
   public static void run(String[] args) {
      Preconditions.checkNotNull(args, "args may not be null!");
      new JellyFish().doRun(args);
   }

   /**
    * Runs JellyFish with the given modules.
    *
    * @param args    the program arguments. The first argument should always be the name of the command in which to run
    *                followed by a list of parameters for that command.
    * @param modules the Guice modules to execute JellyFish with.  When using this option, no default JellyFish modules
    *                will be loaded
    */
   public static void run(String[] args, Collection<Module> modules) {
      Preconditions.checkNotNull(args, "args may not be null!");
      Preconditions.checkNotNull(modules, "modules may not be null!");
      new JellyFish(modules).doRun(args);
   }

   private JellyFish() {
      this(getDefaultModules());
   }

   private JellyFish(Collection<Module> modules) {
      this.modules = modules;
   }

   private void doRun(String[] args) {
      Injector injector = getInjector();
      IJellyFishCommandProvider provider = injector.getInstance(IJellyFishCommandProvider.class);
      provider.run(args);
   }

   /**
    * @return the Guice injector
    */
   private Injector getInjector() {
      return Guice.createInjector(modules);
   }

   /**
    * Get a collection of Guice modules from the classpath. The service loader will look for a
    * property file called com.google.inject.Module located in the META-INF/services directory
    * of the jar. The file just needs to list all Guice Module classes with the fully qualified name.
    *
    * @return A collection of modules or an empty collection.
    */
   private static Collection<Module> getDefaultModules() {
      Collection<Module> modules = new ArrayList<>();
      modules.add(new JellyFishServiceModule());
      // Add the proxy command provider module so that an IJellyFishCommandProvider can be resolved.
      //modules.add(proxyJellyFishCommandProvider);
      for (Module dynamicModule : ServiceLoader.load(Module.class)) {
         // Ignore the XTextSystemDescriptorServiceModule, we'll createStringTable the module below via forStandaloneUsage().  This
         // is because XTextSystemDescriptorServiceModule is registered as an Module and the service loader picks it up.
         // However, we need to build the module via forStandaloneUsage() to make sure the XText framework is
         // initialized correctly.
         if (dynamicModule.getClass() != XTextSystemDescriptorServiceModule.class) {
            modules.add(dynamicModule);
         }
      }
      // Register the standalone version of the XText service.
      modules.add(XTextSystemDescriptorServiceModule.forStandaloneUsage());
      return modules;
   }
}