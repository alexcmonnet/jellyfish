package com.ngc.seaside.jellyfish.cli.command.createjavadistribution;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.ngc.seaside.command.api.ICommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;

public class CreateJavaDistributionCommandGuiceModule extends AbstractModule {

   @Override
   protected void configure() {
      Multibinder.newSetBinder(binder(), IJellyFishCommand.class).addBinding()
               .to(CreateJavaDistributionCommandGuiceWrapper.class);
      Multibinder.newSetBinder(binder(), ICommand.class).addBinding()
               .to(CreateJavaDistributionCommandGuiceWrapper.class);
   }
}