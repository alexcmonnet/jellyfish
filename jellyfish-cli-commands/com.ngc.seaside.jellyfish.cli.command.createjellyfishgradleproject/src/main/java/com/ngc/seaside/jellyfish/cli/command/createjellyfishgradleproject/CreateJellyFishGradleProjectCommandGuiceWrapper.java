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
package com.ngc.seaside.jellyfish.cli.command.createjellyfishgradleproject;

import com.google.inject.Inject;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.IUsage;
import com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService;
import com.ngc.seaside.jellyfish.service.name.api.IProjectNamingService;
import com.ngc.seaside.jellyfish.service.template.api.ITemplateService;
import com.ngc.seaside.jellyfish.service.user.api.IJellyfishUserService;

public class CreateJellyFishGradleProjectCommandGuiceWrapper implements IJellyFishCommand {

   private final CreateJellyFishGradleProjectCommand delegate = new CreateJellyFishGradleProjectCommand();

   @Inject
   public CreateJellyFishGradleProjectCommandGuiceWrapper(ILogService logService,
                                                          ITemplateService templateService,
                                                          IBuildManagementService buildManagementService,
                                                          IProjectNamingService projectNamingService,
                                                          IJellyfishUserService jellyfishUserService) {
      delegate.setLogService(logService);
      delegate.setTemplateService(templateService);
      delegate.setBuildManagementService(buildManagementService);
      delegate.setProjectNamingService(projectNamingService);
      delegate.setJellyfishUserService(jellyfishUserService);
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
