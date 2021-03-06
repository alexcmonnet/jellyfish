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
package com.ngc.seaside.jellyfish.cli.command.help;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.ngc.seaside.jellyfish.api.DefaultParameter;
import com.ngc.seaside.jellyfish.api.DefaultParameterCollection;
import com.ngc.seaside.jellyfish.api.DefaultUsage;
import com.ngc.seaside.jellyfish.api.ICommandOptions;
import com.ngc.seaside.jellyfish.api.IJellyFishCommand;
import com.ngc.seaside.jellyfish.api.IJellyFishCommandProvider;
import com.ngc.seaside.jellyfish.api.IUsage;
import com.ngc.seaside.jellyfish.api.ParameterCategory;
import com.ngc.seaside.systemdescriptor.service.log.api.PrintStreamLogService;

@RunWith(MockitoJUnitRunner.class)
public class HelpCommandTest {

   private static final IUsage USAGE_1 = new DefaultUsage(
         "Usage 1",
         new DefaultParameter<>("param1_1")
               .setDescription("Description 1_1")
               .setParameterCategory(ParameterCategory.REQUIRED),
         new DefaultParameter<>("param1_2")
               .setDescription("Description 1_2")
               .setParameterCategory(ParameterCategory.OPTIONAL),
         new DefaultParameter<>("param1_3")
               .setDescription("Description 1_3")
               .setParameterCategory(ParameterCategory.ADVANCED));

   private static final IUsage USAGE_2 = new DefaultUsage(
         "Usage 2",
         new DefaultParameter<>("param2_1")
               .setDescription("Description 2_1")
               .setParameterCategory(ParameterCategory.REQUIRED),
         new DefaultParameter<>("param2_2")
               .setDescription("Description 2_2")
               .setParameterCategory(ParameterCategory.OPTIONAL),
         new DefaultParameter<>("param2_3")
               .setDescription("Description 2_3")
               .setParameterCategory(ParameterCategory.ADVANCED));

   private HelpCommand cmd;

   @Mock
   private ICommandOptions options;
   
   @Mock(answer = Answers.RETURNS_DEEP_STUBS)
   private IJellyFishCommandProvider jellyfishProvider;

   private DefaultParameterCollection parameters = new DefaultParameterCollection();

   private ByteArrayOutputStream stream = new ByteArrayOutputStream();

   @Before
   public void before() {
      when(jellyfishProvider.getUsage().getAllParameters())
         .thenReturn(new DefaultParameterCollection().getAllParameters());

      cmd = new HelpCommand();
      cmd.setLogService(new PrintStreamLogService(new PrintStream(stream)));
      cmd.addCommand(cmd);
      cmd.setJellyfishProvider(jellyfishProvider);
      cmd.activate();

      IJellyFishCommand mock1 = mock(IJellyFishCommand.class);
      when(mock1.getName()).thenReturn("Command1");
      when(mock1.getUsage()).thenReturn(USAGE_1);

      IJellyFishCommand mock2 = mock(IJellyFishCommand.class);
      when(mock2.getName()).thenReturn("Command2");
      when(mock2.getUsage()).thenReturn(USAGE_2);

      cmd.addCommand(mock1);
      cmd.addCommand(mock2);

      when(options.getParameters()).thenReturn(parameters);
   }

   @Test
   public void testBasicRun() {
      cmd.run(options);

      String output = stream.toString();

      assertTrue(output.contains("help"));
      assertTrue(output.contains("Command1"));
      assertTrue(output.contains("Command2"));
      assertTrue(output.contains("Usage 1"));
      assertTrue(output.contains("Usage 2"));

      assertFalse(output.contains("Description"));
   }

   @Test
   public void testVerboseRun() {
      parameters.addParameter(new DefaultParameter<>("verbose", "true"));

      cmd.run(options);

      String output = stream.toString();

      assertTrue(output.contains("help"));
      assertTrue(output.contains("Command1"));
      assertTrue(output.contains("Command2"));
      assertTrue(output.contains("Usage 1"));
      assertTrue(output.contains("Usage 2"));

      assertTrue(output.contains("param1_1"));
      assertTrue(output.contains("param1_2"));
      assertTrue(output.contains("param2_1"));
      assertTrue(output.contains("param2_2"));
      assertTrue(output.contains("Description 1_1"));
      assertTrue(output.contains("Description 1_2"));
      assertTrue(output.contains("Description 2_1"));
      assertTrue(output.contains("Description 2_2"));
      assertFalse(output.contains("Description 1_3"));
      assertFalse(output.contains("Description 2_3"));
   }

   @Test
   public void testCommandRun() {
      parameters.addParameter(new DefaultParameter<>("command", "Command1"));

      cmd.run(options);

      String output = stream.toString();

      assertTrue(output.contains("Command1"));
      assertFalse(output.contains("Command2"));
      assertTrue(output.contains("Usage 1"));
      assertFalse(output.contains("Usage 2"));

      assertTrue(output.contains("param1_1"));
      assertTrue(output.contains("param1_2"));
      assertTrue(output.contains("Description 1_1"));
      assertTrue(output.contains("Description 1_2"));
      assertFalse(output.contains("Description 2_1"));
      assertFalse(output.contains("Description 2_2"));
      assertFalse(output.contains("Description 1_3"));
      assertFalse(output.contains("Description 2_3"));
   }

   @Test
   public void testAdvancedRun() {
      parameters.addParameter(new DefaultParameter<>("command", "Command1"));
      parameters.addParameter(new DefaultParameter<>("advanced", "true"));
      cmd.run(options);

      String output = stream.toString();

      assertTrue(output.contains("Command1"));
      assertFalse(output.contains("Command2"));
      assertTrue(output.contains("Usage 1"));
      assertFalse(output.contains("Usage 2"));

      assertTrue(output.contains("param1_1"));
      assertTrue(output.contains("param1_2"));
      assertTrue(output.contains("Description 1_1"));
      assertTrue(output.contains("Description 1_2"));
      assertFalse(output.contains("Description 2_1"));
      assertFalse(output.contains("Description 2_2"));
      assertTrue(output.contains("Description 1_3"));
      assertFalse(output.contains("Description 2_3"));
   }

   @After
   public void after() {
      cmd.deactivate();
   }

}
