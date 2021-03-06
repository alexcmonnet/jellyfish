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
package com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps;

import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IDataField;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenario;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenarioStep;
import com.ngc.seaside.systemdescriptor.model.impl.basic.model.scenario.ScenarioStep;
import com.ngc.seaside.systemdescriptor.scenario.api.VerbTense;
import com.ngc.seaside.systemdescriptor.test.systemdescriptor.ModelUtils;
import com.ngc.seaside.systemdescriptor.test.systemdescriptor.ModelUtils.PubSubModel;
import com.ngc.seaside.systemdescriptor.validation.api.IValidationContext;
import com.ngc.seaside.systemdescriptor.validation.api.Severity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CorrelateStepHandlerTest {

   private CorrelateStepHandler handler;

   private ScenarioStep step;

   @Mock
   private IValidationContext<IScenarioStep> context;

   @Before
   public void setup() throws Throwable {
      handler = new CorrelateStepHandler();
   }

   @Test
   public void testDoesRegisterVerbs() throws Throwable {

      assertEquals("did not register correct present tense!",
                   CorrelateStepHandler.PRESENT,
                   handler.getVerbs().get(VerbTense.PRESENT_TENSE));
      assertEquals("did not register correct future tense!",
                   CorrelateStepHandler.FUTURE,
                   handler.getVerbs().get(VerbTense.FUTURE_TENSE));
   }

   @Test
   public void testInvalidNumberOfArgs() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("a.foo", "to"));
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Result
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testInvalidLeftArg() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("foo", "to", "input1.intField2"));
      when(context.getObject()).thenReturn(step);

      // Method to test
      try {
         handler.doValidateStep(context);
         fail("Expected illegal argument exception.");
      } catch (IllegalArgumentException e) {
         // Expected.
      }

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("&*&(.foo", "to", "b.foo"));
      when(context.getObject()).thenReturn(step);

      // Method to test
      try {
         handler.doValidateStep(context);
         fail("Expected illegal argument exception.");
      } catch (IllegalArgumentException e) {
         // Expected.
      }

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("a.", "to", "b.foo"));
      when(context.getObject()).thenReturn(step);

      // Method to test
      try {
         handler.doValidateStep(context);
         fail("Expected illegal argument exception.");
      } catch (IllegalArgumentException e) {
         // Expected.
      }
   }

   @Test
   public void testInvalidRightArg() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("a.foo", "to", "asfd"));
      when(context.getObject()).thenReturn(step);

      // Method to test
      try {
         handler.doValidateStep(context);
         fail("Expected illegal argument exception.");
      } catch (IllegalArgumentException e) {
         // Expected.
      }

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("a.foo", "to", ".foo"));
      when(context.getObject()).thenReturn(step);

      // Method to test
      try {
         handler.doValidateStep(context);
         fail("Expected illegal argument exception.");
      } catch (IllegalArgumentException e) {
         // Expected.
      }
   }

   @Test
   public void testValidPresentInputToInput() throws Throwable {

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "input1.intField2"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData inputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType1");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(inputDataType1, null, "intField2", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addInput("input1", inputDataType1);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(context, never()).declare(eq(Severity.ERROR), anyString(), eq(step));
   }

   @Test
   public void testGetters() throws Throwable {

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "output0.stringField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "stringField0", DataTypes.STRING);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      //Methods to test
      IDataField leftResult = handler.getLeftData(step);
      IDataField rightResult = handler.getRightData(step);

      //Results
      assertEquals("Incorrect Data field returned", "intField0", leftResult.getName());
      assertEquals("Incorrect Data field returned", DataTypes.INT, leftResult.getType());

      assertEquals("Incorrect Data field returned", "stringField0", rightResult.getName());
      assertEquals("Incorrect Data field returned", DataTypes.STRING, rightResult.getType());
   }

   @Test
   public void testGettersWithNestedFields() throws Throwable {

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.complex0.intField0", "to", "output0.complex0.stringField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      IData complexInputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.ComplexInputDataType0");
      IData complexOutputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.ComplexOutputDataType0");

      ModelUtils.mockData(complexInputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(complexOutputDataType0, null, "stringField0", DataTypes.STRING);

      ModelUtils.mockData(inputDataType0, null, "complex0", complexInputDataType0);
      ModelUtils.mockData(outputDataType0, null, "complex0", complexOutputDataType0);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      //Methods to test
      IDataField leftResult = handler.getLeftData(step);
      IDataField rightResult = handler.getRightData(step);

      //Results
      assertEquals("Incorrect Data field returned", "intField0", leftResult.getName());
      assertEquals("Incorrect Data field returned", DataTypes.INT, leftResult.getType());

      assertEquals("Incorrect Data field returned", "stringField0", rightResult.getName());
      assertEquals("Incorrect Data field returned", DataTypes.STRING, rightResult.getType());
   }

   @Test
   public void testInvalidPresentInputToInputWrongType() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "input1.stringField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData inputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType1");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(inputDataType1, null, "stringField0", DataTypes.STRING);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addInput("input1", inputDataType1);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();

   }

   @Test
   public void testInvalidPresentInputToSameInputData() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "input0.intField1"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();

   }

   @Test
   public void testInvalidPresentInputToOutput() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "output0.intField2"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "intField2", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testInvalidPresentOutputToInput() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("output0.intField2", "to", "input0.intField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "intField2", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testInvalidPresentCorrelateSameField() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "input0.intField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep, times(2)).getKeyword();

   }

   @Test
   public void testValidPresentInputToInputSuper() throws Throwable {

      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.PRESENT.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.superSuperField0", "to", "input1.intField2"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData inputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType1");
      IData superDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.SuperDataType0");
      IData superSuperDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.SuperSuperDataType0");

      ModelUtils.mockData(superSuperDataType0, null, "superSuperField0", DataTypes.INT);
      ModelUtils.mockData(superDataType0, superSuperDataType0, "superField0", DataTypes.INT);

      ModelUtils.mockData(inputDataType0, superDataType0, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(inputDataType1, null, "intField2", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addInput("input1", inputDataType1);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(context, never()).declare(eq(Severity.ERROR), anyString(), eq(step));
   }

   @Test
   public void testValidFutureInputToOutput() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "output0.intField2"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "intField2", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(context, never()).declare(eq(Severity.ERROR), anyString(), eq(step));
   }

   @Test
   public void testValidFutureOutputToInput() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("output0.intField3", "to", "input0.intField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData inputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType1");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(inputDataType1, null, "intField2", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "intField3", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addInput("input1", inputDataType1);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(context, never()).declare(eq(Severity.ERROR), anyString(), eq(step));
   }

   @Test
   public void testInvalidFutureInputToInput() throws Throwable {
      // Setup
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "input1.intField2"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData inputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType1");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(inputDataType1, null, "intField2", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addInput("input1", inputDataType1);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testInvalidFutureOutputToOutput() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("output0.intField3", "to", "output1.intField5"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData inputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType1");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");
      IData outputDataType1 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType1");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(inputDataType1, null, "intField2", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "intField3", DataTypes.INT, "intField4", DataTypes.INT);
      ModelUtils.mockData(outputDataType1, null, "intField5", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addInput("input1", inputDataType1);
      model.addOutput("output0", outputDataType0);
      model.addOutput("output1", outputDataType1);

      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testInvalidFutureInputToOutputWrongType() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("input0.intField0", "to", "output0.floatField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "floatField0", DataTypes.FLOAT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testInvalidFutureOutputToInputWrongType() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());
      step.getParameters().addAll(Arrays.asList("output0.boolField0", "to", "input0.intField0"));

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "intField0", DataTypes.INT, "intField1", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "boolField0", DataTypes.BOOLEAN);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);
      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);
      when(context.getObject()).thenReturn(step);

      IScenarioStep mockedStep = mock(IScenarioStep.class);
      when(context.declare(eq(Severity.ERROR), anyString(), eq((IScenarioStep) step))).thenReturn(mockedStep);

      // Method to test
      handler.doValidateStep(context);

      // Results
      verify(mockedStep).getKeyword();
   }

   @Test
   public void testParameterCompletion() throws Throwable {
      step = new ScenarioStep();
      step.setKeyword(CorrelateStepHandler.FUTURE.getVerb());

      IData inputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.InputDataType0");
      IData outputDataType0 = ModelUtils.getMockNamedChild(IData.class, "test.OutputDataType0");

      ModelUtils.mockData(inputDataType0, null, "field0", DataTypes.INT);
      ModelUtils.mockData(outputDataType0, null, "field1", DataTypes.INT);

      PubSubModel model = new PubSubModel("com.ModelName");
      model.addInput("input0", inputDataType0);
      model.addOutput("output0", outputDataType0);
      IScenario scenarioParent = mock(IScenario.class);
      when(scenarioParent.getParent()).thenReturn(model);

      model.addScenario(scenarioParent);
      step.setParent(scenarioParent);

      Set<String> suggestions;

      step.getParameters().clear();
      step.getParameters().addAll(Arrays.asList("inpu", "to", "output0.field1"));
      suggestions = handler.getSuggestedParameterCompletions(step, CorrelateStepHandler.FUTURE, 0);
      assertEquals(Collections.singleton("input0"), suggestions);

      step.getParameters().clear();
      step.getParameters().addAll(Arrays.asList("input0.field0", "to", "o"));
      suggestions = handler.getSuggestedParameterCompletions(step, CorrelateStepHandler.FUTURE, 2);
      assertEquals(Collections.singleton("output0"), suggestions);

      step.getParameters().clear();
      step.getParameters().addAll(Arrays.asList("input0.f", "to", "output0.field1"));
      suggestions = handler.getSuggestedParameterCompletions(step, CorrelateStepHandler.FUTURE, 0);
      assertEquals(Collections.singleton("input0.field0"), suggestions);

      step.getParameters().clear();
      step.getParameters().addAll(Arrays.asList("input0.field0", "to", "output0."));
      suggestions = handler.getSuggestedParameterCompletions(step, CorrelateStepHandler.FUTURE, 2);
      assertEquals(Collections.singleton("output0.field1"), suggestions);
   }
}
