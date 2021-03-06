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
package com.ngc.seaside.jellyfish.cli.command.test.service.config;

import com.ngc.seaside.jellyfish.service.config.api.IAdministrationConfigurationService;
import com.ngc.seaside.jellyfish.service.config.api.ITelemetryConfigurationService;
import com.ngc.seaside.jellyfish.service.config.api.dto.HttpMethod;
import com.ngc.seaside.jellyfish.service.config.api.dto.NetworkAddress;
import com.ngc.seaside.jellyfish.service.config.api.dto.NetworkInterface;
import com.ngc.seaside.jellyfish.service.config.api.dto.RestConfiguration;
import com.ngc.seaside.jellyfish.service.config.api.dto.admin.AdministrationConfiguration;
import com.ngc.seaside.jellyfish.service.config.api.dto.admin.RestAdministrationConfiguration;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

/**
 * A basic implementation of {@link ITelemetryConfigurationService} for tests.
 */
public class MockedAdminstrationConfigurationService extends
         MockedModelPropertyConfigurationService<AdministrationConfiguration>
         implements IAdministrationConfigurationService {

   /**
    * Creates a rest administration configuration.
    * 
    * @param model model
    * @param address the address
    * @param interfaceName the interface name
    * @param port the port number
    * @param shutdownPath the shutdown path
    * @param restartPath the restart path
    * @param contentType the content type
    * @param httpMethod the http method
    * @return a configured RestAdministrationConfiguration
    */
   public RestAdministrationConfiguration addRestAdministrationConfiguration(IModel model, String address,
            String interfaceName, int port, String shutdownPath, String restartPath,
            String contentType, HttpMethod httpMethod) {
      RestConfiguration shutdownConfiguration =
               new RestConfiguration().setNetworkAddress(new NetworkAddress().setAddress(address))
                        .setNetworkInterface(new NetworkInterface().setName(interfaceName))
                        .setPort(port)
                        .setPath(shutdownPath)
                        .setContentType(contentType)
                        .setHttpMethod(httpMethod);
      RestConfiguration restartConfiguration =
               new RestConfiguration().setNetworkAddress(new NetworkAddress().setAddress(address))
                        .setNetworkInterface(new NetworkInterface().setName(interfaceName))
                        .setPort(port)
                        .setPath(restartPath)
                        .setContentType(contentType)
                        .setHttpMethod(httpMethod);
      RestAdministrationConfiguration configuration = new RestAdministrationConfiguration();
      configuration.setShutdown(shutdownConfiguration);
      configuration.setRestart(restartConfiguration);
      return addConfiguration(model, configuration);
   }

}
