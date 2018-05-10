package com.ngc.seaside.jellyfish.impl.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.ICommand;
import com.ngc.seaside.jellyfish.api.ICommandOptions;
import com.ngc.seaside.jellyfish.api.ICommandProvider;
import com.ngc.seaside.jellyfish.api.IUsage;
import com.ngc.seaside.jellyfish.service.parameter.api.IParameterService;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wraps the default command provider for use with Guice.
 */
@Singleton
@SuppressWarnings({"unchecked"})
public class DefaultCommandProviderGuiceWrapper implements ICommandProvider<
      ICommandOptions,
      ICommand<ICommandOptions>,
      ICommandOptions> {

   /**
    * The delegate.
    */
   private final DefaultCommandProvider delegate = new DefaultCommandProvider();

   /**
    * The commands as injected by Guice.  Note some of these may not be real commands but Guice created proxies.  See
    * the note in the constructor.
    */
   private final Set<ICommand> commandProxies;

   /**
    * If true, commands have been injected into the delegate.  If false, they haven been.
    */
   private final AtomicBoolean areCommandsInjected = new AtomicBoolean(false);

   @Inject
   public DefaultCommandProviderGuiceWrapper(ILogService logService,
                                             IParameterService parameterService,
                                             Set<ICommand> commands) {
      delegate.setLogService(logService);
      delegate.setParameterService(parameterService);
      // Note we can't call commands.forEach(delegate::addCommand) because they may throw a Guice exception if a
      // command requires the IJellyFishCommandProvider to be injected into it.  If this is the case, Guice creates a
      // proxy for the command but it would let us use the proxy until all injection is completed.
      commandProxies = commands;
      delegate.activate();
   }

   @Override
   public IUsage getUsage() {
      injectCommandsIfNeeded();
      return delegate.getUsage();
   }

   @Override
   public ICommandOptions run(String[] arguments) {
      injectCommandsIfNeeded();
      return delegate.run(arguments);
   }

   @Override
   public ICommand<ICommandOptions> getCommand(String commandName) {
      injectCommandsIfNeeded();
      return delegate.getCommand(commandName);
   }

   @Override
   public void addCommand(ICommand<ICommandOptions> command) {
      injectCommandsIfNeeded();
      delegate.addCommand(command);
   }

   @Override
   public void removeCommand(ICommand<ICommandOptions> command) {
      injectCommandsIfNeeded();
      delegate.removeCommand(command);
   }

   private void injectCommandsIfNeeded() {
      if (areCommandsInjected.compareAndSet(false, true)) {
         commandProxies.forEach(delegate::addCommand);
      }
   }
}