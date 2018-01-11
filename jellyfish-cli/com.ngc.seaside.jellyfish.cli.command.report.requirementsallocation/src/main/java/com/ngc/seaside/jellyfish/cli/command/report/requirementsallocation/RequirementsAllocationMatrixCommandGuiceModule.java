package com.ngc.seaside.jellyfish.cli.command.report.requirementsallocation;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.ngc.seaside.command.api.ICommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;

public class RequirementsAllocationMatrixCommandGuiceModule extends AbstractModule {

   @Override
   protected void configure() {
      Multibinder.newSetBinder(binder(), IJellyFishCommand.class).addBinding().to(RequirementsAllocationMatrixCommandGuiceWrapper.class);
      Multibinder.newSetBinder(binder(), ICommand.class).addBinding().to(RequirementsAllocationMatrixCommandGuiceWrapper.class);
   }
}