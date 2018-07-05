package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic;

import com.google.common.base.CaseFormat;
import com.ngc.seaside.jellyfish.service.config.api.dto.RestConfiguration;

import java.util.Objects;
import java.util.Set;

/**
 * Transport topic configuration dto interface. A transport topic configuration dto has a value that is typically
 * associated with a system descriptor property type that describes a physical transport, such as
 * {@link RestConfiguration}. Applicable transport provider plugins can use this value to generate their own physical
 * topics and connect them to {@code ITransportTopics} using this dto.
 *
 * @param <T> value type for the configuration
 */
public interface ITransportTopicConfigurationDto<T> {

   /**
    * Returns the value of the topic configuration.
    * 
    * @return the value of the topic configuration
    */
   public T getValue();

   /**
    * Returns the set of transport topics associated with this configuration.
    * 
    * @return the set of transport topics associated with this configuration
    */
   public Set<TransportTopicDto> getTransportTopics();

   /**
    * Returns a unique variable name that can be used by a provider plugin to create a topic.
    * 
    * @return a unique variable name for the topic
    */
   default String getTopicVariableName() {
      Set<TransportTopicDto> transportTopics = getTransportTopics();
      String value;
      if (transportTopics.isEmpty()) {
         value = getValue().getClass().getSimpleName();
         value = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL).convert(value);
      } else {
         value = transportTopics.iterator().next().getValue();
         value = CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(value);
      }
      if (!value.endsWith("Topic")) {
         value += "Topic";
      }
      return value;
   }

   /**
    * Dto for a transport topic.
    */
   public static class TransportTopicDto {

      private final String type;
      private final String value;

      /**
       * Constructs a transport topic dto.
       * 
       * @param type the fully-qualified name of the type of the transport topic (e.g.
       *           {@code com.ngc.seaside.service.telemetry.api.ITelemetryService})
       * @param value the value of the transport topic (e.g. {@code TELEMETRY_REQUEST_TRANSPORT_TOPIC})
       */
      public TransportTopicDto(String type, String value) {
         this.type = type;
         this.value = value;
      }

      /**
       * Returns the fully-qualified name of the type of the transport topic (e.g.
       * {@code com.ngc.seaside.service.telemetry.api.ITelemetryService}).
       * 
       * @return the fully-qualified name of the type of the transport topic
       */
      public String getType() {
         return type;
      }

      /**
       * Returns the value of the transport topic (e.g. {@code TELEMETRY_REQUEST_TRANSPORT_TOPIC}).
       * 
       * @return the value of the transport topic
       */
      public String getValue() {
         return value;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof TransportTopicDto)) {
            return false;
         }
         TransportTopicDto that = (TransportTopicDto) o;
         return Objects.equals(this.type, that.type)
                  && Objects.equals(this.value, that.value);
      }

      @Override
      public int hashCode() {
         return Objects.hash(type, value);
      }
   }
}
