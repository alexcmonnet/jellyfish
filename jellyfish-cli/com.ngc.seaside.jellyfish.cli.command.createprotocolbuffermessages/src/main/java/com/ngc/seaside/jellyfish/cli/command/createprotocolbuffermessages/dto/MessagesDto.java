package com.ngc.seaside.jellyfish.cli.command.createprotocolbuffermessages.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessagesDto {

   private String projectName;
   private Set<String> exportedPackages;

   public String getProjectName() {
      return projectName;
   }

   public MessagesDto setProjectName(String projectName) {
      this.projectName = projectName;
      return this;
   }

   public Set<String> getExportedPackages() {
      return exportedPackages;
   }

   public MessagesDto setExportedPackages(Set<String> exportedPackages) {
      exportedPackages.removeIf(str -> {
         List<String> list = Arrays.asList(str.split("\\."));
         for (int n = 1; n < list.size(); n++) {
            String sub = list.subList(0, n).stream().collect(Collectors.joining("."));
            if (exportedPackages.contains(sub)) {
               return true;
            }
         }
         return false;
      });
      this.exportedPackages = exportedPackages;
      return this;
   }

}