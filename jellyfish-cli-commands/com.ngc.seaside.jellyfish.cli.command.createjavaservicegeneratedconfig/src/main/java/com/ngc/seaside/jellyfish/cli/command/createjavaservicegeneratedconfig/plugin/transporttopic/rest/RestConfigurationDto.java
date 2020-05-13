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
package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.rest;

import com.ngc.seaside.jellyfish.service.config.api.dto.RestConfiguration;

public class RestConfigurationDto {

   private final RestConfiguration configuration;
   private final boolean canSend;
   private final boolean canReceive;

   /**
    * Constructor.
    * 
    * @param configuration configuration
    * @param canSend whether or not providers can use this configuration to send messages
    * @param canReceive whether or not providers can use this configuration to receive messages
    */
   public RestConfigurationDto(RestConfiguration configuration, boolean canSend, boolean canReceive) {
      super();
      this.configuration = configuration;
      this.canSend = canSend;
      this.canReceive = canReceive;
   }

   public RestConfiguration getConfiguration() {
      return configuration;
   }

   public boolean canSend() {
      return canSend;
   }

   public boolean canReceive() {
      return canReceive;
   }

}
