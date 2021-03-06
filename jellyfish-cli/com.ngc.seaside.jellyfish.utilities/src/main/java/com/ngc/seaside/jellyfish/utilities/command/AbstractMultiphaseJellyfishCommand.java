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
package com.ngc.seaside.jellyfish.utilities.command;

import com.ngc.seaside.jellyfish.api.CommandException;
import com.ngc.seaside.jellyfish.api.CommonParameters;
import com.ngc.seaside.jellyfish.api.DefaultParameter;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.api.IParameter;
import com.ngc.seaside.jellyfish.service.name.api.IProjectInformation;

/**
 * Base class for commands while are designed to be invoked in multiple phases.  These are usually commands that have
 * multiple templates and/or generate projects under the {@code generated-projects} directory.
 *
 * @see JellyfishCommandPhase
 */
public abstract class AbstractMultiphaseJellyfishCommand extends AbstractJellyfishCommand {

   /**
    * Creates a command with the given name.
    *
    * @param name the name of the command.
    */
   public AbstractMultiphaseJellyfishCommand(String name) {
      super(name);
   }

   /**
    * Invoked to run the default phase of the command.  This method should generate stubs only and not fully generated
    * code.  This method may safely invoke
    * {@link com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService#registerProject(
    * ICommandOptions, IProjectInformation)}.
    */
   protected abstract void runDefaultPhase();

   /**
    * Invoked to run the deferred phase of the command.  This method should not generate stubs.  It should generate only
    * fully generated code that is never modified.  This method may not invoked {@link
    * com.ngc.seaside.jellyfish.service.buildmgmt.api.IBuildManagementService#registerProject(ICommandOptions,
    * IProjectInformation)}.
    */
   protected abstract void runDeferredPhase();

   /**
    * Runs this command, invoking the method for the appropriate phase.
    */
   @Override
   protected void doRun() {
      switch (getPhase()) {
         case DEFERRED:
            runDeferredPhase();
            break;
         default:
            runDefaultPhase();
            break;
      }
   }

   /**
    * Used to help construct an {@code IUsage} object for a command that supports being run in all phases.
    */
   protected static IParameter<?> allPhasesParameter() {
      return phaseParameter(JellyfishCommandPhase.DEFAULT, JellyfishCommandPhase.DEFERRED);
   }

   /**
    * Used to help construct an {@code IUsage} object for a command that can only be run in the given phases.
    */
   protected static IParameter<?> phaseParameter(JellyfishCommandPhase phase, JellyfishCommandPhase... phases) {
      StringBuilder description = new StringBuilder(CommonParameters.PHASE.getDescription());
      description.append(phase.toString().toLowerCase());
      if (phases != null) {
         for (JellyfishCommandPhase p : phases) {
            description.append(", ").append(p.toString().toLowerCase());
         }
      }

      DefaultParameter<?> p = new DefaultParameter<>(CommonParameters.PHASE.getName()).advanced();
      p.setDescription(description.toString());
      return p;
   }

   private JellyfishCommandPhase getPhase() {
      JellyfishCommandPhase phase = JellyfishCommandPhase.DEFAULT;

      IParameter<?> param = getOptions().getParameters().getParameter(CommonParameters.PHASE.getName());
      if (param != null) {
         try {
            phase = JellyfishCommandPhase.valueOf(param.getStringValue().toUpperCase());
         } catch (IllegalArgumentException e) {
            throw new CommandException(param.getStringValue() + " is not a valid phase!");
         }
      }

      return phase;
   }
}
