package com.ngc.seaside.jellyfish.cli.command.test.service;

import static org.mockito.Mockito.mock;

import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.scenario.api.IPublishSubscribeMessagingFlow;
import com.ngc.seaside.jellyfish.service.scenario.api.IRequestResponseMessagingFlow;
import com.ngc.seaside.jellyfish.service.scenario.api.IScenarioService;
import com.ngc.seaside.jellyfish.service.scenario.api.ITimingConstraint;
import com.ngc.seaside.jellyfish.service.scenario.api.MessagingParadigm;
import com.ngc.seaside.jellyfish.service.scenario.impl.scenarioservice.ScenarioService;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenario;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.CorrelateStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.PublishStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.ReceiveStepHandler;

import java.util.Collection;
import java.util.Optional;

public class MockedScenarioService implements IScenarioService {

   private ScenarioService delegate = new ScenarioService();

   public MockedScenarioService() {
      delegate.setLogService(mock(ILogService.class));
      delegate.setCorrelationStepHandler(new CorrelateStepHandler());
      delegate.setPublishStepHandler(new PublishStepHandler());
      delegate.setReceiveStepHandler(new ReceiveStepHandler());
   }

   @Override
   public Collection<MessagingParadigm> getMessagingParadigms(IJellyFishCommandOptions options, IScenario scenario) {
      return delegate.getMessagingParadigms(options, scenario);
   }

   @Override
   public Optional<IPublishSubscribeMessagingFlow> getPubSubMessagingFlow(IJellyFishCommandOptions options,
            IScenario scenario) {
      return delegate.getPubSubMessagingFlow(options, scenario);
   }

   @Override
   public Collection<IRequestResponseMessagingFlow> getRequestResponseMessagingFlows(IJellyFishCommandOptions options,
            IScenario scenario) {
      return delegate.getRequestResponseMessagingFlows(options, scenario);
   }

   @Override
   public Collection<ITimingConstraint> getTimingConstraints(IJellyFishCommandOptions options, IScenario scenario) {
      return delegate.getTimingConstraints(options, scenario);
   }

}