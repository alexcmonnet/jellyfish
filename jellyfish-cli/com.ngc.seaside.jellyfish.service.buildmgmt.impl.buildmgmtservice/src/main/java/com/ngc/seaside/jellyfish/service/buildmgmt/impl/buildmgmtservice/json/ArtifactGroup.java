package com.ngc.seaside.jellyfish.service.buildmgmt.impl.buildmgmtservice.json;

import com.ngc.seaside.jellyfish.service.buildmgmt.api.DependencyType;

import java.util.List;
import java.util.Objects;

public class ArtifactGroup {

   private String versionPropertyName;
   private String version;
   private DependencyType type;
   private List<DependencyArtifact> artifacts;

   public String getVersionPropertyName() {
      return versionPropertyName;
   }

   public ArtifactGroup setVersionPropertyName(String versionPropertyName) {
      this.versionPropertyName = versionPropertyName;
      return this;
   }

   public String getVersion() {
      return version;
   }

   public ArtifactGroup setVersion(String version) {
      this.version = version;
      return this;
   }

   public DependencyType getType() {
      return type;
   }

   public ArtifactGroup setType(DependencyType type) {
      this.type = type;
      return this;
   }

   public List<DependencyArtifact> getArtifacts() {
      return artifacts;
   }

   public ArtifactGroup setArtifacts(List<DependencyArtifact> artifacts) {
      this.artifacts = artifacts;
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof ArtifactGroup)) {
         return false;
      }
      ArtifactGroup that = (ArtifactGroup) o;
      return Objects.equals(versionPropertyName, that.versionPropertyName) &&
             Objects.equals(version, that.version) &&
             type == that.type &&
             Objects.equals(artifacts, that.artifacts);
   }

   @Override
   public int hashCode() {
      return Objects.hash(versionPropertyName, version, type, artifacts);
   }
}
