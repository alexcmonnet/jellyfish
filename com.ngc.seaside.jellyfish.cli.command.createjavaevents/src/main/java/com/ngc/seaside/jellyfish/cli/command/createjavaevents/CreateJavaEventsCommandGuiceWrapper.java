package com.ngc.seaside.jellyfish.cli.command.createjavaevents;

import com.google.inject.Inject;
import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.blocs.service.resource.api.IResourceService;
import com.ngc.seaside.command.api.IUsage;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandProvider;
import com.ngc.seaside.jellyfish.api.JellyFishCommandConfiguration;
import com.ngc.seaside.jellyfish.service.name.api.IPackageNamingService;
import com.ngc.seaside.jellyfish.service.name.api.IProjectNamingService;

@JellyFishCommandConfiguration(autoTemplateProcessing = false)
public class CreateJavaEventsCommandGuiceWrapper implements IJellyFishCommand {

   private final CreateJavaEventsCommand delegate = new CreateJavaEventsCommand();

   @Inject
   public CreateJavaEventsCommandGuiceWrapper(ILogService logService,
                                              IResourceService resourceService,
                                              IJellyFishCommandProvider jellyFishCommandProvider,
                                              IProjectNamingService projectNamingService,
                                              IPackageNamingService packageNamingService) {
      delegate.setLogService(logService);
      delegate.setResourceService(resourceService);
      delegate.setJellyFishCommandProvider(jellyFishCommandProvider);
      delegate.setProjectNamingService(projectNamingService);
      delegate.setPackageNamingService(packageNamingService);
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
