package com.ngc.seaside.bootstrap.service.template.api;

import com.ngc.seaside.command.api.IParameterCollection;

import java.nio.file.Path;

/**
 * This interface is intended to provide a way to unpack a zip file with the contents of a project. This class does
 * not and should not require knowledge of the type of project being created.
 *
 * The zip file structure should contain a template.properties file at the top level.
 * It should contain the property and it's default for any items within the template that require user
 * input.
 */
public interface ITemplateService {

   /**
    * Determine if the template exist by the given name.
    *
    * @param templateName the name of the template.
    * @return true if the template exists.
    */
   boolean templateExists(String templateName);

   /**
    * Unpack the given template to the output directory.
    *
    * @param templateName    the name of the template.
    * @param outputDirectory the directory to unpack the template.
    * @param parameters      the parameters that should take the place of any parameters located
    *                        in the template.properties file.
    * @param clean           whether or not to recursively delete already existing folder before creating them again
    * @return The Path to the unpacked template directory.
    * @throws TemplateServiceException if any errors occur when parsing or unpacking the template.
    */
   ITemplateOutput unpack(String templateName, IParameterCollection parameters, Path outputDirectory, boolean clean)
            throws TemplateServiceException;
}
