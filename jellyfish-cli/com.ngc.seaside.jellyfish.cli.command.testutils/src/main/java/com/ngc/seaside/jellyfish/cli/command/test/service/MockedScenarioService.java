/**
 * UNCLASSIFIED
 *
 * Copyright 2020 Northrop Grumman Systems Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ngc.seaside.jellyfish.cli.command.test.service;

import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Optional;

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
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.ReceiveRequestStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.ReceiveStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.RespondStepHandler;
import com.ngc.seaside.systemdescriptor.service.log.api.ILogService;

public class MockedScenarioService implements IScenarioService {

   private final ScenarioService delegate;

   /**
    * Default constructor
    */
   public MockedScenarioService() {
      delegate = new ScenarioService();
      delegate.setLogService(mock(ILogService.class));
      delegate.setCorrelationStepHandler(new CorrelateStepHandler());
      delegate.setPublishStepHandler(new PublishStepHandler());
      delegate.setReceiveStepHandler(new ReceiveStepHandler());
      delegate.setReceiveRequestStepHandler(new ReceiveRequestStepHandler());
      delegate.setRespondStepHandler(new RespondStepHandler());
      delegate.activate();
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
   public Optional<IRequestResponseMessagingFlow> getRequestResponseMessagingFlow(IJellyFishCommandOptions options,
                                                                                  IScenario scenario) {
      return delegate.getRequestResponseMessagingFlow(options, scenario);
   }

   @Override
   public Collection<ITimingConstraint> getTimingConstraints(IJellyFishCommandOptions options, IScenario scenario) {
      return delegate.getTimingConstraints(options, scenario);
   }

}
