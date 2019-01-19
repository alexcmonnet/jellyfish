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
package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of {@link ITransportTopicConfigurationDto}.
 */
public class DefaultTransportTopicConfigurationDto<T> implements ITransportTopicConfigurationDto<T> {

   private final T value;
   private final Set<TransportTopicDto> topics = new LinkedHashSet<>();

   public DefaultTransportTopicConfigurationDto(T value) {
      this.value = value;
   }

   public DefaultTransportTopicConfigurationDto<T> addTransportTopic(String type, String value) {
      return addTransportTopic(new TransportTopicDto(type, value));
   }
   
   public DefaultTransportTopicConfigurationDto<T> addTransportTopic(TransportTopicDto topic) {
      topics.add(topic);
      return this;
   }

   @Override
   public T getValue() {
      return value;
   }

   @Override
   public Set<TransportTopicDto> getTransportTopics() {
      return topics;
   }

}
