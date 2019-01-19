/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2019, Northrop Grumman Systems Corporation
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
package com.ngc.seaside.jellyfish.sonarqube.rule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractRuleTest {

   private TestableRule rule;

   @Mock
   private RulesDefinition.NewRepository repository;

   @Mock
   private RulesDefinition.NewRule newRuleInstance;

   @Test
   public void testDoesConfigureRule() {
      RuleKey key = RuleKey.of("repo", "key");
      when(repository.createRule(key.rule())).thenReturn(newRuleInstance);
      when(repository.key()).thenReturn(key.repository());

      rule = new TestableRule(key);
      rule.addRuleToRepository(repository);

      assertEquals("key not correct!",
                   key,
                   rule.getKey());
      assertEquals("configure not invoked with created rule!",
                   newRuleInstance,
                   rule.rule);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testDoesRequireRepoKeysToMatch() {
      RuleKey key = RuleKey.of("repo", "key");
      when(repository.key()).thenReturn("something else");

      rule = new TestableRule(key);
      rule.addRuleToRepository(repository);
   }

   private static class TestableRule extends AbstractRule {

      RulesDefinition.NewRule rule;

      protected TestableRule(RuleKey key) {
         super(key);
      }

      @Override
      protected void configure(RulesDefinition.NewRule rule) {
         this.rule = rule;
      }
   }
}
