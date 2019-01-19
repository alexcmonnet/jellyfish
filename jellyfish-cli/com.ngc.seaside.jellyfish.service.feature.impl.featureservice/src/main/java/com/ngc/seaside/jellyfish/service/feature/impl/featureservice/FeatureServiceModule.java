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
package com.ngc.seaside.jellyfish.service.feature.impl.featureservice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.ngc.seaside.jellyfish.service.feature.api.IFeatureService;

public class FeatureServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(IFeatureService.class).to(FeatureServiceGuiceWrapper.class)
                                      .in(Singleton.class);
   }

}
