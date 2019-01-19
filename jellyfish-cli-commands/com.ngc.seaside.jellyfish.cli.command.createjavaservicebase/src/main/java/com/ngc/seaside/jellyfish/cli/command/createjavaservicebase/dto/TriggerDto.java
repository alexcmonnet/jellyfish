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
package com.ngc.seaside.jellyfish.cli.command.createjavaservicebase.dto;

import java.util.ArrayList;
import java.util.List;

public class TriggerDto {

   private String triggerType;
   private String correlationMethod;
   private String name;
   private String serviceFromStatus;
   private List<EventDto> eventProducers = new ArrayList<>();
   private List<CompletenessDto> completionStatements = new ArrayList<>();
   private List<InputDto> inputs = new ArrayList<>();
   private List<IOCorrelationDto> inputOutputCorrelations  = new ArrayList<>();
   private PublishDto output;

   public String getTriggerType() {
      return triggerType;
   }

   public TriggerDto setTriggerType(String triggerType) {
      this.triggerType = triggerType;
      return this;
   }

   public String getCorrelationMethod() {
      return correlationMethod;
   }

   public TriggerDto setCorrelationMethod(String correlationMethod) {
      this.correlationMethod = correlationMethod;
      return this;
   }

   public List<EventDto> getEventProducers() {
      return eventProducers;
   }

   public TriggerDto setEventProducers(List<EventDto> eventProducers) {
      this.eventProducers = eventProducers;
      return this;
   }

   public List<CompletenessDto> getCompletionStatements() {
      return completionStatements;
   }

   public TriggerDto setCompletionStatements(List<CompletenessDto> completionStatements) {
      this.completionStatements = completionStatements;
      return this;
   }
   
   public PublishDto getOutput() {
      return output;
   }

   public void setOutput(PublishDto output) {
      this.output = output; 
   }

   public List<InputDto> getInputs() {
      return inputs;
   }

   public TriggerDto setInputs(List<InputDto> inputs) {
      this.inputs = inputs;
      return this;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getServiceFromStatus() {
      return serviceFromStatus;
   }

   public TriggerDto setServiceFromStatus(String serviceFromStatus) {
      this.serviceFromStatus = serviceFromStatus;
      return this;
   }

   public List<IOCorrelationDto> getInputOutputCorrelations() {
      return inputOutputCorrelations;
   }

   public void setInputOutputCorrelations(List<IOCorrelationDto> inputOutputCorrelations) {
      this.inputOutputCorrelations = inputOutputCorrelations;
   }

   public static class CompletenessDto {

      private String input1Type;
      private String input2Type;
      private String input1GetterSnippet;
      private String input2GetterSnippet;
      private String outputSetterSnippet;

      public String getInput1Type() {
         return input1Type;
      }

      public CompletenessDto setInput1Type(String input1Type) {
         this.input1Type = input1Type;
         return this;
      }

      public String getInput2Type() {
         return input2Type;
      }

      public CompletenessDto setInput2Type(String input2Type) {
         this.input2Type = input2Type;
         return this;
      }

      public String getInput1GetterSnippet() {
         return input1GetterSnippet;
      }

      public CompletenessDto setInput1GetterSnippet(String input1GetterSnippet) {
         this.input1GetterSnippet = input1GetterSnippet;
         return this;
      }

      public String getInput2GetterSnippet() {
         return input2GetterSnippet;
      }

      public CompletenessDto setInput2GetterSnippet(String input2GetterSnippet) {
         this.input2GetterSnippet = input2GetterSnippet;
         return this;
      }

      public String getOutputSetterSnippet() {
         return outputSetterSnippet;
      }
      
      public CompletenessDto setOutputSetterSnippet(String outputSetterSnippet) {
         this.outputSetterSnippet = outputSetterSnippet;
         return this;
      }
   }

   public static class EventDto {

      private String type;
      private String getterSnippet;

      public String getType() {
         return type;
      }

      public EventDto setType(String type) {
         this.type = type;
         return this;
      }

      public String getGetterSnippet() {
         return getterSnippet;
      }

      public EventDto setGetterSnippet(String getterSnippet) {
         this.getterSnippet = getterSnippet;
         return this;
      }

   }

}
