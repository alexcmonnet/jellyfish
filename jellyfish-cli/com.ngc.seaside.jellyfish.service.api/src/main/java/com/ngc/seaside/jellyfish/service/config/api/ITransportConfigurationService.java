package com.ngc.seaside.jellyfish.service.config.api;

import com.ngc.seaside.jellyfish.api.IJellyFishCommandOptions;
import com.ngc.seaside.jellyfish.service.config.api.dto.MulticastConfiguration;
import com.ngc.seaside.jellyfish.service.config.api.dto.RestConfiguration;
import com.ngc.seaside.jellyfish.service.scenario.api.IMessagingFlow;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.util.Collection;
import java.util.Set;

public interface ITransportConfigurationService {
   String getTransportTopicName(IMessagingFlow flow, IDataReferenceField field);

   /**
    * Returns the transport configuration types used by the given deployment model.
    * 
    * @param options jellyfish options
    * @param deploymentModel deployment model
    * @return the transport configuration types used by the given deployment model
    */
   Set<TransportConfigurationType> getConfigurationTypes(IJellyFishCommandOptions options, IModel deploymentModel);

   /**
    * Returns the multicast configurations for the given field, or an empty collection if there are no multicast configurations for the field.
    * 
    * @param options jellyfish options
    * @param field field
    * @return the multicast configurations for the given field
    */
   Collection<MulticastConfiguration> getMulticastConfiguration(IJellyFishCommandOptions options,
            IDataReferenceField field);

   /**
    * Returns the rest configurations for the given field, or an empty collection if there are no rest configurations for the field.
    * 
    * @param options jellyfish options
    * @param field field
    * @return the rest configurations for the given field
    */
   Collection<RestConfiguration> getRestConfiguration(IJellyFishCommandOptions options, IDataReferenceField field);
}
