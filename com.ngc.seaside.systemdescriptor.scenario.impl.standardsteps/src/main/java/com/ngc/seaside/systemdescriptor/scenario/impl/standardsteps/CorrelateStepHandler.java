package com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps;

import com.google.common.base.Preconditions;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenarioStep;
import com.ngc.seaside.systemdescriptor.scenario.api.AbstractStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.api.ScenarioStepVerb;
import com.ngc.seaside.systemdescriptor.validation.api.IValidationContext;
import com.ngc.seaside.systemdescriptor.validation.api.Severity;

import java.util.List;

/**
 * Implements the "correlate" verb which is used to indicate multiple pieces of data must be correlated together.
 * It contains a number of arguments and its form is:
 *
 * <pre>
 *    {@code
 *     (correlating|willCorrelate) <inputField|outputField>.<dataField> to <inputField|outputField>.<dataField>
 *    }
 * </pre>
 */
public class CorrelateStepHandler extends AbstractStepHandler {
   public final static ScenarioStepVerb PRESENT = ScenarioStepVerb.presentTense("correlating");
   public final static ScenarioStepVerb FUTURE = ScenarioStepVerb.futureTense("willCorrelate");

   private String leftData;
   private String rightData;

   public CorrelateStepHandler() {
      register(PRESENT, FUTURE);
   }

   public String getLeftData() {
      return leftData;
   }

   public String getRightData() {
      return rightData;
   }

   @Override
   protected void doValidateStep(IValidationContext<IScenarioStep> context) {

      requireStepParameters(context, "The 'correlate' verb requires parameters!");

      IScenarioStep step = context.getObject();
      List<String> parameters = step.getParameters();
      if (parameters.size() != 3) {
         context.declare(Severity.ERROR,
            "Expected parameters of the form: within <number/double> <time unit>",
            step)
                .getKeyword();
      } else {

         leftData = getCorrelationArg(step, 0);
         validateToArgument(context, step, 1);
         rightData = getCorrelationArg(step, 2);

         // The first parameter should be an operand.
         // validateOperand(context, step, parameters.get(0));
         // The second parameter should be a number (double).
         // validateDuration(context, step, parameters.get(1));
         // The third parameter should be a time unit.
         // validateTimeUnit(context, step, parameters.get(2));
      }

   }

   private String getCorrelationArg(IScenarioStep step, int argPosition) {
      Preconditions.checkNotNull(step, "step may not be null!");
      String keyword = step.getKeyword();
      Preconditions.checkArgument(
         keyword.equals(PRESENT.getVerb())
            || keyword.equals(FUTURE.getVerb()),
         "the step cannot be processed by this handler!");
      return step.getParameters().get(argPosition);
   }

   private void validateToArgument(IValidationContext<IScenarioStep> context, IScenarioStep step, int argPosition) {
      Preconditions.checkNotNull(step, "step may not be null!");
      if (step.getParameters().get(argPosition) != "to") {
         declareOrThrowError(context,
            step,
            String.format("Expected parameter to be 'to'"));
      }
   
   }

   private static void declareOrThrowError(IValidationContext<IScenarioStep> context, 
                                           IScenarioStep step,
                                           String errMessage) {
      if (context != null) {
         context.declare(Severity.ERROR, errMessage, step).getKeyword();
      } else {
         throw new IllegalArgumentException(errMessage);
      }
   }
}
