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
package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.IConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.admin.RestAdminSystemTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.admin.RestAdminTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.io.MulticastIOTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.io.RestIOTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.io.ZeroMqIOTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.readiness.ConnectorReadinessPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.readiness.IReadinessPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.readiness.PubSubBridgeReadinessPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.readiness.ReadinessConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.telemetry.RestTelemetryConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.telemetry.RestTelemetrySystemTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.telemetry.RestTelemetryTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.telemetryreporting.RestTelemetryReportingConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.telemetryreporting.RestTelemetryReportingTopicPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.ITransportProviderConfigurationDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.ITransportProviderConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.httpclient.HttpClientTransportProviderPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.multicast.MulticastTransportProviderPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.spark.SparkTransportProviderPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportprovider.zeromq.ZeroMqTcpTransportProviderPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transportservice.TransportServiceConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.ITransportTopicConfigurationPlugin;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.multicast.MulticastConfigurationDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.rest.RestConfigurationDto;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.transporttopic.zeromq.ZeroMqTcpConfigurationDto;

import java.util.List;

import static java.util.Arrays.asList;

public class DefaultConfigurationPluginModule extends AbstractModule {

   @Override
   protected void configure() {
      configurePlugins();
      configureTransportProviderPlugins();
      configureTransportTopicPlugins();
      configureReadinessPlugins();
   }

   private void configurePlugins() {
      List<Class<? extends IConfigurationPlugin>> plugins = asList(
               RestAdminTopicPlugin.class,
               RestAdminSystemTopicPlugin.class,
               MulticastIOTopicPlugin.class,
               RestIOTopicPlugin.class,
               ZeroMqIOTopicPlugin.class,
               ReadinessConfigurationPlugin.class,
               RestTelemetryConfigurationPlugin.class,
               RestTelemetrySystemTopicPlugin.class,
               RestTelemetryTopicPlugin.class,
               RestTelemetryReportingConfigurationPlugin.class,
               RestTelemetryReportingTopicPlugin.class,
               HttpClientTransportProviderPlugin.class,
               MulticastTransportProviderPlugin.class,
               SparkTransportProviderPlugin.class,
               ZeroMqTcpTransportProviderPlugin.class,
               TransportServiceConfigurationPlugin.class);
      Multibinder<IConfigurationPlugin> binder = Multibinder.newSetBinder(binder(), IConfigurationPlugin.class);
      for (Class<? extends IConfigurationPlugin> plugin : plugins) {
         binder.addBinding().to(plugin);
      }
   }

   private void configureTransportProviderPlugins() {
      List<Class<
               ? extends ITransportProviderConfigurationPlugin<? extends ITransportProviderConfigurationDto>>> plugins =
                        asList(
                                 HttpClientTransportProviderPlugin.class,
                                 MulticastTransportProviderPlugin.class,
                                 SparkTransportProviderPlugin.class,
                                 ZeroMqTcpTransportProviderPlugin.class);
      Multibinder<ITransportProviderConfigurationPlugin<? extends ITransportProviderConfigurationDto>> binder =
               Multibinder.newSetBinder(binder(), new TypeLiteral<
                        ITransportProviderConfigurationPlugin<? extends ITransportProviderConfigurationDto>>() {
               });
      for (Class<? extends ITransportProviderConfigurationPlugin<
               ? extends ITransportProviderConfigurationDto>> plugin : plugins) {
         binder.addBinding().to(plugin);
      }
   }

   private void configureTransportTopicPlugins() {
      List<Class<? extends ITransportTopicConfigurationPlugin<MulticastConfigurationDto>>> multicast = asList(
               MulticastIOTopicPlugin.class);
      List<Class<? extends ITransportTopicConfigurationPlugin<ZeroMqTcpConfigurationDto>>> zeromqTcp = asList(
               ZeroMqIOTopicPlugin.class);
      List<Class<? extends ITransportTopicConfigurationPlugin<RestConfigurationDto>>> rest = asList(
               RestAdminTopicPlugin.class,
               RestAdminSystemTopicPlugin.class,
               RestIOTopicPlugin.class,
               RestTelemetrySystemTopicPlugin.class,
               RestTelemetryTopicPlugin.class,
               RestTelemetryReportingTopicPlugin.class);

      configureTransportTopicPlugins(new TypeLiteral<ITransportTopicConfigurationPlugin<MulticastConfigurationDto>>() {
      }, multicast);
      configureTransportTopicPlugins(new TypeLiteral<ITransportTopicConfigurationPlugin<RestConfigurationDto>>() {
      }, rest);
      configureTransportTopicPlugins(new TypeLiteral<ITransportTopicConfigurationPlugin<ZeroMqTcpConfigurationDto>>() {
      }, zeromqTcp);

   }

   private <T, U extends ITransportTopicConfigurationPlugin<T>> void
            configureTransportTopicPlugins(TypeLiteral<U> literal, List<Class<? extends U>> plugins) {
      Multibinder<U> binder = Multibinder.newSetBinder(binder(), literal);

      for (Class<? extends U> plugin : plugins) {
         binder.addBinding().to(plugin);
      }
   }

   private void configureReadinessPlugins() {
      List<Class<? extends IReadinessPlugin>> plugins = asList(
               ConnectorReadinessPlugin.class, PubSubBridgeReadinessPlugin.class,
               RestTelemetryConfigurationPlugin.class, RestTelemetryReportingConfigurationPlugin.class);

      Multibinder<IReadinessPlugin> binder = Multibinder.newSetBinder(binder(), IReadinessPlugin.class);
      for (Class<? extends IReadinessPlugin> plugin : plugins) {
         binder.addBinding().to(plugin);
      }
   }

}
