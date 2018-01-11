package com.ngc.seaside.jellyfish.cli.command.createjavaserviceconfig.dto;

public class ServiceConfigDto {

   private String modelName;
   private String packageName;
   private String projectDirectoryName;
   private String baseProjectArtifactName;

   public String getModelName() {
      return modelName;
   }

   public ServiceConfigDto setModelName(String modelName) {
      this.modelName = modelName;
      return this;
   }

   public String getPackageName() {
      return packageName;
   }

   public ServiceConfigDto setPackageName(String packageName) {
      this.packageName = packageName;
      return this;
   }

   public String getBaseProjectArtifactName() {
      return baseProjectArtifactName;
   }

   public ServiceConfigDto setBaseProjectArtifactName(String baseProjectArtifactName) {
      this.baseProjectArtifactName = baseProjectArtifactName;
      return this;
   }

   public String getProjectDirectoryName() {
      return projectDirectoryName;
   }

   public ServiceConfigDto setProjectDirectoryName(String projectDirectoryName) {
      this.projectDirectoryName = projectDirectoryName;
      return this;
   }
}