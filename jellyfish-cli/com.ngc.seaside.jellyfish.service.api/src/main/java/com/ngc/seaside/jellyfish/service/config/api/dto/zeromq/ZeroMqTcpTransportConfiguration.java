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
package com.ngc.seaside.jellyfish.service.config.api.dto.zeromq;

import com.google.common.base.Objects;

import com.ngc.seaside.jellyfish.service.config.api.dto.NetworkAddress;
import com.ngc.seaside.jellyfish.service.config.api.dto.NetworkInterface;

/**
 * Defines the configuration for a link that should use ZeroMQ's TCP transport mechanism. This is the most common
 * ZeroMQ transport mechanism.
 */
public class ZeroMqTcpTransportConfiguration extends ZeroMqConfiguration {

   private NetworkInterface bindConfiguration;
   private NetworkAddress connectConfiguration;
   private int port;

   /**
    * Defines the configuration for the component that acts as the server for the link. This may be the target or the
    * source depending on the connection type of the link.
    */
   public NetworkInterface getBindConfiguration() {
      return bindConfiguration;
   }

   public ZeroMqTcpTransportConfiguration setBindConfiguration(NetworkInterface bindConfiguration) {
      this.bindConfiguration = bindConfiguration;
      return this;
   }

   /**
    * Defines the configuration for the component that acts as the client for the link. This may be the target or the
    * source depending on the connection type of the link.
    */
   public NetworkAddress getConnectConfiguration() {
      return connectConfiguration;
   }

   public ZeroMqTcpTransportConfiguration setConnectConfiguration(NetworkAddress connectConfiguration) {
      this.connectConfiguration = connectConfiguration;
      return this;
   }

   /**
    * The port number.
    */
   public int getPort() {
      return port;
   }

   public ZeroMqTcpTransportConfiguration setPort(int port) {
      this.port = port;
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ZeroMqTcpTransportConfiguration)) {
         return false;
      }
      ZeroMqTcpTransportConfiguration that = (ZeroMqTcpTransportConfiguration) o;
      return Objects.equal(this.getConnectionType(), that.getConnectionType())
            && Objects.equal(this.getBindConfiguration(), that.getBindConfiguration())
            && Objects.equal(this.getConnectConfiguration(), that.getConnectConfiguration())
            && Objects.equal(this.getPort(), that.getPort());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.getConnectionType(),
                              this.getBindConfiguration(),
                              this.getConnectConfiguration(),
                              this.getPort());
   }

   @Override
   public String toString() {
      return "ZeroMqTcpTransportConfiguration[connectionType=" + this.getConnectionType()
            + ",bindInterface=" + this.getBindConfiguration().getName()
            + ",connectionAddress=" + this.getConnectConfiguration().getAddress()
            + ",port=" + this.getPort()
            + "]";
   }
}
