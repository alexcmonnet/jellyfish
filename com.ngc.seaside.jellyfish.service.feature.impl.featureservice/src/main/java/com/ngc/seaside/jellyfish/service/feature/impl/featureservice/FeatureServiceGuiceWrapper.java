package com.ngc.seaside.jellyfish.service.feature.impl.featureservice;

import com.google.inject.Inject;
import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.jellyfish.service.feature.api.IFeatureInformation;
import com.ngc.seaside.jellyfish.service.feature.api.IFeatureService;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

import java.nio.file.Path;
import java.util.Collection;
import java.util.TreeMap;

public class FeatureServiceGuiceWrapper implements IFeatureService {

   private final FeatureService delegate;
   
   @Inject
   public FeatureServiceGuiceWrapper(ILogService logService) {
      delegate = new FeatureService();
      delegate.setLogService(logService);
   }

   @Override
   public IFeatureInformation getFeatureInfo(Path sdPath, IModel model) {
      return delegate.getFeatureInfo(sdPath, model);
   }

   @Override
   public TreeMap<String, IFeatureInformation> getFeatures(Path sdPath, IModel model) {
      return delegate.getFeatures(sdPath, model);
   }

   @Override
   public TreeMap<String, IFeatureInformation> getAllFeatures(Path sdPath, Collection<IModel> models) {
      return delegate.getAllFeatures(sdPath, models);
   }
}
