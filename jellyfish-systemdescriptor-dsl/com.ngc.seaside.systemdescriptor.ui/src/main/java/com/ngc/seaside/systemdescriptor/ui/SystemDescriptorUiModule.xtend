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
/*
 * generated by Xtext 2.14.0
 */
package com.ngc.seaside.systemdescriptor.ui

import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import com.google.inject.Binder
import com.ngc.seaside.systemdescriptor.ui.dynamic.DynamicModuleLoader

/**
 * Use this class to register components to be used within the Eclipse IDE.
 */
@FinalFieldsConstructor
class SystemDescriptorUiModule extends AbstractSystemDescriptorUiModule {

	/**
	 * Discovers any Guice modules that have been registered as Java service loaders and includes those
	 * modules in this module.
	 * @param binder the binder used to configure the modules 
	 */
	def configureAdditionalModules(Binder binder) {
		// Dynamically load any Guice modules that are available.
		new DynamicModuleLoader().loadModules().forEach(m|binder.install(m));
	}
}
