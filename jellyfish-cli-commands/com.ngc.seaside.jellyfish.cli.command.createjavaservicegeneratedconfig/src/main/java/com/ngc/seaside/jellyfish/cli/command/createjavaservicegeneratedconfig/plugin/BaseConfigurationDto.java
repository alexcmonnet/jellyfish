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
package com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin;

import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.utils.DefaultImportManager;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.utils.DefaultVariableManager;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.utils.IImportManager;
import com.ngc.seaside.jellyfish.cli.command.createjavaservicegeneratedconfig.plugin.utils.IVariableManager;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;

public abstract class BaseConfigurationDto {

   private final ConfigurationContext context;
   private final IImportManager imports;
   private final IVariableManager variables;

   /**
    * Constructor.
    * 
    * @param context context
    */
   public BaseConfigurationDto(ConfigurationContext context) {
      this.context = context;
      this.imports = new DefaultImportManager();
      this.variables = new DefaultVariableManager();
   }

   public String getProjectDirectoryName() {
      return context.getProjectInformation().getDirectoryName();
   }

   public String getBasePackage() {
      return context.getBasePackage();
   }

   public IModel getModel() {
      return context.getModel();
   }

   public ConfigurationContext getContext() {
      return context;
   }

   public IImportManager getImports() {
      return imports;
   }

   public IVariableManager getVariables() {
      return variables;
   }

}
